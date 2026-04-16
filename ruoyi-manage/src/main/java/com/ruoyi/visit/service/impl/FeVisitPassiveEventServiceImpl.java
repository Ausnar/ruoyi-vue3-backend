package com.ruoyi.visit.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.FeCompanyDeptMapping;
import com.ruoyi.manage.domain.FeFirePoint;
import com.ruoyi.manage.domain.FeGateway;
import com.ruoyi.manage.mapper.FeCompanyDeptMappingMapper;
import com.ruoyi.manage.mapper.FeFirePointMapper;
import com.ruoyi.system.domain.SysDeptApiConfig;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.visit.constant.VisitConstants;
import com.ruoyi.visit.domain.FeGatewayGpsHistory;
import com.ruoyi.visit.domain.FeVisitApply;
import com.ruoyi.visit.domain.FeVisitContractOption;
import com.ruoyi.visit.domain.FeVisitCustomer;
import com.ruoyi.visit.domain.FeVisitOwnerAssign;
import com.ruoyi.visit.domain.FeVisitPassiveEvent;
import com.ruoyi.visit.domain.dto.VisitPassiveCandidate;
import com.ruoyi.visit.domain.dto.VisitPassiveEventConfirmRequest;
import com.ruoyi.visit.mapper.FeGatewayGpsHistoryMapper;
import com.ruoyi.visit.mapper.FeVisitCustomerMapper;
import com.ruoyi.visit.mapper.FeVisitPassiveEventMapper;
import com.ruoyi.visit.service.IFeVisitApplyService;
import com.ruoyi.visit.service.IFeVisitCustomerService;
import com.ruoyi.visit.service.IFeVisitOwnerAssignService;
import com.ruoyi.visit.service.IFeVisitPassiveEventService;
import com.ruoyi.visit.util.VisitGeoUtils;

@Service
public class FeVisitPassiveEventServiceImpl implements IFeVisitPassiveEventService
{
    private static final BigDecimal DISPLACEMENT_THRESHOLD_M = BigDecimal.valueOf(1000);
    private static final BigDecimal MATCH_RADIUS_M = BigDecimal.valueOf(500);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeGatewayGpsHistoryMapper feGatewayGpsHistoryMapper;

    @Autowired
    private FeVisitPassiveEventMapper feVisitPassiveEventMapper;

    @Autowired
    private FeFirePointMapper feFirePointMapper;

    @Autowired
    private FeCompanyDeptMappingMapper feCompanyDeptMappingMapper;

    @Autowired
    private FeVisitCustomerMapper feVisitCustomerMapper;

    @Autowired
    private IFeVisitApplyService feVisitApplyService;

    @Autowired
    private IFeVisitCustomerService feVisitCustomerService;

    @Autowired
    private IFeVisitOwnerAssignService feVisitOwnerAssignService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void captureGatewayGpsAndDetectEvent(FeGateway gateway, BigDecimal gpsLongitude, BigDecimal gpsLatitude,
        Date gpsTime, Date syncTime, SysDeptApiConfig config, String operator)
    {
        if (gateway == null || gateway.getGatewayId() == null || !VisitGeoUtils.isValidGps(gpsLongitude, gpsLatitude))
        {
            return;
        }

        Long eventDeptId = gateway.getDeptId() != null ? gateway.getDeptId() : config == null ? null : config.getDeptId();
        Date now = DateUtils.getNowDate();
        Date actualSyncTime = syncTime != null ? syncTime : now;
        Date actualGpsTime = gpsTime != null ? gpsTime : actualSyncTime;

        FeGatewayGpsHistory previous = feGatewayGpsHistoryMapper.selectLatestGpsHistoryByGatewayId(gateway.getGatewayId());
        FeGatewayGpsHistory current = new FeGatewayGpsHistory();
        current.setGatewayId(gateway.getGatewayId());
        current.setExternalTboxId(gateway.getExternalTboxId());
        current.setDeptId(eventDeptId);
        current.setGpsLongitude(gpsLongitude);
        current.setGpsLatitude(gpsLatitude);
        current.setGpsTime(actualGpsTime);
        current.setSyncTime(actualSyncTime);
        current.setCreateBy(operator);
        current.setCreateTime(now);
        feGatewayGpsHistoryMapper.insertFeGatewayGpsHistory(current);

        if (previous == null)
        {
            return;
        }
        BigDecimal distanceM = VisitGeoUtils.calculateDistanceMeters(previous.getGpsLongitude(), previous.getGpsLatitude(),
            gpsLongitude, gpsLatitude);
        if (distanceM == null || distanceM.compareTo(DISPLACEMENT_THRESHOLD_M) < 0)
        {
            return;
        }
        if (feVisitPassiveEventMapper.countPendingByGatewayId(gateway.getGatewayId()) > 0)
        {
            return;
        }

        FeVisitPassiveEvent event = new FeVisitPassiveEvent();
        event.setGatewayId(gateway.getGatewayId());
        event.setDeptId(eventDeptId);
        event.setFirePointId(gateway.getFirePointId());
        event.setFromLongitude(previous.getGpsLongitude());
        event.setFromLatitude(previous.getGpsLatitude());
        event.setToLongitude(gpsLongitude);
        event.setToLatitude(gpsLatitude);
        event.setDistanceM(distanceM);
        event.setTriggerTime(actualGpsTime);
        event.setCandidateSummary(buildCandidateSummary(gpsLongitude, gpsLatitude, resolveScopeDeptIds(eventDeptId)));
        event.setStatus(VisitConstants.PASSIVE_EVENT_STATUS_PENDING_CONFIRM);
        event.setCreateBy(operator);
        event.setCreateTime(now);
        event.setUpdateBy(operator);
        event.setUpdateTime(now);
        feVisitPassiveEventMapper.insertFeVisitPassiveEvent(event);
    }

    @Override
    public List<FeVisitPassiveEvent> selectFeVisitPassiveEventList(FeVisitPassiveEvent event)
    {
        FeVisitPassiveEvent query = event == null ? new FeVisitPassiveEvent() : event;
        if (!SecurityUtils.isAdmin())
        {
            query.setScopeDeptIds(sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId()));
        }
        return feVisitPassiveEventMapper.selectFeVisitPassiveEventList(query);
    }

    @Override
    public FeVisitPassiveEvent selectFeVisitPassiveEventByEventId(Long eventId)
    {
        FeVisitPassiveEvent event = feVisitPassiveEventMapper.selectFeVisitPassiveEventByEventId(eventId);
        if (event == null)
        {
            return null;
        }
        checkEventScope(event);
        return event;
    }

    @Override
    public FeGatewayGpsHistory selectLatestGpsHistoryByGatewayId(Long gatewayId)
    {
        return feGatewayGpsHistoryMapper.selectLatestGpsHistoryByGatewayId(gatewayId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmPassiveEvent(Long eventId, VisitPassiveEventConfirmRequest request)
    {
        FeVisitPassiveEvent event = selectFeVisitPassiveEventByEventId(eventId);
        if (event == null)
        {
            throw new ServiceException("未找到对应的被动事件");
        }
        if (!VisitConstants.PASSIVE_EVENT_STATUS_PENDING_CONFIRM.equals(event.getStatus()))
        {
            throw new ServiceException("当前事件不允许确认转单");
        }
        if (request == null || StringUtils.isBlank(request.getSelectedTargetType()) || request.getSelectedTargetId() == null)
        {
            throw new ServiceException("请选择确认目标");
        }

        if (VisitConstants.TARGET_TYPE_CONTRACT_DEPT.equals(request.getSelectedTargetType())
            && request.getSelectedExternalCompanyId() == null)
        {
            throw new ServiceException("合同客户必须确认具体外部单位");
        }

        VisitPassiveCandidate selectedCandidate = requireCandidate(event, request.getSelectedTargetType(),
            request.getSelectedTargetId(), request.getSelectedExternalCompanyId());
        FeVisitOwnerAssign assign = feVisitOwnerAssignService.requireSingleEnabledAssign(request.getSelectedTargetType(),
            request.getSelectedTargetId());
        if (!SecurityUtils.isAdmin() && !SecurityUtils.getUserId().equals(assign.getOwnerUserId()))
        {
            throw new ServiceException("只有目标负责人本人或管理员可以确认转单");
        }

        FeVisitApply passiveApply = buildPassiveVisitApply(event, selectedCandidate, assign);
        feVisitApplyService.createPassiveVisitApply(passiveApply, SecurityUtils.getUserId(), SecurityUtils.getDeptId(),
            SecurityUtils.getUsername());

        event.setSelectedTargetType(request.getSelectedTargetType());
        event.setSelectedTargetId(request.getSelectedTargetId());
        event.setSelectedExternalCompanyId(selectedCandidate.getExternalCompanyId());
        event.setSelectedExternalCompanyName(selectedCandidate.getExternalCompanyName());
        event.setStatus(VisitConstants.PASSIVE_EVENT_STATUS_CONVERTED);
        event.setConfirmUserId(SecurityUtils.getUserId());
        event.setConfirmTime(DateUtils.getNowDate());
        event.setVisitId(passiveApply.getVisitId());
        event.setUpdateBy(SecurityUtils.getUsername());
        event.setUpdateTime(DateUtils.getNowDate());
        return feVisitPassiveEventMapper.updateFeVisitPassiveEvent(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int ignorePassiveEvent(Long eventId)
    {
        FeVisitPassiveEvent event = selectFeVisitPassiveEventByEventId(eventId);
        if (event == null)
        {
            throw new ServiceException("未找到对应的被动事件");
        }
        if (!VisitConstants.PASSIVE_EVENT_STATUS_PENDING_CONFIRM.equals(event.getStatus()))
        {
            throw new ServiceException("当前事件不允许忽略");
        }
        event.setStatus(VisitConstants.PASSIVE_EVENT_STATUS_IGNORED);
        event.setConfirmUserId(SecurityUtils.getUserId());
        event.setConfirmTime(DateUtils.getNowDate());
        event.setUpdateBy(SecurityUtils.getUsername());
        event.setUpdateTime(DateUtils.getNowDate());
        return feVisitPassiveEventMapper.updateFeVisitPassiveEvent(event);
    }

    private String buildCandidateSummary(BigDecimal longitude, BigDecimal latitude, List<Long> scopeDeptIds)
    {
        List<VisitPassiveCandidate> candidates = new ArrayList<>();
        candidates.addAll(buildContractCandidates(longitude, latitude, scopeDeptIds));
        candidates.addAll(buildIndependentCustomerCandidates(longitude, latitude, scopeDeptIds));
        candidates.sort(Comparator.comparing(VisitPassiveCandidate::getDistanceM, Comparator.nullsLast(BigDecimal::compareTo)));
        try
        {
            return objectMapper.writeValueAsString(candidates);
        }
        catch (Exception e)
        {
            throw new ServiceException("候选目标快照生成失败");
        }
    }

    private List<VisitPassiveCandidate> buildContractCandidates(BigDecimal longitude, BigDecimal latitude,
        List<Long> scopeDeptIds)
    {
        FeFirePoint query = new FeFirePoint();
        query.getParams().put("dataScope", "");
        List<FeFirePoint> firePoints = feFirePointMapper.selectFeFirePointList(query);
        Map<Long, FeFirePoint> nearestFirePointByExternalCompany = new LinkedHashMap<>();
        Map<Long, BigDecimal> nearestDistanceByExternalCompany = new LinkedHashMap<>();
        Map<Long, Long> mappedDeptByExternalCompany = new HashMap<>();
        for (FeFirePoint firePoint : firePoints)
        {
            if (!VisitGeoUtils.isValidGps(firePoint.getLongitude(), firePoint.getLatitude())
                || firePoint.getExternalCompanyId() == null)
            {
                continue;
            }
            BigDecimal distanceM = VisitGeoUtils.calculateDistanceMeters(firePoint.getLongitude(), firePoint.getLatitude(),
                longitude, latitude);
            if (distanceM == null || distanceM.compareTo(MATCH_RADIUS_M) > 0)
            {
                continue;
            }
            Long deptId = mappedDeptByExternalCompany.get(firePoint.getExternalCompanyId());
            if (deptId == null && !mappedDeptByExternalCompany.containsKey(firePoint.getExternalCompanyId()))
            {
                FeCompanyDeptMapping mapping = feCompanyDeptMappingMapper.selectByExternalCompanyId(
                    firePoint.getExternalCompanyId());
                deptId = mapping == null ? null : mapping.getDeptId();
                mappedDeptByExternalCompany.put(firePoint.getExternalCompanyId(), deptId);
            }
            if (deptId == null || !isDeptInScope(deptId, scopeDeptIds))
            {
                continue;
            }
            BigDecimal currentNearest = nearestDistanceByExternalCompany.get(firePoint.getExternalCompanyId());
            if (currentNearest == null || distanceM.compareTo(currentNearest) < 0)
            {
                nearestDistanceByExternalCompany.put(firePoint.getExternalCompanyId(), distanceM);
                nearestFirePointByExternalCompany.put(firePoint.getExternalCompanyId(), firePoint);
            }
        }
        if (nearestFirePointByExternalCompany.isEmpty())
        {
            return Collections.emptyList();
        }

        List<Long> deptIds = nearestFirePointByExternalCompany.values().stream()
            .map(FeFirePoint::getExternalCompanyId)
            .map(mappedDeptByExternalCompany::get)
            .filter(java.util.Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, FeVisitContractOption> optionByDept = feVisitApplyService.selectContractOptionsByDeptIds(deptIds)
            .stream()
            .collect(Collectors.toMap(FeVisitContractOption::getDeptId, option -> option, (left, right) -> left,
                LinkedHashMap::new));

        List<VisitPassiveCandidate> candidates = new ArrayList<>();
        for (Map.Entry<Long, FeFirePoint> entry : nearestFirePointByExternalCompany.entrySet())
        {
            Long deptId = mappedDeptByExternalCompany.get(entry.getKey());
            FeVisitContractOption option = optionByDept.get(deptId);
            if (option == null)
            {
                continue;
            }
            FeFirePoint firePoint = entry.getValue();
            VisitPassiveCandidate candidate = new VisitPassiveCandidate();
            candidate.setTargetType(VisitConstants.TARGET_TYPE_CONTRACT_DEPT);
            candidate.setTargetId(option.getDeptId());
            candidate.setTargetName(option.getDeptName());
            candidate.setExternalCompanyId(firePoint.getExternalCompanyId());
            candidate.setExternalCompanyName(firePoint.getExternalCompanyName());
            candidate.setDistanceM(nearestDistanceByExternalCompany.get(entry.getKey()));
            candidate.setFirePointId(firePoint.getFirePointId());
            candidate.setFirePointName(firePoint.getFirePointName());
            candidates.add(candidate);
        }
        return candidates;
    }

    private List<VisitPassiveCandidate> buildIndependentCustomerCandidates(BigDecimal longitude, BigDecimal latitude,
        List<Long> scopeDeptIds)
    {
        List<FeVisitCustomer> customers = feVisitCustomerService.selectEnabledCustomersWithCoordinates(scopeDeptIds);
        List<VisitPassiveCandidate> candidates = new ArrayList<>();
        for (FeVisitCustomer customer : customers)
        {
            BigDecimal distanceM = VisitGeoUtils.calculateDistanceMeters(customer.getLongitude(), customer.getLatitude(),
                longitude, latitude);
            if (distanceM == null || distanceM.compareTo(MATCH_RADIUS_M) > 0)
            {
                continue;
            }
            VisitPassiveCandidate candidate = new VisitPassiveCandidate();
            candidate.setTargetType(VisitConstants.TARGET_TYPE_INDEPENDENT_CUSTOMER);
            candidate.setTargetId(customer.getCustomerId());
            candidate.setTargetName(customer.getCustomerName());
            candidate.setDistanceM(distanceM);
            candidates.add(candidate);
        }
        return candidates;
    }

    private VisitPassiveCandidate requireCandidate(FeVisitPassiveEvent event, String targetType, Long targetId,
        Long externalCompanyId)
    {
        List<VisitPassiveCandidate> candidates = parseCandidates(event.getCandidateSummary());
        for (VisitPassiveCandidate candidate : candidates)
        {
            if (!StringUtils.equals(candidate.getTargetType(), targetType) || !targetId.equals(candidate.getTargetId()))
            {
                continue;
            }
            if (VisitConstants.TARGET_TYPE_CONTRACT_DEPT.equals(targetType))
            {
                if (externalCompanyId != null && externalCompanyId.equals(candidate.getExternalCompanyId()))
                {
                    return candidate;
                }
            }
            else
            {
                return candidate;
            }
        }
        throw new ServiceException("当前事件中不存在所选目标");
    }

    private List<VisitPassiveCandidate> parseCandidates(String candidateSummary)
    {
        if (StringUtils.isBlank(candidateSummary))
        {
            return Collections.emptyList();
        }
        try
        {
            return objectMapper.readValue(candidateSummary, new TypeReference<List<VisitPassiveCandidate>>() {});
        }
        catch (Exception e)
        {
            throw new ServiceException("事件候选数据解析失败");
        }
    }

    private FeVisitApply buildPassiveVisitApply(FeVisitPassiveEvent event, VisitPassiveCandidate candidate,
        FeVisitOwnerAssign assign)
    {
        FeVisitApply apply = new FeVisitApply();
        apply.setApplicantUserId(assign.getOwnerUserId());
        apply.setApplicantDeptId(assign.getOwnerDeptId());
        apply.setVisitMode(VisitConstants.VISIT_MODE_PASSIVE);
        apply.setSourceType(VisitConstants.SOURCE_TYPE_GATEWAY_DISPLACEMENT);
        apply.setSourceEventId(event.getEventId());
        apply.setTriggerGatewayId(event.getGatewayId());
        apply.setTriggerFirePointId(event.getFirePointId());
        apply.setTriggerFromLongitude(event.getFromLongitude());
        apply.setTriggerFromLatitude(event.getFromLatitude());
        apply.setTriggerToLongitude(event.getToLongitude());
        apply.setTriggerToLatitude(event.getToLatitude());
        apply.setTriggerDistanceM(event.getDistanceM());
        apply.setTriggerExternalCompanyId(candidate.getExternalCompanyId());
        apply.setTriggerExternalCompanyNameSnapshot(candidate.getExternalCompanyName());
        apply.setVisitAddress(StringUtils.defaultIfBlank(event.getFirePointName(), candidate.getTargetName()));
        apply.setPlannedStartTime(event.getTriggerTime());
        apply.setPlannedEndTime(event.getTriggerTime());
        apply.setVisitReason("网关位移触发被动拜访");
        apply.setVisitTarget("核实网关位移原因并完成客户跟进");
        apply.setCompanionMembers(null);
        apply.setRemark("来源事件ID=" + event.getEventId());
        if (VisitConstants.TARGET_TYPE_CONTRACT_DEPT.equals(candidate.getTargetType()))
        {
            List<FeVisitContractOption> options = feVisitApplyService.selectContractOptionsByDeptIds(
                Collections.singletonList(candidate.getTargetId()));
            if (options.isEmpty())
            {
                throw new ServiceException("未找到对应的合同单位配置");
            }
            FeVisitContractOption option = options.get(0);
            apply.setCustomerType(VisitConstants.CUSTOMER_TYPE_CONTRACT);
            apply.setContractDeptId(option.getDeptId());
            apply.setContractConfigId(null);
            apply.setCustomerId(null);
            apply.setCustomerNameSnapshot(option.getDeptName());
            apply.setContactPersonSnapshot(option.getContactPerson());
            apply.setContactPhoneSnapshot(option.getContactPhone());
        }
        else if (VisitConstants.TARGET_TYPE_INDEPENDENT_CUSTOMER.equals(candidate.getTargetType()))
        {
            FeVisitCustomer customer = feVisitCustomerMapper.selectFeVisitCustomerByCustomerId(candidate.getTargetId());
            if (customer == null)
            {
                throw new ServiceException("未找到对应的独立客户");
            }
            apply.setCustomerType(VisitConstants.CUSTOMER_TYPE_INDEPENDENT);
            apply.setContractDeptId(null);
            apply.setContractConfigId(null);
            apply.setCustomerId(customer.getCustomerId());
            apply.setCustomerNameSnapshot(customer.getCustomerName());
            apply.setContactPersonSnapshot(customer.getContactPerson());
            apply.setContactPhoneSnapshot(customer.getContactPhone());
        }
        else
        {
            throw new ServiceException("不支持的事件目标类型");
        }
        return apply;
    }

    private void checkEventScope(FeVisitPassiveEvent event)
    {
        if (SecurityUtils.isAdmin())
        {
            return;
        }
        List<Long> scopeDeptIds = sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId());
        if (event.getDeptId() == null || !scopeDeptIds.contains(event.getDeptId()))
        {
            throw new ServiceException("无权查看该被动事件");
        }
    }

    private List<Long> resolveScopeDeptIds(Long deptId)
    {
        if (deptId == null)
        {
            return null;
        }
        return sysDeptService.selectDeptAndChildrenIds(deptId);
    }

    private boolean isDeptInScope(Long deptId, List<Long> scopeDeptIds)
    {
        return deptId != null && (scopeDeptIds == null || scopeDeptIds.isEmpty() || scopeDeptIds.contains(deptId));
    }
}
