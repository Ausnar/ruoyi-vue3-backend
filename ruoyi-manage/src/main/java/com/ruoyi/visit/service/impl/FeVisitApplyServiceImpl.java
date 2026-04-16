package com.ruoyi.visit.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.visit.constant.VisitConstants;
import com.ruoyi.visit.domain.FeVisitApply;
import com.ruoyi.visit.domain.FeVisitApplyLog;
import com.ruoyi.visit.domain.FeVisitApproveConfig;
import com.ruoyi.visit.domain.FeVisitContractOption;
import com.ruoyi.visit.domain.FeVisitCustomer;
import com.ruoyi.visit.domain.dto.VisitAuditRequest;
import com.ruoyi.visit.domain.dto.VisitFeedbackRequest;
import com.ruoyi.visit.mapper.FeVisitApplyLogMapper;
import com.ruoyi.visit.mapper.FeVisitApplyMapper;
import com.ruoyi.visit.mapper.FeVisitApproveConfigMapper;
import com.ruoyi.visit.mapper.FeVisitCustomerMapper;
import com.ruoyi.visit.service.IFeVisitApplyService;

@Service
public class FeVisitApplyServiceImpl implements IFeVisitApplyService
{
    private static final DecimalFormat VISIT_NO_FORMAT = new DecimalFormat("0000");

    @Autowired
    private FeVisitApplyMapper feVisitApplyMapper;

    @Autowired
    private FeVisitApplyLogMapper feVisitApplyLogMapper;

    @Autowired
    private FeVisitApproveConfigMapper feVisitApproveConfigMapper;

    @Autowired
    private FeVisitCustomerMapper feVisitCustomerMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public List<FeVisitApply> selectMyVisitApplyList(FeVisitApply apply)
    {
        apply.setApplicantUserId(SecurityUtils.getUserId());
        List<FeVisitApply> list = feVisitApplyMapper.selectMyVisitApplyList(apply);
        fillCanApprove(list);
        return list;
    }

    @Override
    public List<FeVisitApply> selectApproveVisitApplyList(FeVisitApply apply)
    {
        if (StringUtils.isBlank(apply.getVisitMode()))
        {
            apply.setVisitMode(VisitConstants.VISIT_MODE_ACTIVE);
        }
        if (!SecurityUtils.isAdmin())
        {
            apply.setScopeDeptIds(sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId()));
        }
        List<FeVisitApply> list = feVisitApplyMapper.selectApproveVisitApplyList(apply);
        fillCanApprove(list);
        return list;
    }

    @Override
    public FeVisitApply selectFeVisitApplyByVisitId(Long visitId)
    {
        FeVisitApply apply = feVisitApplyMapper.selectFeVisitApplyByVisitId(visitId);
        if (apply == null)
        {
            return null;
        }
        checkVisitScope(apply);
        apply.setCanApprove(canApprove(apply));
        return apply;
    }

    @Override
    public List<FeVisitApplyLog> selectFeVisitApplyLogListByVisitId(Long visitId)
    {
        FeVisitApply apply = selectFeVisitApplyByVisitId(visitId);
        if (apply == null)
        {
            return new ArrayList<FeVisitApplyLog>();
        }
        return feVisitApplyLogMapper.selectFeVisitApplyLogListByVisitId(visitId);
    }

    @Override
    public List<FeVisitContractOption> selectContractOptions()
    {
        return feVisitApplyMapper.selectContractOptions(resolveScopeDeptIds());
    }

    @Override
    public List<FeVisitContractOption> selectContractOptionsByDeptIds(List<Long> deptIds)
    {
        if (CollectionUtils.isEmpty(deptIds))
        {
            return new ArrayList<FeVisitContractOption>();
        }
        return feVisitApplyMapper.selectContractOptionsByDeptIds(deptIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitVisitApply(FeVisitApply apply)
    {
        validateSubject(apply);
        FeVisitApproveConfig approveConfig = requireActiveApproveConfig(SecurityUtils.getDeptId());
        fillCustomerSnapshot(apply);
        apply.setVisitNo(buildVisitNo());
        apply.setApplicantUserId(SecurityUtils.getUserId());
        apply.setApplicantDeptId(SecurityUtils.getDeptId());
        apply.setVisitMode(VisitConstants.VISIT_MODE_ACTIVE);
        apply.setSourceType(VisitConstants.SOURCE_TYPE_MANUAL);
        apply.setSourceEventId(null);
        apply.setStatus(VisitConstants.STATUS_PENDING_APPROVE);
        clearTriggerSnapshot(apply);
        apply.setApproveRoleIdSnapshot(approveConfig.getRoleId());
        apply.setApproveUserId(null);
        apply.setApproveTime(null);
        apply.setApproveComment(null);
        apply.setActualStartTime(null);
        apply.setActualEndTime(null);
        apply.setResultDesc(null);
        apply.setVisitConclusion(null);
        apply.setIntentionLevel(null);
        apply.setNextFollowTime(null);
        apply.setResultAttachments(null);
        apply.setDelFlag("0");
        apply.setCreateBy(SecurityUtils.getUsername());
        apply.setCreateTime(DateUtils.getNowDate());
        apply.setUpdateBy(SecurityUtils.getUsername());
        apply.setUpdateTime(DateUtils.getNowDate());
        int rows = feVisitApplyMapper.insertFeVisitApply(apply);
        insertLog(apply.getVisitId(), VisitConstants.ACTION_SUBMIT, null, VisitConstants.STATUS_PENDING_APPROVE, "提交申请");
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resubmitVisitApply(FeVisitApply apply)
    {
        FeVisitApply origin = requireOwnerVisit(apply.getVisitId());
        if (!VisitConstants.STATUS_REJECTED.equals(origin.getStatus()) && !VisitConstants.STATUS_WITHDRAWN.equals(origin.getStatus()))
        {
            throw new ServiceException("当前状态不允许重提");
        }
        validateSubject(apply);
        FeVisitApproveConfig approveConfig = requireActiveApproveConfig(SecurityUtils.getDeptId());
        String fromStatus = origin.getStatus();
        mergeSubjectFields(origin, apply);
        fillCustomerSnapshot(origin);
        origin.setVisitMode(VisitConstants.VISIT_MODE_ACTIVE);
        origin.setSourceType(VisitConstants.SOURCE_TYPE_MANUAL);
        origin.setSourceEventId(null);
        origin.setStatus(VisitConstants.STATUS_PENDING_APPROVE);
        clearTriggerSnapshot(origin);
        origin.setApproveRoleIdSnapshot(approveConfig.getRoleId());
        origin.setApproveUserId(null);
        origin.setApproveTime(null);
        origin.setApproveComment(null);
        origin.setActualStartTime(null);
        origin.setActualEndTime(null);
        origin.setResultDesc(null);
        origin.setVisitConclusion(null);
        origin.setIntentionLevel(null);
        origin.setNextFollowTime(null);
        origin.setResultAttachments(null);
        origin.setUpdateBy(SecurityUtils.getUsername());
        origin.setUpdateTime(DateUtils.getNowDate());
        int rows = feVisitApplyMapper.updateFeVisitApply(origin);
        insertLog(origin.getVisitId(), VisitConstants.ACTION_RESUBMIT, fromStatus, VisitConstants.STATUS_PENDING_APPROVE, "撤回/驳回后重提");
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int withdrawVisitApply(Long visitId)
    {
        FeVisitApply apply = requireOwnerVisit(visitId);
        if (!VisitConstants.STATUS_PENDING_APPROVE.equals(apply.getStatus()))
        {
            throw new ServiceException("仅待审批状态可撤回");
        }
        apply.setStatus(VisitConstants.STATUS_WITHDRAWN);
        apply.setUpdateBy(SecurityUtils.getUsername());
        apply.setUpdateTime(DateUtils.getNowDate());
        int rows = feVisitApplyMapper.updateFeVisitApply(apply);
        insertLog(visitId, VisitConstants.ACTION_WITHDRAW, VisitConstants.STATUS_PENDING_APPROVE, VisitConstants.STATUS_WITHDRAWN, "申请人撤回");
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveVisitApply(Long visitId, VisitAuditRequest request)
    {
        FeVisitApply apply = selectFeVisitApplyByVisitId(visitId);
        requireApproveAllowed(apply);
        apply.setStatus(VisitConstants.STATUS_APPROVED_PENDING_FEEDBACK);
        apply.setApproveUserId(SecurityUtils.getUserId());
        apply.setApproveTime(DateUtils.getNowDate());
        apply.setApproveComment(request == null ? null : request.getApproveComment());
        apply.setUpdateBy(SecurityUtils.getUsername());
        apply.setUpdateTime(DateUtils.getNowDate());
        int rows = feVisitApplyMapper.updateFeVisitApply(apply);
        insertLog(visitId, VisitConstants.ACTION_APPROVE, VisitConstants.STATUS_PENDING_APPROVE, VisitConstants.STATUS_APPROVED_PENDING_FEEDBACK, apply.getApproveComment());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rejectVisitApply(Long visitId, VisitAuditRequest request)
    {
        FeVisitApply apply = selectFeVisitApplyByVisitId(visitId);
        requireApproveAllowed(apply);
        apply.setStatus(VisitConstants.STATUS_REJECTED);
        apply.setApproveUserId(SecurityUtils.getUserId());
        apply.setApproveTime(DateUtils.getNowDate());
        apply.setApproveComment(request == null ? null : request.getApproveComment());
        apply.setUpdateBy(SecurityUtils.getUsername());
        apply.setUpdateTime(DateUtils.getNowDate());
        int rows = feVisitApplyMapper.updateFeVisitApply(apply);
        insertLog(visitId, VisitConstants.ACTION_REJECT, VisitConstants.STATUS_PENDING_APPROVE, VisitConstants.STATUS_REJECTED, apply.getApproveComment());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int feedbackVisitApply(VisitFeedbackRequest request)
    {
        FeVisitApply apply = requireOwnerVisit(request.getVisitId());
        if (!VisitConstants.STATUS_APPROVED_PENDING_FEEDBACK.equals(apply.getStatus()))
        {
            throw new ServiceException("当前状态不允许结果回填");
        }
        if (request.getActualStartTime() != null && request.getActualEndTime() != null && request.getActualEndTime().before(request.getActualStartTime()))
        {
            throw new ServiceException("实际结束时间不能早于实际开始时间");
        }
        if (StringUtils.isBlank(request.getResultDesc()))
        {
            throw new ServiceException("请填写拜访结果说明");
        }
        apply.setActualStartTime(request.getActualStartTime());
        apply.setActualEndTime(request.getActualEndTime());
        apply.setResultDesc(request.getResultDesc());
        apply.setVisitConclusion(request.getVisitConclusion());
        apply.setIntentionLevel(request.getIntentionLevel());
        apply.setNextFollowTime(request.getNextFollowTime());
        apply.setResultAttachments(request.getResultAttachments());
        apply.setRemark(request.getRemark());
        apply.setStatus(VisitConstants.STATUS_COMPLETED);
        apply.setUpdateBy(SecurityUtils.getUsername());
        apply.setUpdateTime(DateUtils.getNowDate());
        int rows = feVisitApplyMapper.updateFeVisitApply(apply);
        insertLog(apply.getVisitId(), VisitConstants.ACTION_FEEDBACK, VisitConstants.STATUS_APPROVED_PENDING_FEEDBACK, VisitConstants.STATUS_COMPLETED, "完成结果回填");
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createPassiveVisitApply(FeVisitApply apply, Long operatorUserId, Long operatorDeptId, String operatorName)
    {
        if (apply == null)
        {
            throw new ServiceException("被动拜访数据不能为空");
        }
        if (apply.getApplicantUserId() == null || apply.getApplicantDeptId() == null)
        {
            throw new ServiceException("被动拜访缺少负责人信息");
        }
        apply.setVisitNo(buildVisitNo());
        apply.setVisitMode(VisitConstants.VISIT_MODE_PASSIVE);
        apply.setSourceType(VisitConstants.SOURCE_TYPE_GATEWAY_DISPLACEMENT);
        apply.setStatus(VisitConstants.STATUS_APPROVED_PENDING_FEEDBACK);
        apply.setApproveRoleIdSnapshot(null);
        apply.setApproveUserId(null);
        apply.setApproveTime(null);
        apply.setApproveComment(null);
        apply.setActualStartTime(null);
        apply.setActualEndTime(null);
        apply.setResultDesc(null);
        apply.setVisitConclusion(null);
        apply.setIntentionLevel(null);
        apply.setNextFollowTime(null);
        apply.setResultAttachments(null);
        apply.setDelFlag("0");
        apply.setCreateBy(operatorName);
        apply.setCreateTime(DateUtils.getNowDate());
        apply.setUpdateBy(operatorName);
        apply.setUpdateTime(DateUtils.getNowDate());
        int rows = feVisitApplyMapper.insertFeVisitApply(apply);
        insertLog(apply.getVisitId(), VisitConstants.ACTION_PASSIVE_CONVERT, null,
            VisitConstants.STATUS_APPROVED_PENDING_FEEDBACK, "被动事件确认转单",
            operatorUserId, operatorDeptId, operatorName);
        return rows;
    }

    private FeVisitApply requireOwnerVisit(Long visitId)
    {
        FeVisitApply apply = feVisitApplyMapper.selectFeVisitApplyByVisitId(visitId);
        if (apply == null || !SecurityUtils.getUserId().equals(apply.getApplicantUserId()))
        {
            throw new ServiceException("只能操作自己的拜访申请");
        }
        return apply;
    }

    private void validateSubject(FeVisitApply apply)
    {
        if (StringUtils.isBlank(apply.getCustomerType()))
        {
            throw new ServiceException("请选择客户类型");
        }
        if (apply.getPlannedStartTime() == null || apply.getPlannedEndTime() == null)
        {
            throw new ServiceException("请填写计划拜访时间");
        }
        if (apply.getPlannedEndTime().before(apply.getPlannedStartTime()))
        {
            throw new ServiceException("计划结束时间不能早于计划开始时间");
        }
        if (StringUtils.isBlank(apply.getVisitReason()))
        {
            throw new ServiceException("请填写拜访事由");
        }
        if (VisitConstants.CUSTOMER_TYPE_CONTRACT.equals(apply.getCustomerType()) && apply.getContractConfigId() == null)
        {
            throw new ServiceException("请选择合同客户");
        }
        if (VisitConstants.CUSTOMER_TYPE_INDEPENDENT.equals(apply.getCustomerType()) && apply.getCustomerId() == null)
        {
            throw new ServiceException("请选择独立客户");
        }
    }

    private void fillCustomerSnapshot(FeVisitApply apply)
    {
        if (VisitConstants.CUSTOMER_TYPE_CONTRACT.equals(apply.getCustomerType()))
        {
            FeVisitContractOption option = feVisitApplyMapper.selectContractOptionByConfigId(apply.getContractConfigId());
            if (option == null)
            {
                throw new ServiceException("未找到对应的合同客户");
            }
            checkContractScope(option);
            apply.setContractDeptId(option.getDeptId());
            apply.setCustomerId(null);
            apply.setCustomerNameSnapshot(option.getDeptName());
            apply.setContactPersonSnapshot(option.getContactPerson());
            apply.setContactPhoneSnapshot(option.getContactPhone());
            return;
        }
        if (VisitConstants.CUSTOMER_TYPE_INDEPENDENT.equals(apply.getCustomerType()))
        {
            FeVisitCustomer customer = feVisitCustomerMapper.selectFeVisitCustomerByCustomerId(apply.getCustomerId());
            if (customer == null || "2".equals(customer.getDelFlag()))
            {
                throw new ServiceException("未找到对应的独立客户");
            }
            checkCustomerScope(customer);
            apply.setContractDeptId(null);
            apply.setContractConfigId(null);
            apply.setCustomerNameSnapshot(customer.getCustomerName());
            apply.setContactPersonSnapshot(customer.getContactPerson());
            apply.setContactPhoneSnapshot(customer.getContactPhone());
            return;
        }
        throw new ServiceException("不支持的客户类型");
    }

    private void mergeSubjectFields(FeVisitApply target, FeVisitApply source)
    {
        target.setCustomerType(source.getCustomerType());
        target.setContractConfigId(source.getContractConfigId());
        target.setCustomerId(source.getCustomerId());
        target.setVisitAddress(source.getVisitAddress());
        target.setPlannedStartTime(source.getPlannedStartTime());
        target.setPlannedEndTime(source.getPlannedEndTime());
        target.setVisitReason(source.getVisitReason());
        target.setVisitTarget(source.getVisitTarget());
        target.setCompanionMembers(source.getCompanionMembers());
        target.setRemark(source.getRemark());
    }

    private void clearTriggerSnapshot(FeVisitApply apply)
    {
        apply.setTriggerGatewayId(null);
        apply.setTriggerFirePointId(null);
        apply.setTriggerFromLongitude(null);
        apply.setTriggerFromLatitude(null);
        apply.setTriggerToLongitude(null);
        apply.setTriggerToLatitude(null);
        apply.setTriggerDistanceM(null);
    }

    private FeVisitApproveConfig requireActiveApproveConfig(Long deptId)
    {
        FeVisitApproveConfig config = feVisitApproveConfigMapper.selectFeVisitApproveConfigByDeptId(deptId);
        if (config == null || !"0".equals(config.getStatus()))
        {
            throw new ServiceException("当前部门未配置可用的审批角色，暂时无法提交");
        }
        int enabledUserCount = feVisitApproveConfigMapper.countEnabledUsersByDeptAndRole(deptId, config.getRoleId());
        if (enabledUserCount <= 0)
        {
            throw new ServiceException("当前部门审批角色下没有启用用户，暂时无法提交");
        }
        return config;
    }

    private List<Long> resolveScopeDeptIds()
    {
        if (SecurityUtils.isAdmin())
        {
            return null;
        }
        return sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId());
    }

    private void checkVisitScope(FeVisitApply apply)
    {
        if (SecurityUtils.isAdmin())
        {
            return;
        }
        if (SecurityUtils.getUserId().equals(apply.getApplicantUserId()))
        {
            return;
        }
        List<Long> scopeDeptIds = resolveScopeDeptIds();
        if (apply.getApplicantDeptId() == null || !scopeDeptIds.contains(apply.getApplicantDeptId()))
        {
            throw new ServiceException("无权查看该拜访申请");
        }
    }

    private void checkContractScope(FeVisitContractOption option)
    {
        if (SecurityUtils.isAdmin())
        {
            return;
        }
        List<Long> scopeDeptIds = resolveScopeDeptIds();
        if (option.getDeptId() == null || !scopeDeptIds.contains(option.getDeptId()))
        {
            throw new ServiceException("无权选择该合同客户");
        }
    }

    private void checkCustomerScope(FeVisitCustomer customer)
    {
        if (SecurityUtils.isAdmin())
        {
            return;
        }
        List<Long> scopeDeptIds = resolveScopeDeptIds();
        if (customer.getDeptId() == null || !scopeDeptIds.contains(customer.getDeptId()))
        {
            throw new ServiceException("无权选择该独立客户");
        }
    }

    private void requireApproveAllowed(FeVisitApply apply)
    {
        if (apply == null)
        {
            throw new ServiceException("未找到对应的拜访申请");
        }
        if (!canApprove(apply))
        {
            throw new ServiceException("当前用户无权审批该申请");
        }
    }

    private boolean canApprove(FeVisitApply apply)
    {
        if (apply == null)
        {
            return false;
        }
        if (!VisitConstants.STATUS_PENDING_APPROVE.equals(apply.getStatus()))
        {
            return false;
        }
        if (!SecurityUtils.isAdmin() && !SecurityUtils.getDeptId().equals(apply.getApplicantDeptId()))
        {
            return false;
        }
        if (SecurityUtils.isAdmin())
        {
            return true;
        }
        Long roleId = apply.getApproveRoleIdSnapshot();
        if (roleId == null)
        {
            return false;
        }
        return currentRoleIds().contains(roleId);
    }

    private Set<Long> currentRoleIds()
    {
        Set<Long> roleIds = new HashSet<Long>();
        List<SysRole> roles = SecurityUtils.getLoginUser().getUser().getRoles();
        if (CollectionUtils.isEmpty(roles))
        {
            return roleIds;
        }
        for (SysRole role : roles)
        {
            if (role.getRoleId() != null)
            {
                roleIds.add(role.getRoleId());
            }
        }
        return roleIds;
    }

    private void fillCanApprove(List<FeVisitApply> list)
    {
        for (FeVisitApply item : list)
        {
            item.setCanApprove(canApprove(item));
        }
    }

    private synchronized String buildVisitNo()
    {
        String prefix = "VF" + DateUtils.dateTimeNow("yyyyMMdd");
        String latestVisitNo = feVisitApplyMapper.selectLatestVisitNo(prefix);
        int nextNumber = 1;
        if (StringUtils.isNotBlank(latestVisitNo) && latestVisitNo.length() >= prefix.length() + 4)
        {
            nextNumber = Integer.parseInt(latestVisitNo.substring(prefix.length())) + 1;
        }
        return prefix + VISIT_NO_FORMAT.format(nextNumber);
    }

    private void insertLog(Long visitId, String actionType, String fromStatus, String toStatus, String actionComment)
    {
        insertLog(visitId, actionType, fromStatus, toStatus, actionComment,
            SecurityUtils.getUserId(), SecurityUtils.getDeptId(), SecurityUtils.getUsername());
    }

    private void insertLog(Long visitId, String actionType, String fromStatus, String toStatus, String actionComment,
                           Long operatorUserId, Long operatorDeptId, String operatorName)
    {
        FeVisitApplyLog log = new FeVisitApplyLog();
        log.setVisitId(visitId);
        log.setActionType(actionType);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setOperatorUserId(operatorUserId);
        log.setOperatorDeptId(operatorDeptId);
        log.setActionTime(new Date());
        log.setActionComment(actionComment);
        log.setCreateBy(operatorName);
        log.setCreateTime(DateUtils.getNowDate());
        feVisitApplyLogMapper.insertFeVisitApplyLog(log);
    }
}
