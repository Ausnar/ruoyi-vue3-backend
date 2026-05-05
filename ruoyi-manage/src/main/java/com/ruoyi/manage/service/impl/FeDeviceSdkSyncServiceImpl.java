package com.ruoyi.manage.service.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.manage.config.AzdapsProperties;
import com.ruoyi.manage.domain.FeApiConfigCompanyScope;
import com.ruoyi.manage.domain.FeCompanyDeptMapping;
import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.domain.FeExtinguisher;
import com.ruoyi.manage.domain.FeExtinguisherSensorBinding;
import com.ruoyi.manage.domain.FeFirePoint;
import com.ruoyi.manage.domain.FeGateway;
import com.ruoyi.manage.domain.FeSdkSyncLog;
import com.ruoyi.manage.domain.FeSensor;
import com.ruoyi.manage.domain.FeSensorHistory;
import com.ruoyi.manage.mapper.FeApiConfigCompanyScopeMapper;
import com.ruoyi.manage.mapper.FeCompanyDeptMappingMapper;
import com.ruoyi.manage.mapper.FeExternalCompanyMapper;
import com.ruoyi.manage.mapper.FeExtinguisherMapper;
import com.ruoyi.manage.mapper.FeExtinguisherSensorBindingMapper;
import com.ruoyi.manage.mapper.FeFirePointDeviceSnapshotMapper;
import com.ruoyi.manage.mapper.FeFirePointMapper;
import com.ruoyi.manage.mapper.FeGatewayMapper;
import com.ruoyi.manage.mapper.FeSdkSyncLogMapper;
import com.ruoyi.manage.mapper.FeSensorHistoryMapper;
import com.ruoyi.manage.mapper.FeSensorMapper;
import com.ruoyi.manage.service.IFeDeviceSdkSyncService;
import com.ruoyi.manage.service.IFeDeviceWarningScanService;
import com.ruoyi.system.domain.SysDeptApiConfig;
import com.ruoyi.system.service.ISysDeptApiConfigService;
import com.ruoyi.visit.service.IFeVisitPassiveEventService;
import com.ruoyi.visit.util.VisitGeoUtils;

@Service
public class FeDeviceSdkSyncServiceImpl implements IFeDeviceSdkSyncService
{
    private static final Logger log = LoggerFactory.getLogger(FeDeviceSdkSyncServiceImpl.class);
    private static final String STATUS_RUNNING = "running";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILED = "failed";
    private static final String STATUS_SYNCED = "synced";
    private static final String STATUS_OBSERVED = "observed";
    private static final String STATUS_PENDING_GATEWAY = "pending_gateway";
    private static final String STATUS_UNBOUND = "unbound";
    private static final String ACTIVE_YES = "1";
    private static final String ACTIVE_NO = "0";
    private static final String SOURCE_SDK = "sdk";
    private static final String SENSOR_STATUS_NORMAL = "0";
    private static final String SENSOR_STATUS_OFFLINE = "2";
    private static final String EXTINGUISHER_STATUS_NORMAL = "0";
    private static final String FIRE_POINT_STATUS_NORMAL = "0";
    private static final String GATEWAY_STATUS_ONLINE = "online";
    private static final String GATEWAY_STATUS_OFFLINE = "offline";
    private static final String SCOPE_FULL = "full";
    private static final String PATH_STATION = "/api/station/";
    private static final String PATH_TBOX = "/api/tbox/";
    private static final String PATH_GPS = "/api/gps/";
    private static final String PATH_SENSOR = "/api/sensor/";
    private static final String PATH_EXTINGUISHER = "/api/extinguisher/";
    private static final String PATH_COMPANY = "/api/company";
    private static final String PATH_SENSOR_VALUES = "/api/sensor/{sensor_id}";
    private static final String MESSAGE_SYNC_RUNNING = "Device sync is already running";

    @Autowired private AzdapsProperties azdapsProperties;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ISysDeptApiConfigService sysDeptApiConfigService;
    @Autowired private FeFirePointMapper feFirePointMapper;
    @Autowired private FeGatewayMapper feGatewayMapper;
    @Autowired private FeSensorMapper feSensorMapper;
    @Autowired private FeExtinguisherMapper feExtinguisherMapper;
    @Autowired private FeExtinguisherSensorBindingMapper feExtinguisherSensorBindingMapper;
    @Autowired private FeFirePointDeviceSnapshotMapper feFirePointDeviceSnapshotMapper;
    @Autowired private FeExternalCompanyMapper feExternalCompanyMapper;
    @Autowired private FeApiConfigCompanyScopeMapper feApiConfigCompanyScopeMapper;
    @Autowired private FeCompanyDeptMappingMapper feCompanyDeptMappingMapper;
    @Autowired private FeSdkSyncLogMapper feSdkSyncLogMapper;
    @Autowired private FeSensorHistoryMapper feSensorHistoryMapper;
    @Autowired private IFeDeviceWarningScanService feDeviceWarningScanService;
    @Autowired private IFeVisitPassiveEventService feVisitPassiveEventService;

    private volatile RestTemplate restTemplate;
    private final AtomicBoolean syncRunning = new AtomicBoolean(false);

    @Override
    public Map<String, Object> syncAllActiveConfigs(String operator)
    {
        if (!syncRunning.compareAndSet(false, true))
        {
            return buildBusyResult(null);
        }

        List<SysDeptApiConfig> configs = sysDeptApiConfigService.selectActiveSysDeptApiConfigs();
        try
        {
            List<Map<String, Object>> details = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;
            for (SysDeptApiConfig config : configs)
            {
                try
                {
                    Map<String, Object> detail = syncConfig(config, operator);
                    details.add(detail);
                    if (Boolean.TRUE.equals(detail.get("success"))) successCount++;
                    else failCount++;
                }
                catch (Exception e)
                {
                    Map<String, Object> detail = new LinkedHashMap<>();
                    detail.put("configId", config.getConfigId());
                    detail.put("deptId", config.getDeptId());
                    detail.put("success", false);
                    detail.put("message", e.getMessage());
                    details.add(detail);
                    failCount++;
                }
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", failCount == 0);
            result.put("message", failCount == 0 ? "Device sync completed" : "Device sync completed with failures");
            result.put("total", configs.size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("details", details);
            return result;
        }
        finally
        {
            syncRunning.set(false);
        }
    }

    @Override
    public Map<String, Object> syncByConfigId(Long configId, String operator)
    {
        if (!syncRunning.compareAndSet(false, true))
        {
            return buildBusyResult(configId);
        }

        try
        {
            SysDeptApiConfig config = sysDeptApiConfigService.selectSysDeptApiConfigByConfigId(configId);
            if (config == null)
            {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("configId", configId);
                result.put("success", false);
                result.put("message", "Dept API config does not exist");
                return result;
            }
            return syncConfig(config, operator);
        }
        finally
        {
            syncRunning.set(false);
        }
    }

    @Override
    public Map<String, Object> refreshExtinguisherProfile(Long extinguisherId, String operator)
    {
        FeExtinguisher extinguisher = feExtinguisherMapper.selectFeExtinguisherByExtinguisherId(extinguisherId);
        if (extinguisher == null)
        {
            throw new IllegalArgumentException("Extinguisher does not exist");
        }
        if (StringUtils.isBlank(extinguisher.getLabelCode()))
        {
            markProfileSyncFailed(extinguisher, "Missing label code", operator);
            return buildProfileResult(extinguisher, false, "Missing label code");
        }

        List<SysDeptApiConfig> configs = selectProfileRefreshConfigs(extinguisher);
        if (configs.isEmpty())
        {
            markProfileSyncFailed(extinguisher, "No active SDK credential is available", operator);
            return buildProfileResult(extinguisher, false, "No active SDK credential is available");
        }

        String lastMessage = null;
        for (SysDeptApiConfig config : configs)
        {
            try
            {
                validateConfig(config);
                TokenContext tokenContext = login(config);
                JsonNode profileNode = fetchExtinguisherProfileNode(extinguisher.getLabelCode(), tokenContext, config);
                applySdkProfile(extinguisher, profileNode, operator);
                feExtinguisherMapper.updateFeExtinguisher(extinguisher);
                return buildProfileResult(extinguisher, true, "Profile refreshed");
            }
            catch (Exception e)
            {
                lastMessage = e.getMessage();
                log.warn("[DeviceSync] refresh extinguisher profile failed, extinguisherId={}, labelCode={}, configId={}, message={}",
                    extinguisherId, extinguisher.getLabelCode(), config.getConfigId(), e.getMessage());
            }
        }

        markProfileSyncFailed(extinguisher, StringUtils.defaultIfBlank(lastMessage, "SDK profile refresh failed"), operator);
        return buildProfileResult(extinguisher, false, StringUtils.defaultIfBlank(lastMessage, "SDK profile refresh failed"));
    }

    private Map<String, Object> buildBusyResult(Long configId)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        if (configId != null)
        {
            result.put("configId", configId);
        }
        result.put("success", false);
        result.put("message", MESSAGE_SYNC_RUNNING);
        result.put("busy", true);
        if (configId == null)
        {
            result.put("total", 0);
            result.put("successCount", 0);
            result.put("failCount", 1);
            result.put("details", Collections.emptyList());
        }
        return result;
    }

    private Map<String, Object> syncConfig(SysDeptApiConfig config, String operator)
    {
        validateConfig(config);
        Date now = DateUtils.getNowDate();
        FeSdkSyncLog syncLog = buildRunningLog(config, operator, now);
        feSdkSyncLogMapper.insertFeSdkSyncLog(syncLog);
        SyncStats stats = new SyncStats();
        try
        {
            TokenContext tokenContext = login(config);
            syncLog.setTokenExpireTime(tokenContext.getTokenExpireTime());

            List<JsonNode> stationNodes = fetchPagedItems(PATH_STATION, tokenContext, config);
            List<JsonNode> tboxNodes = fetchPagedItems(PATH_TBOX, tokenContext, config);
            Map<Long, JsonNode> gpsNodeMap = buildGpsNodeMap(fetchPagedItems(PATH_GPS, tokenContext, config));
            List<JsonNode> sensorNodes = fetchPagedItems(PATH_SENSOR, tokenContext, config);
            List<JsonNode> extinguisherNodes = fetchPagedItems(PATH_EXTINGUISHER, tokenContext, config);
            List<JsonNode> companyNodes = fetchPagedItems(PATH_COMPANY, tokenContext, config);

            Set<Long> syncedCompanyIds = new TreeSet<>();
            syncObservedCompanies(companyNodes, config, operator, syncedCompanyIds);
            List<FeSensor> syncedSensors = new ArrayList<>();
            for (JsonNode stationNode : stationNodes)
            {
                FeFirePoint firePoint = upsertFirePoint(stationNode, config, operator);
                if (firePoint.getExternalCompanyId() != null) syncedCompanyIds.add(firePoint.getExternalCompanyId());
                stats.incrementSuccess("station");
            }
            for (JsonNode tboxNode : tboxNodes)
            {
                Long externalTboxId = readLong(tboxNode, "id");
                FeGateway gateway = upsertGateway(tboxNode, gpsNodeMap.get(externalTboxId), config, operator);
                if (gateway.getExternalCompanyId() != null) syncedCompanyIds.add(gateway.getExternalCompanyId());
                stats.incrementSuccess("tbox");
            }
            for (JsonNode sensorNode : sensorNodes)
            {
                FeSensor sensor = upsertSensor(sensorNode, config, operator);
                syncedSensors.add(sensor);
                FeGateway gateway = sensor.getGatewayId() == null ? null : feGatewayMapper.selectFeGatewayByGatewayId(sensor.getGatewayId());
                if (gateway != null && gateway.getExternalCompanyId() != null) syncedCompanyIds.add(gateway.getExternalCompanyId());
                stats.incrementSuccess("sensor");
            }
            for (JsonNode extinguisherNode : extinguisherNodes)
            {
                FeExtinguisher extinguisher = upsertExtinguisher(extinguisherNode, tokenContext, config, operator);
                Long externalCompanyId = readNestedLong(extinguisherNode, "company", "id");
                if (externalCompanyId != null) syncedCompanyIds.add(externalCompanyId);
                if (STATUS_UNBOUND.equals(extinguisher.getSyncStatus())) stats.incrementFail("binding");
                stats.incrementSuccess("extinguisher");
            }
            syncSensorValues(syncedSensors, tokenContext, config, stats);
            int snapshotCount = feFirePointDeviceSnapshotMapper.insertSnapshotsForSourceDept(
                config.getConfigId(), config.getDeptId(), DateUtils.getNowDate(), operator);
            stats.addInfo("firePointSnapshot", snapshotCount);
            runWarningScan(config.getDeptId(), operator, stats);

            syncLog.setExternalCompanyId(syncedCompanyIds.size() == 1 ? syncedCompanyIds.iterator().next() : null);
            syncLog.setSyncStatus(STATUS_SUCCESS);
            syncLog.setEndTime(DateUtils.getNowDate());
            syncLog.setSuccessCount(stats.getSuccessCount());
            syncLog.setFailCount(stats.getFailCount());
            syncLog.setMessage(stats.buildMessage());
            syncLog.setUpdateBy(operator);
            syncLog.setUpdateTime(DateUtils.getNowDate());
            feSdkSyncLogMapper.updateFeSdkSyncLog(syncLog);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("configId", config.getConfigId());
            result.put("deptId", config.getDeptId());
            result.put("success", true);
            result.put("message", "Device sync completed");
            result.put("stats", stats.toMap());
            return result;
        }
        catch (Exception e)
        {
            syncLog.setSyncStatus(STATUS_FAILED);
            syncLog.setEndTime(DateUtils.getNowDate());
            syncLog.setSuccessCount(stats.getSuccessCount());
            syncLog.setFailCount(stats.getFailCount() + 1);
            syncLog.setMessage(StringUtils.left(e.getMessage(), 1900));
            syncLog.setUpdateBy(operator);
            syncLog.setUpdateTime(DateUtils.getNowDate());
            feSdkSyncLogMapper.updateFeSdkSyncLog(syncLog);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("configId", config.getConfigId());
            result.put("deptId", config.getDeptId());
            result.put("success", false);
            result.put("message", e.getMessage());
            result.put("stats", stats.toMap());
            return result;
        }
    }

    private void runWarningScan(Long sourceDeptId, String operator, SyncStats stats)
    {
        try
        {
            Map<String, Object> scanResult = feDeviceWarningScanService.scanAfterSdkSync(sourceDeptId, operator);
            int suspectedFireCount = getInt(scanResult, "suspectedFireCount");
            int lowBatteryCount = getInt(scanResult, "lowBatteryCount");
            int lowPressureCount = getInt(scanResult, "lowPressureCount");
            int highPressureCount = getInt(scanResult, "highPressureCount");
            int insufficientExtinguisherCount = getInt(scanResult, "insufficientExtinguisherCount");
            int extinguisherExpiredCount = getInt(scanResult, "extinguisherExpiredCount");
            int abnormalTemperatureCount = getInt(scanResult, "abnormalTemperatureCount");

            stats.putInfo("warningSuspectedFire", suspectedFireCount);
            stats.putInfo("warningLowBattery", lowBatteryCount);
            stats.putInfo("warningLowPressure", lowPressureCount);
            stats.putInfo("warningHighPressure", highPressureCount);
            stats.putInfo("warningInsufficientExtinguisher", insufficientExtinguisherCount);
            stats.putInfo("warningExtinguisherExpired", extinguisherExpiredCount);
            stats.putInfo("warningAbnormalTemperature", abnormalTemperatureCount);
            stats.putInfo("warningScanSummary", buildWarningScanSummary(suspectedFireCount, lowBatteryCount,
                lowPressureCount, highPressureCount, insufficientExtinguisherCount, extinguisherExpiredCount,
                abnormalTemperatureCount));
            stats.incrementInfo("warningScanSuccess");
        }
        catch (Exception e)
        {
            log.warn("Device warning scan failed after SDK sync, sourceDeptId={}", sourceDeptId, e);
            stats.incrementInfo("warningScanFailed");
        }
    }

    private int getInt(Map<String, Object> source, String key)
    {
        Object value = source == null ? null : source.get(key);
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    private String buildWarningScanSummary(int suspectedFireCount, int lowBatteryCount, int lowPressureCount,
                                           int highPressureCount, int insufficientExtinguisherCount,
                                           int extinguisherExpiredCount, int abnormalTemperatureCount)
    {
        return String.format("预警扫描完成：疑似火灾%d条，低电量%d条，低压%d条，高压%d条，数量不足%d条，灭火器到期%d条，环境温度异常%d条",
            suspectedFireCount, lowBatteryCount, lowPressureCount, highPressureCount, insufficientExtinguisherCount,
            extinguisherExpiredCount, abnormalTemperatureCount);
    }

    private FeSdkSyncLog buildRunningLog(SysDeptApiConfig config, String operator, Date now)
    {
        FeSdkSyncLog syncLog = new FeSdkSyncLog();
        syncLog.setDeptId(config.getDeptId());
        syncLog.setSyncScope(SCOPE_FULL);
        syncLog.setSyncStatus(STATUS_RUNNING);
        syncLog.setStartTime(now);
        syncLog.setSuccessCount(0);
        syncLog.setFailCount(0);
        syncLog.setCreateBy(operator);
        syncLog.setCreateTime(now);
        syncLog.setUpdateBy(operator);
        syncLog.setUpdateTime(now);
        return syncLog;
    }

    private void validateConfig(SysDeptApiConfig config)
    {
        if (config == null) throw new IllegalArgumentException("Dept API config does not exist");
        if (!StringUtils.equals(config.getStatus(), "1")) throw new IllegalArgumentException("Dept API config is disabled");
        if (StringUtils.isBlank(config.getApiId()) || StringUtils.isBlank(config.getApiKey())) throw new IllegalArgumentException("Dept API credentials are incomplete");
        if (config.getExpireDate() != null && config.getExpireDate().before(DateUtils.getNowDate())) throw new IllegalArgumentException("Dept API credentials have expired");
    }

    private TokenContext login(SysDeptApiConfig config)
    {
        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("secret_id", config.getApiId());
        queryParams.put("secret_key", config.getApiKey());
        JsonNode root = requestJson(HttpMethod.POST, azdapsProperties.getLoginPath(), queryParams, null, config, false);
        String accessToken = readText(root, "access_token");
        if (StringUtils.isBlank(accessToken)) throw new IllegalStateException("SDK login response missing access_token");
        TokenContext tokenContext = new TokenContext();
        tokenContext.setAccessToken(accessToken);
        tokenContext.setRefreshToken(readText(root, "refresh_token"));
        return tokenContext;
    }

    private List<JsonNode> fetchPagedItems(String path, TokenContext tokenContext, SysDeptApiConfig config)
    {
        int limit = Math.max(1, azdapsProperties.getPageLimit());
        int offset = 0;
        List<JsonNode> items = new ArrayList<>();
        while (true)
        {
            Map<String, Object> queryParams = new LinkedHashMap<>();
            queryParams.put("limit", limit);
            queryParams.put("offset", offset);
            JsonNode root = requestJson(HttpMethod.GET, path, queryParams, tokenContext, config, true);
            if (root == null || root.isMissingNode() || root.isNull()) break;
            if (root.isArray())
            {
                root.forEach(items::add);
                break;
            }
            JsonNode itemArray = extractItemArray(root);
            if (itemArray == null) break;
            int batchSize = 0;
            for (JsonNode item : itemArray)
            {
                items.add(item);
                batchSize++;
            }
            Integer totalCount = readInteger(root, "count");
            if (totalCount != null && offset + batchSize >= totalCount) break;
            if (batchSize < limit) break;
            offset += batchSize;
        }
        return items;
    }

    private JsonNode extractItemArray(JsonNode root)
    {
        for (String field : new String[] { "items", "results", "data" })
        {
            JsonNode candidate = root.get(field);
            if (candidate != null && candidate.isArray()) return candidate;
        }
        return null;
    }

    private JsonNode fetchExtinguisherProfileNode(String labelCode, TokenContext tokenContext, SysDeptApiConfig config)
    {
        if (StringUtils.isBlank(labelCode))
        {
            throw new IllegalArgumentException("Missing label code");
        }
        JsonNode root = requestJson(HttpMethod.GET, PATH_EXTINGUISHER + encodePathSegment(labelCode), null, tokenContext, config, true);
        JsonNode payload = unwrapObjectPayload(root);
        if (payload == null || payload.isMissingNode() || payload.isNull())
        {
            throw new IllegalStateException("SDK profile response is empty");
        }
        return payload;
    }

    private JsonNode unwrapObjectPayload(JsonNode root)
    {
        if (root == null || root.isMissingNode() || root.isNull())
        {
            return null;
        }
        if (root.isObject())
        {
            for (String field : new String[] { "data", "result", "item" })
            {
                JsonNode candidate = root.get(field);
                if (candidate != null && candidate.isObject())
                {
                    return candidate;
                }
            }
            return root;
        }
        if (root.isArray() && root.size() > 0 && root.get(0).isObject())
        {
            return root.get(0);
        }
        return null;
    }

    private String encodePathSegment(String value)
    {
        return UriUtils.encodePathSegment(value, StandardCharsets.UTF_8);
    }

    private void applySdkProfile(FeExtinguisher extinguisher, JsonNode profileNode, String operator)
    {
        applySdkRawFields(extinguisher, profileNode);
        applySdkProfileMetadata(extinguisher, operator);
    }

    private void applySdkRawFields(FeExtinguisher extinguisher, JsonNode node)
    {
        if (extinguisher == null || node == null || node.isMissingNode() || node.isNull())
        {
            return;
        }
        Long externalExtinguisherId = readLong(node, "id");
        if (externalExtinguisherId != null)
        {
            extinguisher.setExternalExtinguisherId(externalExtinguisherId);
        }
        String labelCode = readFirstText(node, "gb_number", "label_code", "labelCode", "gbNumber");
        if (StringUtils.isNotBlank(labelCode))
        {
            extinguisher.setLabelCode(labelCode);
        }
        String specification = readFirstText(node, "model_display", "specification", "spec", "model_name", "model");
        if (StringUtils.isNotBlank(specification))
        {
            extinguisher.setSpecification(specification);
        }
        String productName = readFirstText(node, "product_name", "productName", "model", "name");
        if (StringUtils.isNotBlank(productName))
        {
            extinguisher.setProductName(productName);
        }
        String manufacturer = StringUtils.defaultIfBlank(readNestedText(node, "factory", "name"),
            readFirstText(node, "factory_name", "manufacturer"));
        if (StringUtils.isNotBlank(manufacturer))
        {
            extinguisher.setManufacturer(manufacturer);
        }
        String serviceProvider = StringUtils.defaultIfBlank(readNestedText(node, "serve", "name"),
            readFirstText(node, "service_provider", "serviceProvider"));
        if (StringUtils.isNotBlank(serviceProvider))
        {
            extinguisher.setServiceProvider(serviceProvider);
        }
        Date productionDate = readFirstDate(node, "made_time", "production_date", "productionDate", "made_date", "manufacture_date");
        if (productionDate != null)
        {
            extinguisher.setProductionDate(productionDate);
        }
    }

    private void applySdkProfileMetadata(FeExtinguisher extinguisher, String operator)
    {
        Date now = DateUtils.getNowDate();
        FeExtinguisherBusinessFieldUtils.completeWarningBusinessFields(extinguisher, true);
        String profileStatus = FeExtinguisherBusinessFieldUtils.resolveProfileSyncStatus(extinguisher);
        extinguisher.setProfileSource(StringUtils.isNotBlank(extinguisher.getTemperatureRange())
            ? FeExtinguisherBusinessFieldUtils.PROFILE_SOURCE_MIXED
            : FeExtinguisherBusinessFieldUtils.PROFILE_SOURCE_DERIVED);
        extinguisher.setProfileSyncStatus(profileStatus);
        extinguisher.setProfileSyncTime(now);
        extinguisher.setProfileSyncMessage(FeExtinguisherBusinessFieldUtils.PROFILE_SYNC_STATUS_SUCCESS.equals(profileStatus)
            ? "标志明码资料已同步并完成本地规则推导"
            : "标志明码资料已同步，但生产日期、灭火器类型或形式不完整");
        extinguisher.setUpdateBy(operator);
        extinguisher.setUpdateTime(now);
    }

    private void markProfileSyncFailed(FeExtinguisher extinguisher, String message, String operator)
    {
        if (extinguisher == null)
        {
            return;
        }
        extinguisher.setProfileSyncStatus(FeExtinguisherBusinessFieldUtils.PROFILE_SYNC_STATUS_FAILED);
        extinguisher.setProfileSyncTime(DateUtils.getNowDate());
        extinguisher.setProfileSyncMessage(StringUtils.left(message, 500));
        extinguisher.setUpdateBy(operator);
        extinguisher.setUpdateTime(DateUtils.getNowDate());
        feExtinguisherMapper.updateFeExtinguisher(extinguisher);
    }

    private Map<String, Object> buildProfileResult(FeExtinguisher extinguisher, boolean success, String message)
    {
        FeExtinguisher latest = extinguisher == null || extinguisher.getExtinguisherId() == null
            ? extinguisher : feExtinguisherMapper.selectFeExtinguisherByExtinguisherId(extinguisher.getExtinguisherId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", success);
        result.put("message", message);
        result.put("data", latest);
        return result;
    }

    private List<SysDeptApiConfig> selectProfileRefreshConfigs(FeExtinguisher extinguisher)
    {
        List<SysDeptApiConfig> activeConfigs = sysDeptApiConfigService.selectActiveSysDeptApiConfigs();
        if (activeConfigs == null || activeConfigs.isEmpty())
        {
            return Collections.emptyList();
        }
        List<SysDeptApiConfig> configs = new ArrayList<>();
        for (SysDeptApiConfig config : activeConfigs)
        {
            if (Objects.equals(config.getDeptId(), extinguisher.getSourceDeptId()))
            {
                configs.add(config);
            }
        }
        for (SysDeptApiConfig config : activeConfigs)
        {
            if (!configs.contains(config))
            {
                configs.add(config);
            }
        }
        return configs;
    }

    private JsonNode requestJson(HttpMethod method, String path, Map<String, Object> queryParams,
                                 TokenContext tokenContext, SysDeptApiConfig config, boolean allowRelogin)
    {
        try
        {
            return doRequestJson(method, path, queryParams, tokenContext);
        }
        catch (HttpStatusCodeException e)
        {
            if (allowRelogin && e.getStatusCode().value() == 401 && tokenContext != null)
            {
                TokenContext renewed = login(config);
                tokenContext.setAccessToken(renewed.getAccessToken());
                tokenContext.setRefreshToken(renewed.getRefreshToken());
                return doRequestJson(method, path, queryParams, tokenContext);
            }
            throw new IllegalStateException("SDK request failed: " + StringUtils.defaultIfBlank(e.getResponseBodyAsString(), e.getMessage()), e);
        }
    }
    private JsonNode doRequestJson(HttpMethod method, String path, Map<String, Object> queryParams, TokenContext tokenContext)
    {
        URI uri = buildUri(path, queryParams);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (tokenContext != null && StringUtils.isNotBlank(tokenContext.getAccessToken())) headers.setBearerAuth(tokenContext.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = getRestTemplate().exchange(uri, method, entity, String.class);
        String body = response.getBody();
        if (StringUtils.isBlank(body)) return objectMapper.createObjectNode();
        try
        {
            return objectMapper.readTree(body);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("SDK response body is not valid JSON", e);
        }
    }

    private URI buildUri(String path, Map<String, Object> queryParams)
    {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(azdapsProperties.getBaseUrl() + path);
        if (queryParams != null)
        {
            queryParams.forEach((key, value) -> {
                if (value != null && StringUtils.isNotBlank(String.valueOf(value))) builder.queryParam(key, value);
            });
        }
        return builder.build(true).toUri();
    }

    private RestTemplate getRestTemplate()
    {
        if (restTemplate == null)
        {
            synchronized (this)
            {
                if (restTemplate == null)
                {
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(azdapsProperties.getConnectTimeoutMs());
                    factory.setReadTimeout(azdapsProperties.getReadTimeoutMs());
                    restTemplate = new RestTemplate(factory);
                }
            }
        }
        return restTemplate;
    }

    private FeFirePoint upsertFirePoint(JsonNode stationNode, SysDeptApiConfig config, String operator)
    {
        Long externalStationId = readLong(stationNode, "id");
        String stationNumber = readText(stationNode, "number");
        FeFirePoint firePoint = externalStationId == null ? null : feFirePointMapper.selectByExternalStationId(externalStationId);
        if (firePoint == null && StringUtils.isNotBlank(stationNumber)) firePoint = feFirePointMapper.selectByStationNumber(stationNumber);
        boolean isNew = firePoint == null;
        if (isNew)
        {
            firePoint = new FeFirePoint();
            firePoint.setCreateBy(operator);
            firePoint.setCreateTime(DateUtils.getNowDate());
            firePoint.setDelFlag("0");
        }

        Long externalCompanyId = readLong(stationNode, "company");
        captureObservedCompany(config, externalCompanyId, null, null, null, null, operator);
        Long deptId = resolveDeptId(externalCompanyId);
        firePoint.setExternalStationId(externalStationId);
        if (StringUtils.isBlank(firePoint.getFirePointCode())) firePoint.setFirePointCode(stationNumber);
        if (StringUtils.isBlank(firePoint.getFirePointName())) firePoint.setFirePointName(stationNumber);
        firePoint.setStationNumber(stationNumber);
        firePoint.setStationType(readText(stationNode, "type"));
        firePoint.setExternalCompanyId(externalCompanyId);
        firePoint.setSourceDeptId(config.getDeptId());
        firePoint.setDeptId(deptId);
        if (StringUtils.isBlank(firePoint.getPointType())) firePoint.setPointType(readText(stationNode, "type"));
        if (StringUtils.isBlank(firePoint.getRemark())) firePoint.setRemark(readText(stationNode, "remark"));
        firePoint.setStatus(StringUtils.defaultIfBlank(firePoint.getStatus(), FIRE_POINT_STATUS_NORMAL));
        firePoint.setSyncStatus(deptId == null ? STATUS_OBSERVED : STATUS_SYNCED);
        firePoint.setLastSyncTime(DateUtils.getNowDate());
        firePoint.setUpdateBy(operator);
        firePoint.setUpdateTime(DateUtils.getNowDate());

        if (isNew) feFirePointMapper.insertFeFirePoint(firePoint);
        else feFirePointMapper.updateFeFirePoint(firePoint);
        return firePoint;
    }

    private FeGateway upsertGateway(JsonNode tboxNode, JsonNode gpsNode, SysDeptApiConfig config, String operator)
    {
        Long externalTboxId = readLong(tboxNode, "id");
        String imei = readText(tboxNode, "imei");
        FeGateway gateway = externalTboxId == null ? null : feGatewayMapper.selectByExternalTboxId(externalTboxId);
        if (gateway == null && StringUtils.isNotBlank(imei)) gateway = feGatewayMapper.selectByImei(imei);
        boolean isNew = gateway == null;
        if (isNew)
        {
            gateway = new FeGateway();
            gateway.setCreateBy(operator);
            gateway.setCreateTime(DateUtils.getNowDate());
            gateway.setDelFlag("0");
        }

        Long externalCompanyId = readNestedLong(tboxNode, "company", "id");
        String externalCompanyName = readNestedText(tboxNode, "company", "name");
        captureObservedCompany(config, externalCompanyId, externalCompanyName, null, null, null, operator);
        Long deptId = resolveDeptId(externalCompanyId);
        Long firePointId = resolveFirePointIdForGateway(tboxNode, config.getDeptId(), externalCompanyId);
        gateway.setExternalTboxId(externalTboxId);
        gateway.setImei(imei);
        gateway.setSim(readText(tboxNode, "sim"));
        gateway.setExternalCompanyId(externalCompanyId);
        gateway.setSourceDeptId(config.getDeptId());
        gateway.setDeptId(deptId);
        gateway.setFirePointId(firePointId);
        BigDecimal gpsLongitude = readGpsCoordinate(gpsNode, "longitude");
        BigDecimal gpsLatitude = readGpsCoordinate(gpsNode, "latitude");
        if (gpsLongitude == null) gpsLongitude = readNestedBigDecimal(tboxNode, "gps", "longitude");
        if (gpsLatitude == null) gpsLatitude = readNestedBigDecimal(tboxNode, "gps", "latitude");
        if (!hasValidGps(gpsLongitude, gpsLatitude))
        {
            gpsLongitude = null;
            gpsLatitude = null;
        }
        Date syncTime = DateUtils.getNowDate();
        Date gpsTime = resolveGatewayGpsTime(gpsNode, tboxNode, syncTime);
        gateway.setGpsLongitude(gpsLongitude);
        gateway.setGpsLatitude(gpsLatitude);
        gateway.setLastOnlineTime(syncTime);
        gateway.setStatus(StringUtils.isNotBlank(imei) ? GATEWAY_STATUS_ONLINE : GATEWAY_STATUS_OFFLINE);
        gateway.setSyncStatus(deptId == null ? STATUS_OBSERVED : STATUS_SYNCED);
        gateway.setLastSyncTime(syncTime);
        if (externalCompanyId != null && firePointId == null) gateway.setRemark("External company has no unique fire point under current credential scope");
        gateway.setUpdateBy(operator);
        gateway.setUpdateTime(syncTime);

        if (isNew) feGatewayMapper.insertFeGateway(gateway);
        else feGatewayMapper.updateFeGateway(gateway);
        syncFirePointGpsFromGateway(gateway, operator);
        feVisitPassiveEventService.captureGatewayGpsAndDetectEvent(gateway, gpsLongitude, gpsLatitude, gpsTime, syncTime, config, operator);
        return gateway;
    }

    private void syncObservedCompanies(List<JsonNode> companyNodes, SysDeptApiConfig config, String operator, Set<Long> syncedCompanyIds)
    {
        if (companyNodes == null || companyNodes.isEmpty())
        {
            return;
        }
        for (JsonNode companyNode : companyNodes)
        {
            Long externalCompanyId = readLong(companyNode, "id");
            String externalCompanyName = readText(companyNode, "name");
            String numberPrefix = readText(companyNode, "number_prefix");
            String orgPath = readText(companyNode, "org_path");
            Long parentExternalCompanyId = readLong(companyNode, "parent");
            captureObservedCompany(config, externalCompanyId, externalCompanyName, numberPrefix, orgPath, parentExternalCompanyId, operator);
            if (externalCompanyId != null)
            {
                syncedCompanyIds.add(externalCompanyId);
            }
        }
    }
    private FeSensor upsertSensor(JsonNode sensorNode, SysDeptApiConfig config, String operator)
    {
        Long externalSensorId = readLong(sensorNode, "id");
        String mac = readText(sensorNode, "mac");
        FeSensor sensor = externalSensorId == null ? null : feSensorMapper.selectByExternalSensorId(externalSensorId);
        if (sensor == null && StringUtils.isNotBlank(mac)) sensor = feSensorMapper.selectByMac(mac);
        if (sensor == null && StringUtils.isNotBlank(mac)) sensor = feSensorMapper.selectBySensorCode(mac);
        boolean isNew = sensor == null;
        if (isNew)
        {
            sensor = new FeSensor();
            sensor.setCreateBy(operator);
            sensor.setCreateTime(DateUtils.getNowDate());
            sensor.setDelFlag("0");
        }

        Long externalTboxId = readLong(sensorNode, "tbox_id");
        FeGateway gateway = externalTboxId == null ? null : feGatewayMapper.selectByExternalTboxId(externalTboxId);
        JsonNode latestValue = extractLatestSensorValue(sensorNode.get("values"));
        sensor.setExternalSensorId(externalSensorId);
        sensor.setSensorCode(StringUtils.defaultIfBlank(sensor.getSensorCode(), mac));
        sensor.setMac(mac);
        sensor.setGatewayId(gateway == null ? null : gateway.getGatewayId());
        sensor.setGatewayCode(gateway == null ? null : gateway.getImei());
        sensor.setSourceDeptId(config.getDeptId());
        sensor.setDeptId(gateway == null ? null : gateway.getDeptId());
        sensor.setPressure(readBigDecimal(latestValue, "pressure"));
        sensor.setTemperature(readBigDecimal(latestValue, "temp"));
        sensor.setBatteryLevel(readInteger(latestValue, "battery"));
        sensor.setSignalStrength(null);
        sensor.setLastOnlineTime(parseDate(readText(latestValue, "created_time")));
        sensor.setStatus(latestValue == null ? SENSOR_STATUS_OFFLINE : SENSOR_STATUS_NORMAL);
        sensor.setSyncStatus(gateway == null ? STATUS_PENDING_GATEWAY : (gateway.getDeptId() == null ? STATUS_OBSERVED : STATUS_SYNCED));
        sensor.setLastSyncTime(DateUtils.getNowDate());
        sensor.setUpdateBy(operator);
        sensor.setUpdateTime(DateUtils.getNowDate());

        if (isNew) feSensorMapper.insertFeSensor(sensor);
        else feSensorMapper.updateFeSensor(sensor);
        return sensor;
    }
    private FeExtinguisher upsertExtinguisher(JsonNode extinguisherNode, TokenContext tokenContext, SysDeptApiConfig config, String operator)
    {
        Long externalExtinguisherId = readLong(extinguisherNode, "id");
        String labelCode = readText(extinguisherNode, "gb_number");
        FeExtinguisher extinguisher = externalExtinguisherId == null ? null : feExtinguisherMapper.selectByExternalExtinguisherId(externalExtinguisherId);
        if (extinguisher == null && StringUtils.isNotBlank(labelCode)) extinguisher = feExtinguisherMapper.selectByLabelCode(labelCode);
        boolean isNew = extinguisher == null;
        if (isNew)
        {
            extinguisher = new FeExtinguisher();
            extinguisher.setCreateBy(operator);
            extinguisher.setCreateTime(DateUtils.getNowDate());
            extinguisher.setDelFlag("0");
        }

        Long externalCompanyId = readNestedLong(extinguisherNode, "company", "id");
        String externalCompanyName = readNestedText(extinguisherNode, "company", "name");
        captureObservedCompany(config, externalCompanyId, externalCompanyName, null, null, null, operator);
        extinguisher.setExternalExtinguisherId(externalExtinguisherId);
        extinguisher.setExternalCompanyId(externalCompanyId);
        extinguisher.setLabelCode(labelCode);
        extinguisher.setSourceDeptId(config.getDeptId());
        extinguisher.setDeptId(resolveDeptId(externalCompanyId));
        applySdkRawFields(extinguisher, extinguisherNode);
        try
        {
            JsonNode profileNode = fetchExtinguisherProfileNode(labelCode, tokenContext, config);
            applySdkRawFields(extinguisher, profileNode);
        }
        catch (Exception e)
        {
            log.warn("[DeviceSync] detail profile refresh skipped, labelCode={}, message={}", labelCode, e.getMessage());
        }
        applySdkProfileMetadata(extinguisher, operator);
        extinguisher.setStatus(StringUtils.defaultIfBlank(extinguisher.getStatus(), EXTINGUISHER_STATUS_NORMAL));
        extinguisher.setLastSyncTime(DateUtils.getNowDate());
        extinguisher.setUpdateBy(operator);
        extinguisher.setUpdateTime(DateUtils.getNowDate());

        if (isNew) feExtinguisherMapper.insertFeExtinguisher(extinguisher);
        else feExtinguisherMapper.updateFeExtinguisher(extinguisher);

        syncBindings(extinguisher, extinguisherNode, config, operator);
        refreshExtinguisherBindingCache(extinguisher, config, operator);
        return feExtinguisherMapper.selectFeExtinguisherByExtinguisherId(extinguisher.getExtinguisherId());
    }
    private void syncBindings(FeExtinguisher extinguisher, JsonNode extinguisherNode, SysDeptApiConfig config, String operator)
    {
        JsonNode bindingsNode = extinguisherNode.get("bindings");
        boolean hasBindingNode = bindingsNode != null && bindingsNode.isArray() && bindingsNode.size() > 0;
        Long desiredSensorId = null;
        Long desiredExternalSensorId = null;
        Date desiredBindTime = null;
        boolean desiredActive = false;

        if (hasBindingNode)
        {
            for (JsonNode bindingNode : bindingsNode)
            {
                Long externalSensorId = resolveExternalSensorIdFromBindingNode(bindingNode);
                if (externalSensorId == null)
                {
                    log.warn("[DeviceSync] binding payload missing external sensor id, extinguisherExternalId={}, payload={}",
                        extinguisher.getExternalExtinguisherId(), safeJson(bindingNode));
                    continue;
                }
                FeSensor sensor = feSensorMapper.selectByExternalSensorId(externalSensorId);
                if (sensor == null)
                {
                    log.warn("[DeviceSync] binding sensor not found locally, extinguisherExternalId={}, externalSensorId={}, payload={}",
                        extinguisher.getExternalExtinguisherId(), externalSensorId, safeJson(bindingNode));
                    continue;
                }
                Date bindTime = parseDate(readText(bindingNode, "created_time"));
                if (bindTime == null) bindTime = DateUtils.getNowDate();
                Date disableTime = parseDate(readText(bindingNode, "disable_time"));
                FeExtinguisherSensorBinding existed = feExtinguisherSensorBindingMapper.selectByExtinguisherSensorAndBindTime(extinguisher.getExtinguisherId(), sensor.getSensorId(), bindTime);
                if (existed == null)
                {
                    FeExtinguisherSensorBinding binding = new FeExtinguisherSensorBinding();
                    binding.setExtinguisherId(extinguisher.getExtinguisherId());
                    binding.setSensorId(sensor.getSensorId());
                    binding.setExternalExtinguisherId(extinguisher.getExternalExtinguisherId());
                    binding.setExternalSensorId(externalSensorId);
                    binding.setBindTime(bindTime);
                    binding.setUnbindTime(disableTime);
                    binding.setIsActive(disableTime == null ? ACTIVE_YES : ACTIVE_NO);
                    binding.setSourceType("sdk_sync");
                    binding.setCreateBy(operator);
                    binding.setCreateTime(DateUtils.getNowDate());
                    feExtinguisherSensorBindingMapper.insertFeExtinguisherSensorBinding(binding);
                }
                if (disableTime == null)
                {
                    desiredSensorId = sensor.getSensorId();
                    desiredExternalSensorId = externalSensorId;
                    desiredBindTime = bindTime;
                    desiredActive = true;
                }
            }
        }

        if (!desiredActive)
        {
            JsonNode sensorsNode = extinguisherNode.get("sensors");
            if (sensorsNode != null && sensorsNode.isArray() && sensorsNode.size() > 0)
            {
                for (JsonNode sensorNode : sensorsNode)
                {
                    Long externalSensorId = resolveExternalSensorIdFromSensorNode(sensorNode);
                    if (externalSensorId == null)
                    {
                        continue;
                    }
                    FeSensor sensor = feSensorMapper.selectByExternalSensorId(externalSensorId);
                    if (sensor == null)
                    {
                        log.warn("[DeviceSync] extinguisher sensors[] reference not found locally, extinguisherExternalId={}, externalSensorId={}, payload={}",
                            extinguisher.getExternalExtinguisherId(), externalSensorId, safeJson(sensorNode));
                        continue;
                    }
                    Date bindTime = parseDate(readText(extinguisherNode, "created_time"));
                    if (bindTime == null) bindTime = DateUtils.getNowDate();
                    FeExtinguisherSensorBinding existed = feExtinguisherSensorBindingMapper.selectByExtinguisherSensorAndBindTime(extinguisher.getExtinguisherId(), sensor.getSensorId(), bindTime);
                    if (existed == null)
                    {
                        FeExtinguisherSensorBinding binding = new FeExtinguisherSensorBinding();
                        binding.setExtinguisherId(extinguisher.getExtinguisherId());
                        binding.setSensorId(sensor.getSensorId());
                        binding.setExternalExtinguisherId(extinguisher.getExternalExtinguisherId());
                        binding.setExternalSensorId(externalSensorId);
                        binding.setBindTime(bindTime);
                        binding.setIsActive(ACTIVE_YES);
                        binding.setSourceType("sdk_sync");
                        binding.setCreateBy(operator);
                        binding.setCreateTime(DateUtils.getNowDate());
                        feExtinguisherSensorBindingMapper.insertFeExtinguisherSensorBinding(binding);
                    }
                    desiredSensorId = sensor.getSensorId();
                    desiredExternalSensorId = externalSensorId;
                    desiredBindTime = bindTime;
                    desiredActive = true;
                    break;
                }
                if (!desiredActive)
                {
                    log.warn("[DeviceSync] extinguisher sensors[] did not match any local sensor, extinguisherExternalId={}, payload={}",
                        extinguisher.getExternalExtinguisherId(), safeJson(sensorsNode));
                }
            }
        }

        if (!desiredActive)
        {
            log.warn("[DeviceSync] no active binding resolved for extinguisher, extinguisherExternalId={}, sourceDeptId={}, externalCompanyId={}, bindingsPayload={}, sensorsPayload={}, fullPayload={}",
                extinguisher.getExternalExtinguisherId(),
                config == null ? null : config.getDeptId(),
                extinguisher.getExternalCompanyId(),
                safeJson(bindingsNode),
                safeJson(extinguisherNode.get("sensors")),
                safeJson(extinguisherNode));
        }

        if (desiredActive)
        {
            FeExtinguisherSensorBinding activeBinding = feExtinguisherSensorBindingMapper.selectActiveBindingByExtinguisherId(extinguisher.getExtinguisherId());
            if (activeBinding != null && (!Objects.equals(activeBinding.getSensorId(), desiredSensorId) || !Objects.equals(activeBinding.getBindTime(), desiredBindTime)))
            {
                feExtinguisherSensorBindingMapper.deactivateActiveBinding(extinguisher.getExtinguisherId(), DateUtils.getNowDate(), operator);
            }
            if (activeBinding == null || !Objects.equals(activeBinding.getSensorId(), desiredSensorId) || !Objects.equals(activeBinding.getBindTime(), desiredBindTime))
            {
                FeExtinguisherSensorBinding binding = feExtinguisherSensorBindingMapper.selectByExtinguisherSensorAndBindTime(extinguisher.getExtinguisherId(), desiredSensorId, desiredBindTime);
                if (binding == null)
                {
                    binding = new FeExtinguisherSensorBinding();
                    binding.setExtinguisherId(extinguisher.getExtinguisherId());
                    binding.setSensorId(desiredSensorId);
                    binding.setExternalExtinguisherId(extinguisher.getExternalExtinguisherId());
                    binding.setExternalSensorId(desiredExternalSensorId);
                    binding.setBindTime(desiredBindTime);
                    binding.setIsActive(ACTIVE_YES);
                    binding.setSourceType("sdk_sync");
                    binding.setCreateBy(operator);
                    binding.setCreateTime(DateUtils.getNowDate());
                    feExtinguisherSensorBindingMapper.insertFeExtinguisherSensorBinding(binding);
                }
            }
        }
    }

    private void refreshExtinguisherBindingCache(FeExtinguisher extinguisher, SysDeptApiConfig config, String operator)
    {
        FeExtinguisher latest = feExtinguisherMapper.selectFeExtinguisherByExtinguisherId(extinguisher.getExtinguisherId());
        FeExtinguisherSensorBinding activeBinding = feExtinguisherSensorBindingMapper.selectActiveBindingByExtinguisherId(extinguisher.getExtinguisherId());
        if (activeBinding == null)
        {
            latest.setSensorId(null);
            latest.setSensorCode(null);
            latest.setFirePointId(null);
            latest.setSourceDeptId(config.getDeptId());
            latest.setDeptId(resolveDeptId(latest.getExternalCompanyId()));
            latest.setSyncStatus(STATUS_UNBOUND);
            latest.setUpdateBy(operator);
            latest.setUpdateTime(DateUtils.getNowDate());
            feExtinguisherMapper.updateFeExtinguisher(latest);
            return;
        }

        FeSensor sensor = feSensorMapper.selectFeSensorBySensorId(activeBinding.getSensorId());
        latest.setSensorId(sensor == null ? null : sensor.getSensorId());
        latest.setSensorCode(sensor == null ? null : sensor.getSensorCode());
        latest.setSourceDeptId(sensor == null ? config.getDeptId() : sensor.getSourceDeptId());
        latest.setDeptId(sensor == null ? resolveDeptId(latest.getExternalCompanyId()) : sensor.getDeptId());
        Long firePointId = null;
        if (sensor != null && sensor.getGatewayId() != null)
        {
            FeGateway gateway = feGatewayMapper.selectFeGatewayByGatewayId(sensor.getGatewayId());
            if (gateway != null)
            {
                firePointId = gateway.getFirePointId();
                if (latest.getDeptId() == null) latest.setDeptId(gateway.getDeptId());
                if (latest.getSourceDeptId() == null) latest.setSourceDeptId(gateway.getSourceDeptId());
            }
        }
        latest.setFirePointId(firePointId);
        latest.setSyncStatus(latest.getDeptId() == null ? STATUS_OBSERVED : STATUS_SYNCED);
        latest.setUpdateBy(operator);
        latest.setUpdateTime(DateUtils.getNowDate());
        feExtinguisherMapper.updateFeExtinguisher(latest);
    }
    private void syncSensorValues(List<FeSensor> sensors, TokenContext tokenContext, SysDeptApiConfig config, SyncStats stats)
    {
        for (FeSensor sensor : sensors)
        {
            if (sensor.getExternalSensorId() == null || sensor.getSensorId() == null) continue;
            stats.incrementInfo("valuesRequest");
            JsonNode root;
            try
            {
                root = requestJson(HttpMethod.GET,
                    PATH_SENSOR_VALUES.replace("{sensor_id}", String.valueOf(sensor.getExternalSensorId())),
                    Collections.singletonMap("unit", azdapsProperties.getHistoryUnit()), tokenContext, config, true);
            }
            catch (Exception e)
            {
                stats.incrementFail("values");
                continue;
            }
            JsonNode values = extractSensorValueArray(root);
            if (values == null || !values.isArray() || values.size() == 0)
            {
                stats.incrementInfo("valuesEmpty");
                continue;
            }
            for (JsonNode item : values)
            {
                Date createTime = parseDate(readText(item, "created_time"));
                if (createTime == null) continue;
                if (feSensorHistoryMapper.countBySensorIdAndCreateTime(sensor.getSensorId(), createTime) > 0)
                {
                    stats.incrementInfo("valuesDuplicate");
                    continue;
                }
                FeSensorHistory history = new FeSensorHistory();
                history.setSensorId(sensor.getSensorId());
                history.setSensorCode(sensor.getSensorCode());
                history.setPressure(readBigDecimal(item, "pressure"));
                history.setTemperature(readBigDecimal(item, "temp"));
                history.setBatteryLevel(readInteger(item, "battery"));
                history.setSignalStrength(sensor.getSignalStrength());
                history.setStatus(sensor.getStatus());
                history.setCreateTime(createTime);
                feSensorHistoryMapper.insertFeSensorHistory(history);
                stats.incrementSuccess("values");
            }
        }
    }

    private JsonNode extractSensorValueArray(JsonNode root)
    {
        if (root == null || root.isMissingNode() || root.isNull()) return null;
        if (root.isArray()) return root;
        JsonNode values = root.get("values");
        if (values != null && values.isArray()) return values;
        return extractItemArray(root);
    }
    private void captureObservedCompany(SysDeptApiConfig config, Long externalCompanyId, String companyName,
                                        String numberPrefix, String orgPath, Long parentExternalCompanyId,
                                        String operator)
    {
        if (externalCompanyId == null)
        {
            return;
        }
        Date now = DateUtils.getNowDate();

        FeExternalCompany company = feExternalCompanyMapper.selectByExternalCompanyId(externalCompanyId);
        if (company == null)
        {
            company = new FeExternalCompany();
            company.setExternalCompanyId(externalCompanyId);
            company.setFirstSourceType(SOURCE_SDK);
            company.setSdkObserved(ACTIVE_YES);
            company.setManualCreated(ACTIVE_NO);
            company.setRecordStatus("active");
            company.setFirstSeenTime(now);
            company.setCreateBy(operator);
            company.setCreateTime(now);
        }
        else
        {
            if (StringUtils.isBlank(company.getFirstSourceType()))
            {
                company.setFirstSourceType(SOURCE_SDK);
            }
            company.setRecordStatus(StringUtils.defaultIfBlank(company.getRecordStatus(), "active"));
            company.setSdkObserved(ACTIVE_YES);
            company.setManualCreated(StringUtils.defaultIfBlank(company.getManualCreated(), ACTIVE_NO));
            if (company.getFirstSeenTime() == null)
            {
                company.setFirstSeenTime(now);
            }
        }
        company.setExternalCompanyName(StringUtils.defaultIfBlank(companyName, company.getExternalCompanyName()));
        company.setNumberPrefix(StringUtils.defaultIfBlank(numberPrefix, company.getNumberPrefix()));
        company.setOrgPath(StringUtils.defaultIfBlank(orgPath, company.getOrgPath()));
        if (parentExternalCompanyId != null) company.setParentExternalCompanyId(parentExternalCompanyId);
        company.setLastSourceDeptId(config.getDeptId());
        company.setLastSeenTime(now);
        company.setSyncStatus(STATUS_OBSERVED);
        company.setUpdateBy(operator);
        company.setUpdateTime(now);
        if (company.getCompanyRecordId() == null) feExternalCompanyMapper.insertFeExternalCompany(company);
        else feExternalCompanyMapper.updateFeExternalCompany(company);

        FeApiConfigCompanyScope scope = feApiConfigCompanyScopeMapper.selectByConfigIdAndExternalCompanyId(config.getConfigId(), externalCompanyId);
        if (scope == null)
        {
            scope = new FeApiConfigCompanyScope();
            scope.setConfigId(config.getConfigId());
            scope.setSourceDeptId(config.getDeptId());
            scope.setExternalCompanyId(externalCompanyId);
            scope.setFirstSeenTime(now);
            scope.setCreateBy(operator);
            scope.setCreateTime(now);
        }
        scope.setExternalCompanyName(StringUtils.defaultIfBlank(companyName, scope.getExternalCompanyName()));
        scope.setLastSeenTime(now);
        scope.setSyncStatus(STATUS_OBSERVED);
        scope.setUpdateBy(operator);
        scope.setUpdateTime(now);
        if (scope.getScopeId() == null) feApiConfigCompanyScopeMapper.insertFeApiConfigCompanyScope(scope);
        else feApiConfigCompanyScopeMapper.updateFeApiConfigCompanyScope(scope);
    }

    private Long resolveDeptId(Long externalCompanyId)
    {
        if (externalCompanyId == null) return null;
        FeCompanyDeptMapping mapping = feCompanyDeptMappingMapper.selectByExternalCompanyId(externalCompanyId);
        return mapping == null ? null : mapping.getDeptId();
    }
    private Long resolveUniqueFirePointId(Long sourceDeptId, Long externalCompanyId)
    {
        if (sourceDeptId == null || externalCompanyId == null) return null;
        List<FeFirePoint> firePoints = feFirePointMapper.selectBySourceDeptIdAndExternalCompanyId(sourceDeptId, externalCompanyId);
        if (firePoints == null || firePoints.size() != 1) return null;
        return firePoints.get(0).getFirePointId();
    }

    private Long resolveFirePointIdForGateway(JsonNode tboxNode, Long sourceDeptId, Long externalCompanyId)
    {
        Long externalStationId = readLong(tboxNode, "train");
        if (externalStationId != null)
        {
            FeFirePoint firePoint = feFirePointMapper.selectByExternalStationId(externalStationId);
            if (firePoint != null) return firePoint.getFirePointId();
        }
        return resolveUniqueFirePointId(sourceDeptId, externalCompanyId);
    }

    private Map<Long, JsonNode> buildGpsNodeMap(List<JsonNode> gpsNodes)
    {
        Map<Long, JsonNode> gpsNodeMap = new LinkedHashMap<>();
        if (gpsNodes == null) return gpsNodeMap;
        for (JsonNode gpsNode : gpsNodes)
        {
            Long tboxId = readLong(gpsNode, "tbox_id");
            if (tboxId != null) gpsNodeMap.put(tboxId, gpsNode);
        }
        return gpsNodeMap;
    }

    private void syncFirePointGpsFromGateway(FeGateway gateway, String operator)
    {
        if (gateway == null || gateway.getFirePointId() == null) return;
        if (!hasValidGps(gateway.getGpsLongitude(), gateway.getGpsLatitude())) return;

        FeFirePoint firePoint = feFirePointMapper.selectFeFirePointByFirePointId(gateway.getFirePointId());
        if (firePoint == null) return;
        if (Objects.equals(firePoint.getLongitude(), gateway.getGpsLongitude())
            && Objects.equals(firePoint.getLatitude(), gateway.getGpsLatitude()))
        {
            return;
        }

        firePoint.setLongitude(gateway.getGpsLongitude());
        firePoint.setLatitude(gateway.getGpsLatitude());
        firePoint.setUpdateBy(operator);
        firePoint.setUpdateTime(DateUtils.getNowDate());
        feFirePointMapper.updateFeFirePoint(firePoint);
    }

    private JsonNode selectLatestValueNode(JsonNode valuesNode)
    {
        if (valuesNode == null || !valuesNode.isArray() || valuesNode.size() == 0) return null;
        JsonNode latest = null;
        Date latestTime = null;
        for (JsonNode valueNode : valuesNode)
        {
            Date currentTime = parseDate(readText(valueNode, "created_time"));
            if (latest == null || (currentTime != null && (latestTime == null || currentTime.after(latestTime))))
            {
                latest = valueNode;
                latestTime = currentTime;
            }
        }
        return latest == null ? valuesNode.get(valuesNode.size() - 1) : latest;
    }

    private String readText(JsonNode node, String fieldName)
    {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        return asText(node.get(fieldName));
    }

    private String readFirstText(JsonNode node, String... fieldNames)
    {
        if (node == null || fieldNames == null)
        {
            return null;
        }
        for (String fieldName : fieldNames)
        {
            String value = readText(node, fieldName);
            if (StringUtils.isNotBlank(value))
            {
                return value;
            }
        }
        return null;
    }

    private String readNestedText(JsonNode node, String parentField, String childField)
    {
        if (node == null) return null;
        return readText(node.get(parentField), childField);
    }

    private Long readLong(JsonNode node, String fieldName)
    {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        return asLong(node.get(fieldName));
    }

    private JsonNode extractLatestSensorValue(JsonNode valuesNode)
    {
        return selectLatestValueNode(valuesNode);
    }

    private Long readNestedLong(JsonNode node, String parentField, String childField)
    {
        if (node == null) return null;
        JsonNode parent = node.get(parentField);
        if (parent == null || parent.isNull() || parent.isMissingNode()) return null;
        return asLong(parent.get(childField));
    }

    private Integer readInteger(JsonNode node, String fieldName)
    {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull() || value.isMissingNode()) return null;
        if (value.isInt() || value.isLong()) return value.intValue();
        if (value.isTextual() && StringUtils.isNotBlank(value.asText()))
        {
            try { return Integer.parseInt(value.asText()); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private BigDecimal readBigDecimal(JsonNode node, String fieldName)
    {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        return asBigDecimal(node.get(fieldName));
    }

    private BigDecimal readNestedBigDecimal(JsonNode node, String parentField, String childField)
    {
        if (node == null) return null;
        JsonNode parent = node.get(parentField);
        if (parent == null || parent.isNull() || parent.isMissingNode()) return null;
        return asBigDecimal(parent.get(childField));
    }

    private BigDecimal readGpsCoordinate(JsonNode node, String fieldName)
    {
        return readBigDecimal(node, fieldName);
    }

    private boolean hasValidGps(BigDecimal longitude, BigDecimal latitude)
    {
        return VisitGeoUtils.isValidGps(longitude, latitude);
    }

    private Date resolveGatewayGpsTime(JsonNode gpsNode, JsonNode tboxNode, Date syncTime)
    {
        Date gpsTime = readFirstDate(gpsNode, "gps_time", "created_time", "updated_time", "time", "timestamp");
        if (gpsTime != null)
        {
            return gpsTime;
        }
        gpsTime = readFirstNestedDate(tboxNode, "gps", "gps_time", "created_time", "updated_time", "time", "timestamp");
        return gpsTime != null ? gpsTime : syncTime;
    }

    private Date readFirstDate(JsonNode node, String... fieldNames)
    {
        if (node == null || fieldNames == null)
        {
            return null;
        }
        for (String fieldName : fieldNames)
        {
            Date value = parseDate(readText(node, fieldName));
            if (value != null)
            {
                return value;
            }
        }
        return null;
    }

    private Date readFirstNestedDate(JsonNode node, String parentField, String... fieldNames)
    {
        if (node == null)
        {
            return null;
        }
        JsonNode parent = node.get(parentField);
        return readFirstDate(parent, fieldNames);
    }

    private Long asLong(JsonNode node)
    {
        if (node == null || node.isNull() || node.isMissingNode()) return null;
        if (node.isIntegralNumber()) return node.longValue();
        if (node.isTextual() && StringUtils.isNotBlank(node.asText()))
        {
            try { return Long.parseLong(node.asText()); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private String asText(JsonNode node)
    {
        if (node == null || node.isNull() || node.isMissingNode()) return null;
        String text = node.asText();
        return StringUtils.isBlank(text) ? null : text;
    }

    private BigDecimal asBigDecimal(JsonNode node)
    {
        if (node == null || node.isNull() || node.isMissingNode()) return null;
        if (node.isNumber()) return node.decimalValue();
        if (node.isTextual() && StringUtils.isNotBlank(node.asText()))
        {
            try { return new BigDecimal(node.asText()); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private Long resolveExternalSensorIdFromBindingNode(JsonNode bindingNode)
    {
        Long externalSensorId = readNestedLong(bindingNode, "sensor", "id");
        if (externalSensorId != null) return externalSensorId;
        externalSensorId = readLong(bindingNode, "sensor_id");
        if (externalSensorId != null) return externalSensorId;
        return readLong(bindingNode, "sensorId");
    }

    private Long resolveExternalSensorIdFromSensorNode(JsonNode sensorNode)
    {
        Long externalSensorId = asLong(sensorNode);
        if (externalSensorId != null) return externalSensorId;
        externalSensorId = readLong(sensorNode, "id");
        if (externalSensorId != null) return externalSensorId;
        externalSensorId = readLong(sensorNode, "sensor_id");
        if (externalSensorId != null) return externalSensorId;
        externalSensorId = readLong(sensorNode, "sensorId");
        if (externalSensorId != null) return externalSensorId;
        return readNestedLong(sensorNode, "sensor", "id");
    }

    private String safeJson(JsonNode node)
    {
        return node == null ? "null" : node.toString();
    }

    private Date parseDate(String value)
    {
        if (StringUtils.isBlank(value)) return null;
        try { return Date.from(Instant.parse(value)); } catch (DateTimeParseException e) { }
        try { return Date.from(OffsetDateTime.parse(value).toInstant()); } catch (DateTimeParseException e) { }
        try { return Date.from(LocalDateTime.parse(value).atZone(ZoneId.systemDefault()).toInstant()); } catch (DateTimeParseException e) { }
        try { return Date.from(LocalDate.parse(value).atStartOfDay(ZoneId.systemDefault()).toInstant()); } catch (DateTimeParseException e) { }
        return null;
    }

    private static class TokenContext
    {
        private String accessToken;
        private String refreshToken;
        private Date tokenExpireTime;

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public Date getTokenExpireTime() { return tokenExpireTime; }
        public void setTokenExpireTime(Date tokenExpireTime) { this.tokenExpireTime = tokenExpireTime; }
    }

    private static class SyncStats
    {
        private final Map<String, Integer> detailSuccess = new LinkedHashMap<>();
        private final Map<String, Integer> detailFail = new LinkedHashMap<>();
        private final Map<String, Object> detailInfo = new LinkedHashMap<>();

        void incrementSuccess(String scope) { detailSuccess.merge(scope, 1, Integer::sum); }
        void incrementFail(String scope) { detailFail.merge(scope, 1, Integer::sum); }
        void incrementInfo(String scope)
        {
            Object value = detailInfo.get(scope);
            int count = value instanceof Number ? ((Number) value).intValue() : 0;
            detailInfo.put(scope, count + 1);
        }
        void addInfo(String scope, int count)
        {
            if (count > 0)
            {
                Object value = detailInfo.get(scope);
                int current = value instanceof Number ? ((Number) value).intValue() : 0;
                detailInfo.put(scope, current + count);
            }
        }
        void putInfo(String scope, int count) { detailInfo.put(scope, count); }
        void putInfo(String scope, String value) { detailInfo.put(scope, value); }
        int getSuccessCount() { return detailSuccess.values().stream().mapToInt(Integer::intValue).sum(); }
        int getFailCount() { return detailFail.values().stream().mapToInt(Integer::intValue).sum(); }
        String buildMessage() { return "success=" + detailSuccess + ", fail=" + detailFail + ", info=" + detailInfo; }
        Map<String, Object> toMap()
        {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("success", detailSuccess);
            map.put("fail", detailFail);
            map.put("info", detailInfo);
            map.put("successCount", getSuccessCount());
            map.put("failCount", getFailCount());
            return map;
        }
    }
}
