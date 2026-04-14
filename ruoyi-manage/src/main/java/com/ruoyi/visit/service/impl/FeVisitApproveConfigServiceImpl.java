package com.ruoyi.visit.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.visit.domain.FeVisitApproveConfig;
import com.ruoyi.visit.mapper.FeVisitApproveConfigMapper;
import com.ruoyi.visit.service.IFeVisitApproveConfigService;

@Service
public class FeVisitApproveConfigServiceImpl implements IFeVisitApproveConfigService
{
    @Autowired
    private FeVisitApproveConfigMapper feVisitApproveConfigMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeVisitApproveConfig selectFeVisitApproveConfigByConfigId(Long configId)
    {
        FeVisitApproveConfig config = feVisitApproveConfigMapper.selectFeVisitApproveConfigByConfigId(configId);
        if (config == null)
        {
            return null;
        }
        checkConfigScope(config.getDeptId());
        return config;
    }

    @Override
    public List<FeVisitApproveConfig> selectFeVisitApproveConfigList(FeVisitApproveConfig config)
    {
        if (!SecurityUtils.isAdmin())
        {
            config.setScopeDeptIds(sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId()));
        }
        return feVisitApproveConfigMapper.selectFeVisitApproveConfigList(config);
    }

    @Override
    public List<SysRole> selectRoleOptions()
    {
        return feVisitApproveConfigMapper.selectRoleOptions();
    }

    @Override
    public int insertFeVisitApproveConfig(FeVisitApproveConfig config)
    {
        checkConfigScope(config.getDeptId());
        config.setCreateBy(SecurityUtils.getUsername());
        config.setCreateTime(DateUtils.getNowDate());
        config.setUpdateBy(SecurityUtils.getUsername());
        config.setUpdateTime(DateUtils.getNowDate());
        return feVisitApproveConfigMapper.insertFeVisitApproveConfig(config);
    }

    @Override
    public int updateFeVisitApproveConfig(FeVisitApproveConfig config)
    {
        FeVisitApproveConfig origin = selectFeVisitApproveConfigByConfigId(config.getConfigId());
        if (origin == null)
        {
            throw new ServiceException("未找到对应的审批配置");
        }
        checkConfigScope(origin.getDeptId());
        config.setDeptId(origin.getDeptId());
        config.setUpdateBy(SecurityUtils.getUsername());
        config.setUpdateTime(DateUtils.getNowDate());
        return feVisitApproveConfigMapper.updateFeVisitApproveConfig(config);
    }

    @Override
    public int deleteFeVisitApproveConfigByConfigIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            FeVisitApproveConfig config = feVisitApproveConfigMapper.selectFeVisitApproveConfigByConfigId(configId);
            if (config == null)
            {
                continue;
            }
            checkConfigScope(config.getDeptId());
        }
        return feVisitApproveConfigMapper.deleteFeVisitApproveConfigByConfigIds(configIds);
    }

    private void checkConfigScope(Long deptId)
    {
        if (deptId == null)
        {
            throw new ServiceException("部门不能为空");
        }
        if (!SecurityUtils.isAdmin())
        {
            List<Long> scopeDeptIds = sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId());
            if (!scopeDeptIds.contains(deptId))
            {
                throw new ServiceException("无权维护该部门的审批配置");
            }
        }
    }
}
