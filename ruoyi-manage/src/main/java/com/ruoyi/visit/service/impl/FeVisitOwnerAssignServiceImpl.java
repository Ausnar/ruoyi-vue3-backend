package com.ruoyi.visit.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.visit.constant.VisitConstants;
import com.ruoyi.visit.domain.FeVisitContractOption;
import com.ruoyi.visit.domain.FeVisitCustomer;
import com.ruoyi.visit.domain.FeVisitOwnerAssign;
import com.ruoyi.visit.mapper.FeVisitOwnerAssignMapper;
import com.ruoyi.visit.service.IFeVisitApplyService;
import com.ruoyi.visit.service.IFeVisitCustomerService;
import com.ruoyi.visit.service.IFeVisitOwnerAssignService;

@Service
public class FeVisitOwnerAssignServiceImpl implements IFeVisitOwnerAssignService
{
    @Autowired
    private FeVisitOwnerAssignMapper feVisitOwnerAssignMapper;

    @Autowired
    private IFeVisitApplyService feVisitApplyService;

    @Autowired
    private IFeVisitCustomerService feVisitCustomerService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeVisitOwnerAssign selectFeVisitOwnerAssignByAssignId(Long assignId)
    {
        FeVisitOwnerAssign assign = feVisitOwnerAssignMapper.selectFeVisitOwnerAssignByAssignId(assignId);
        if (assign == null)
        {
            return null;
        }
        checkAssignScope(assign);
        return assign;
    }

    @Override
    public List<FeVisitOwnerAssign> selectFeVisitOwnerAssignList(FeVisitOwnerAssign assign)
    {
        FeVisitOwnerAssign query = assign == null ? new FeVisitOwnerAssign() : assign;
        if (!SecurityUtils.isAdmin())
        {
            query.setScopeDeptIds(sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId()));
        }
        return feVisitOwnerAssignMapper.selectFeVisitOwnerAssignList(query);
    }

    @Override
    public int insertFeVisitOwnerAssign(FeVisitOwnerAssign assign)
    {
        validateAssign(assign, null);
        assign.setCreateBy(SecurityUtils.getUsername());
        assign.setCreateTime(DateUtils.getNowDate());
        assign.setUpdateBy(SecurityUtils.getUsername());
        assign.setUpdateTime(DateUtils.getNowDate());
        return feVisitOwnerAssignMapper.insertFeVisitOwnerAssign(assign);
    }

    @Override
    public int updateFeVisitOwnerAssign(FeVisitOwnerAssign assign)
    {
        if (assign == null || assign.getAssignId() == null)
        {
            throw new ServiceException("责任分配编号不能为空");
        }
        FeVisitOwnerAssign origin = selectFeVisitOwnerAssignByAssignId(assign.getAssignId());
        if (origin == null)
        {
            throw new ServiceException("未找到对应的责任分配");
        }
        validateAssign(assign, origin.getAssignId());
        assign.setCreateBy(origin.getCreateBy());
        assign.setCreateTime(origin.getCreateTime());
        assign.setUpdateBy(SecurityUtils.getUsername());
        assign.setUpdateTime(DateUtils.getNowDate());
        return feVisitOwnerAssignMapper.updateFeVisitOwnerAssign(assign);
    }

    @Override
    public int deleteFeVisitOwnerAssignByAssignIds(Long[] assignIds)
    {
        if (assignIds == null || assignIds.length == 0)
        {
            return 0;
        }
        for (Long assignId : assignIds)
        {
            FeVisitOwnerAssign assign = selectFeVisitOwnerAssignByAssignId(assignId);
            if (assign != null)
            {
                checkAssignScope(assign);
            }
        }
        return feVisitOwnerAssignMapper.deleteFeVisitOwnerAssignByAssignIds(assignIds);
    }

    @Override
    public List<FeVisitContractOption> selectContractOptions()
    {
        return feVisitApplyService.selectContractOptions();
    }

    @Override
    public FeVisitOwnerAssign requireSingleEnabledAssign(String targetType, Long targetId)
    {
        List<FeVisitOwnerAssign> assigns = feVisitOwnerAssignMapper.selectEnabledAssignsByTarget(targetType, targetId);
        if (assigns == null || assigns.isEmpty())
        {
            throw new ServiceException("未找到启用的负责人，请先维护责任分配");
        }
        if (assigns.size() > 1)
        {
            throw new ServiceException("当前目标存在多条启用负责人，请先清理责任分配");
        }
        return assigns.get(0);
    }

    private void validateAssign(FeVisitOwnerAssign assign, Long currentAssignId)
    {
        if (assign == null)
        {
            throw new ServiceException("责任分配数据不能为空");
        }
        if (StringUtils.isBlank(assign.getTargetType()) || assign.getTargetId() == null || assign.getOwnerUserId() == null)
        {
            throw new ServiceException("目标类型、目标对象和负责人不能为空");
        }
        if (StringUtils.isBlank(assign.getStatus()))
        {
            assign.setStatus(VisitConstants.OWNER_ASSIGN_STATUS_ENABLED);
        }
        validateTarget(assign.getTargetType(), assign.getTargetId());
        SysUser ownerUser = sysUserService.selectUserById(assign.getOwnerUserId());
        if (ownerUser == null || StringUtils.equals(ownerUser.getDelFlag(), "2") || !StringUtils.equals(ownerUser.getStatus(), "0"))
        {
            throw new ServiceException("负责人用户不存在或已停用");
        }
        assign.setOwnerDeptId(ownerUser.getDeptId());
        if (StringUtils.equals(assign.getStatus(), VisitConstants.OWNER_ASSIGN_STATUS_ENABLED))
        {
            List<FeVisitOwnerAssign> enabledAssigns = feVisitOwnerAssignMapper.selectEnabledAssignsByTarget(assign.getTargetType(), assign.getTargetId());
            for (FeVisitOwnerAssign enabledAssign : enabledAssigns)
            {
                if (currentAssignId == null || !currentAssignId.equals(enabledAssign.getAssignId()))
                {
                    throw new ServiceException("同一目标只允许存在一条启用负责人");
                }
            }
        }
    }

    private void validateTarget(String targetType, Long targetId)
    {
        if (VisitConstants.TARGET_TYPE_CONTRACT_DEPT.equals(targetType))
        {
            List<FeVisitContractOption> options = feVisitApplyService.selectContractOptionsByDeptIds(Arrays.asList(targetId));
            if (options.isEmpty())
            {
                throw new ServiceException("未找到对应的合同单位");
            }
            return;
        }
        if (VisitConstants.TARGET_TYPE_INDEPENDENT_CUSTOMER.equals(targetType))
        {
            FeVisitCustomer customer = feVisitCustomerService.selectFeVisitCustomerByCustomerId(targetId);
            if (customer == null)
            {
                throw new ServiceException("未找到对应的独立客户");
            }
            return;
        }
        throw new ServiceException("不支持的目标类型");
    }

    private void checkAssignScope(FeVisitOwnerAssign assign)
    {
        if (SecurityUtils.isAdmin())
        {
            return;
        }
        List<Long> scopeDeptIds = sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId());
        if (assign.getOwnerDeptId() == null || !scopeDeptIds.contains(assign.getOwnerDeptId()))
        {
            throw new ServiceException("无权访问该责任分配");
        }
    }
}
