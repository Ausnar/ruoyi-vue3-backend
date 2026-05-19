package com.ruoyi.manage.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.manage.config.HodoIotCardProperties;
import com.ruoyi.manage.domain.FeIotCard;
import com.ruoyi.manage.domain.FeIotCardMonthFlow;
import com.ruoyi.manage.domain.FeIotCardSyncLog;
import com.ruoyi.manage.mapper.FeIotCardMapper;
import com.ruoyi.manage.service.IFeIotCardService;
import com.ruoyi.system.service.ISysConfigService;

@Service
public class FeIotCardServiceImpl implements IFeIotCardService
{
    private static final Logger log = LoggerFactory.getLogger(FeIotCardServiceImpl.class);

    private static final String CONFIG_BASE_URL = "iot.hodo.baseUrl";
    private static final String CONFIG_ACCESS_KEY = "iot.hodo.accessKey";
    private static final String CONFIG_SECRET_KEY = "iot.hodo.secretKey";
    private static final String CONFIG_ACCOUNT = "iot.hodo.account";
    private static final String CONFIG_SKIP_SSL = "iot.hodo.skipSslVerify";
    private static final String CONFIG_EXPIRY_SOON_DAYS = "iot.card.expiry.soonDays";
    private static final String CONFIG_EXPIRY_NEAR_DAYS = "iot.card.expiry.nearDays";
    private static final String CONFIG_FLOW_LOW_PERCENT = "iot.card.flow.lowPercent";
    private static final String CONFIG_FLOW_CRITICAL_MB = "iot.card.flow.criticalMb";

    private static final String PATH_CARD_INFO = "/hodo/openApi/queryCardInfo";
    private static final String PATH_CARD_PACKAGE = "/hodo/openApi/queryCardPackage";
    private static final String PATH_MONTH_FLOW = "/hodo/openApi/queryMonthFlow";

    private static final String STATUS_RUNNING = "running";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILED = "failed";
    private static final String STATUS_INCOMPLETE = "incomplete";
    private static final String STATUS_PENDING = "pending";
    private static final String SOURCE_GATEWAY = "gateway";

    @Autowired private FeIotCardMapper feIotCardMapper;
    @Autowired private ISysConfigService sysConfigService;
    @Autowired private HodoIotCardProperties hodoIotCardProperties;
    @Autowired private ObjectMapper objectMapper;

    private final AtomicBoolean syncRunning = new AtomicBoolean(false);
    private final Random random = new Random();
    private volatile RestTemplate normalRestTemplate;
    private volatile RestTemplate insecureRestTemplate;

    @Override
    public FeIotCard selectFeIotCardByCardId(Long cardId)
    {
        return feIotCardMapper.selectFeIotCardByCardId(cardId);
    }

    @Override
    public List<FeIotCard> selectFeIotCardList(FeIotCard feIotCard)
    {
        return feIotCardMapper.selectFeIotCardList(feIotCard);
    }

    @Override
    public Map<String, Object> importFromGatewaySims(String operator)
    {
        FeIotCard seed = new FeIotCard();
        seed.setDataSource(SOURCE_GATEWAY);
        seed.setSyncStatus(STATUS_PENDING);
        seed.setProviderAccount(readRequiredConfig(CONFIG_ACCOUNT));
        seed.setCreateBy(operator);
        seed.setCreateTime(DateUtils.getNowDate());
        seed.setUpdateBy(operator);
        seed.setUpdateTime(DateUtils.getNowDate());
        int count = feIotCardMapper.insertFromGatewaySims(seed);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("importedOrRefreshed", count);
        return result;
    }

    @Override
    public Map<String, Object> syncAll(String operator)
    {
        ensureSyncEnabled();
        if (!syncRunning.compareAndSet(false, true))
        {
            return busyResult();
        }

        List<FeIotCard> cards = feIotCardMapper.selectSyncableCards();
        FeIotCardSyncLog syncLog = createSyncLog("all", cards.size(), operator);
        feIotCardMapper.insertSyncLog(syncLog);

        int successCount = 0;
        int failCount = 0;
        List<Map<String, Object>> details = new ArrayList<>();
        try
        {
            for (FeIotCard card : cards)
            {
                Map<String, Object> detail = syncOne(card, operator);
                details.add(detail);
                if (Boolean.TRUE.equals(detail.get("success")))
                {
                    successCount++;
                }
                else
                {
                    failCount++;
                }
            }
            finishSyncLog(syncLog, failCount == 0 ? STATUS_SUCCESS : STATUS_INCOMPLETE, successCount, failCount,
                "iot card sync completed", operator);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", failCount == 0);
            result.put("total", cards.size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("details", details);
            return result;
        }
        catch (Exception e)
        {
            finishSyncLog(syncLog, STATUS_FAILED, successCount, failCount, e.getMessage(), operator);
            throw e;
        }
        finally
        {
            syncRunning.set(false);
        }
    }

    @Override
    public Map<String, Object> syncByCardId(Long cardId, String operator)
    {
        ensureSyncEnabled();
        FeIotCard card = feIotCardMapper.selectFeIotCardByCardId(cardId);
        if (card == null)
        {
            throw new ServiceException("IOT card does not exist");
        }
        return syncOne(card, operator);
    }

    private Map<String, Object> syncOne(FeIotCard card, String operator)
    {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("cardId", card.getCardId());
        detail.put("iccid", card.getIccid());
        try
        {
            if (StringUtils.isBlank(card.getIccid()))
            {
                throw new ServiceException("ICCID is blank");
            }
            HodoConfig config = loadConfig(card);
            Date now = DateUtils.getNowDate();
            HodoResponse info = post(config, PATH_CARD_INFO, body(config, card.getIccid(), null));
            HodoResponse cardPackage = post(config, PATH_CARD_PACKAGE, body(config, card.getIccid(), null));
            String month = DateTimeFormatter.ofPattern("yyyyMM").format(LocalDate.now());
            HodoResponse monthFlow = post(config, PATH_MONTH_FLOW, body(config, card.getIccid(), month));

            applyInfo(card, info);
            applyPackage(card, cardPackage);
            applyMonthFlow(card, monthFlow, month, now, operator);
            card.setProviderAccount(config.account);
            card.setRawCardInfo(info.rawBody);
            card.setRawPackageInfo(cardPackage.rawBody);
            card.setRawMonthFlow(monthFlow.rawBody);
            card.setLastSyncTime(now);
            card.setSyncStatus(STATUS_SUCCESS);
            card.setSyncMessage("sync success");
            card.setUpdateBy(operator);
            card.setUpdateTime(now);
            calculateRisk(card);
            feIotCardMapper.updateFeIotCard(card);

            detail.put("success", true);
            detail.put("message", "sync success");
            return detail;
        }
        catch (Exception e)
        {
            markCardFailed(card, e.getMessage(), operator);
            log.warn("[FeIotCard] sync failed, cardId={}, iccid={}, message={}", card.getCardId(), card.getIccid(), e.getMessage());
            detail.put("success", false);
            detail.put("message", e.getMessage());
            return detail;
        }
    }

    private void applyInfo(FeIotCard card, HodoResponse response)
    {
        JsonNode result = response.result;
        card.setMsisdn(text(result, "msisdn"));
        card.setCardStatus(integer(result, "dicCardStatus"));
        card.setCardStatusName(cardStatusName(card.getCardStatus()));
        card.setActiveTime(date(result, "activeTime"));
    }

    private void applyPackage(FeIotCard card, HodoResponse response)
    {
        JsonNode result = response.result;
        card.setTotalFlow(decimal(result, "totalFlow"));
        card.setUsedFlow(decimal(result, "usedFlow"));
        card.setLeftFlow(decimal(result, "leftFlow"));

        JsonNode mainPackage = result.path("mainPackage");
        if (!mainPackage.isMissingNode() && !mainPackage.isNull())
        {
            card.setPackageName(text(mainPackage, "mainPackageName"));
            card.setPackageStartDate(date(mainPackage, "startDate"));
            card.setPackageStopDate(date(mainPackage, "stopDate"));
            card.setPeriodStartDate(date(mainPackage, "periodStartDate"));
            card.setPeriodStopDate(date(mainPackage, "periodStopDate"));
            card.setPeriod(text(mainPackage, "period"));
        }
    }

    private void applyMonthFlow(FeIotCard card, HodoResponse response, String month, Date now, String operator)
    {
        BigDecimal monthUsedFlow = decimal(response.result, "totalFlow");
        card.setCurrentMonth(month);
        card.setMonthUsedFlow(monthUsedFlow);

        FeIotCardMonthFlow monthFlow = new FeIotCardMonthFlow();
        monthFlow.setCardId(card.getCardId());
        monthFlow.setIccid(card.getIccid());
        monthFlow.setFlowMonth(month);
        monthFlow.setMonthUsedFlow(monthUsedFlow);
        monthFlow.setRawResponse(response.rawBody);
        monthFlow.setSyncTime(now);
        monthFlow.setCreateBy(operator);
        monthFlow.setCreateTime(now);
        monthFlow.setUpdateBy(operator);
        monthFlow.setUpdateTime(now);
        feIotCardMapper.upsertMonthFlow(monthFlow);
    }

    private void calculateRisk(FeIotCard card)
    {
        card.setExpiryLevel(expiryLevel(card.getPackageStopDate()));
        card.setFlowLevel(flowLevel(card.getTotalFlow(), card.getLeftFlow()));
        card.setRiskSummary(card.getExpiryLevel() + "/" + card.getFlowLevel());
    }

    private String expiryLevel(Date stopDate)
    {
        if (stopDate == null)
        {
            return "unknown";
        }
        LocalDate stop = stopDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        if (stop.isBefore(today))
        {
            return "expired";
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(today, stop);
        if (days <= intConfig(CONFIG_EXPIRY_SOON_DAYS, 7))
        {
            return "expiring_soon";
        }
        if (days <= intConfig(CONFIG_EXPIRY_NEAR_DAYS, 30))
        {
            return "near_expiry";
        }
        return "normal";
    }

    private String flowLevel(BigDecimal totalFlow, BigDecimal leftFlow)
    {
        if (totalFlow == null || leftFlow == null || totalFlow.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "unknown";
        }
        if (leftFlow.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "exhausted";
        }
        BigDecimal criticalMb = decimalConfig(CONFIG_FLOW_CRITICAL_MB, new BigDecimal("10"));
        if (leftFlow.compareTo(criticalMb) <= 0)
        {
            return "critical";
        }
        BigDecimal ratio = leftFlow.multiply(new BigDecimal("100")).divide(totalFlow, 2, RoundingMode.HALF_UP);
        if (ratio.compareTo(decimalConfig(CONFIG_FLOW_LOW_PERCENT, new BigDecimal("20"))) <= 0)
        {
            return "low";
        }
        return "normal";
    }

    private HodoResponse post(HodoConfig config, String path, Map<String, Object> body)
    {
        try
        {
            String bodyJson = objectMapper.writeValueAsString(body);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=utf-8"));
            headers.add("nonce", String.valueOf(1000 + random.nextInt(9000)));
            headers.add("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            headers.add("accessKey", config.accessKey);
            headers.add("sign", md5(bodyJson + config.secretKey));

            ResponseEntity<String> response = restTemplate(config.skipSslVerify).postForEntity(
                config.baseUrl + path,
                new HttpEntity<>(bodyJson, headers),
                String.class
            );
            JsonNode root = objectMapper.readTree(response.getBody());
            if (!root.path("success").asBoolean(false))
            {
                throw new ServiceException(root.path("message").asText("Hodo API failed") + " (" + root.path("code").asText() + ")");
            }
            HodoResponse hodoResponse = new HodoResponse();
            hodoResponse.rawBody = response.getBody();
            hodoResponse.result = root.path("result");
            return hodoResponse;
        }
        catch (Exception e)
        {
            if (e instanceof ServiceException)
            {
                throw (ServiceException) e;
            }
            throw new ServiceException("Hodo API request failed: " + e.getMessage());
        }
    }

    private Map<String, Object> body(HodoConfig config, String iccid, String month)
    {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("iccid", iccid);
        body.put("account", config.account);
        if (StringUtils.isNotBlank(month))
        {
            body.put("month", month);
        }
        return body;
    }

    private HodoConfig loadConfig(FeIotCard card)
    {
        HodoConfig config = new HodoConfig();
        config.baseUrl = StringUtils.defaultIfBlank(sysConfigService.selectConfigByKey(CONFIG_BASE_URL), "https://api.hodo170.com");
        config.accessKey = readRequiredConfig(CONFIG_ACCESS_KEY);
        config.secretKey = readRequiredConfig(CONFIG_SECRET_KEY);
        config.account = StringUtils.defaultIfBlank(card.getProviderAccount(), readRequiredConfig(CONFIG_ACCOUNT));
        config.skipSslVerify = Boolean.parseBoolean(StringUtils.defaultIfBlank(sysConfigService.selectConfigByKey(CONFIG_SKIP_SSL), "false"));
        return config;
    }

    private void ensureSyncEnabled()
    {
        if (!hodoIotCardProperties.isSyncEnabled())
        {
            throw new ServiceException("Hodo IOT card sync is disabled in this runtime");
        }
    }

    private RestTemplate restTemplate(boolean skipSslVerify)
    {
        if (skipSslVerify)
        {
            if (insecureRestTemplate == null)
            {
                insecureRestTemplate = new RestTemplate(insecureFactory());
            }
            return insecureRestTemplate;
        }
        if (normalRestTemplate == null)
        {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(hodoIotCardProperties.getConnectTimeoutMs());
            factory.setReadTimeout(hodoIotCardProperties.getReadTimeoutMs());
            normalRestTemplate = new RestTemplate(factory);
        }
        return normalRestTemplate;
    }

    private SimpleClientHttpRequestFactory insecureFactory()
    {
        return new SimpleClientHttpRequestFactory()
        {
            @Override
            protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) throws java.io.IOException
            {
                super.prepareConnection(connection, httpMethod);
                setConnectTimeout(hodoIotCardProperties.getConnectTimeoutMs());
                setReadTimeout(hodoIotCardProperties.getReadTimeoutMs());
                if (connection instanceof HttpsURLConnection)
                {
                    HttpsURLConnection https = (HttpsURLConnection) connection;
                    https.setSSLSocketFactory(insecureSslContext().getSocketFactory());
                    https.setHostnameVerifier(insecureHostnameVerifier());
                }
            }
        };
    }

    private SSLContext insecureSslContext()
    {
        try
        {
            TrustManager[] trustAll = new TrustManager[] {
                new X509TrustManager()
                {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new java.security.SecureRandom());
            return sslContext;
        }
        catch (Exception e)
        {
            throw new ServiceException("Create insecure SSL context failed: " + e.getMessage());
        }
    }

    private HostnameVerifier insecureHostnameVerifier()
    {
        return (hostname, session) -> true;
    }

    private String md5(String value)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            throw new ServiceException("MD5 sign failed: " + e.getMessage());
        }
    }

    private String readRequiredConfig(String key)
    {
        String value = sysConfigService.selectConfigByKey(key);
        if (StringUtils.isBlank(value))
        {
            throw new ServiceException("Missing system config: " + key);
        }
        return value;
    }

    private int intConfig(String key, int defaultValue)
    {
        String value = sysConfigService.selectConfigByKey(key);
        if (StringUtils.isBlank(value))
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(value);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    private BigDecimal decimalConfig(String key, BigDecimal defaultValue)
    {
        String value = sysConfigService.selectConfigByKey(key);
        if (StringUtils.isBlank(value))
        {
            return defaultValue;
        }
        try
        {
            return new BigDecimal(value);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    private String text(JsonNode node, String field)
    {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private Integer integer(JsonNode node, String field)
    {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asInt();
    }

    private BigDecimal decimal(JsonNode node, String field)
    {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.decimalValue();
    }

    private Date date(JsonNode node, String field)
    {
        String value = text(node, field);
        return StringUtils.isBlank(value) ? null : DateUtils.parseDate(value);
    }

    private String cardStatusName(Integer status)
    {
        if (status == null) return null;
        switch (status)
        {
            case 1: return "inactive";
            case 2: return "active";
            case 3: return "stopped";
            case 4: return "testable";
            case 5: return "stock";
            case 6: return "pre_cancelled";
            case 7: return "operator_managed";
            case 8: return "removed";
            default: return "unknown";
        }
    }

    private void markCardFailed(FeIotCard card, String message, String operator)
    {
        card.setSyncStatus(STATUS_FAILED);
        card.setSyncMessage(StringUtils.left(message, 1000));
        card.setLastSyncTime(DateUtils.getNowDate());
        card.setUpdateBy(operator);
        card.setUpdateTime(DateUtils.getNowDate());
        feIotCardMapper.updateFeIotCard(card);
    }

    private FeIotCardSyncLog createSyncLog(String scope, int total, String operator)
    {
        FeIotCardSyncLog log = new FeIotCardSyncLog();
        log.setSyncScope(scope);
        log.setSyncStatus(STATUS_RUNNING);
        log.setStartTime(DateUtils.getNowDate());
        log.setTotalCount(total);
        log.setSuccessCount(0);
        log.setFailCount(0);
        log.setCreateBy(operator);
        log.setCreateTime(DateUtils.getNowDate());
        log.setUpdateBy(operator);
        log.setUpdateTime(DateUtils.getNowDate());
        return log;
    }

    private void finishSyncLog(FeIotCardSyncLog syncLog, String status, int successCount, int failCount, String message, String operator)
    {
        syncLog.setSyncStatus(status);
        syncLog.setEndTime(DateUtils.getNowDate());
        syncLog.setSuccessCount(successCount);
        syncLog.setFailCount(failCount);
        syncLog.setMessage(StringUtils.left(message, 1000));
        syncLog.setUpdateBy(operator);
        syncLog.setUpdateTime(DateUtils.getNowDate());
        feIotCardMapper.updateSyncLog(syncLog);
    }

    private Map<String, Object> busyResult()
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", false);
        result.put("message", "IOT card sync is already running");
        return result;
    }

    private static class HodoConfig
    {
        private String baseUrl;
        private String accessKey;
        private String secretKey;
        private String account;
        private boolean skipSslVerify;
    }

    private static class HodoResponse
    {
        private String rawBody;
        private JsonNode result;
    }
}
