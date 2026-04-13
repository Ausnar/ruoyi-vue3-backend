package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.system.domain.SysDeptApiConfig;
import com.ruoyi.system.mapper.SysDeptApiConfigMapper;
import com.ruoyi.system.service.ISysDeptApiConfigService;
import com.ruoyi.system.service.ISysDeptService;

@Service
public class SysDeptApiConfigServiceImpl implements ISysDeptApiConfigService
{
    @Autowired
    private SysDeptApiConfigMapper sysDeptApiConfigMapper;

    @Autowired
    private ISysDeptService deptService;

    @Override
    public SysDeptApiConfig selectSysDeptApiConfigByConfigId(Long configId)
    {
        return sysDeptApiConfigMapper.selectSysDeptApiConfigByConfigId(configId);
    }

    @Override
    public List<SysDeptApiConfig> selectSysDeptApiConfigList(SysDeptApiConfig sysDeptApiConfig)
    {
        if (SecurityUtils.isAdmin())
        {
            return sysDeptApiConfigMapper.selectSysDeptApiConfigList(sysDeptApiConfig);
        }

        Long deptId = SecurityUtils.getDeptId();
        if (deptId != null)
        {
            List<Long> deptIds = deptService.selectDeptAndChildrenIds(deptId);
            sysDeptApiConfig.setDeptIds(deptIds);
        }
        return sysDeptApiConfigMapper.selectSysDeptApiConfigList(sysDeptApiConfig);
    }

    @Override
    public List<SysDeptApiConfig> selectActiveSysDeptApiConfigs()
    {
        return sysDeptApiConfigMapper.selectActiveSysDeptApiConfigs();
    }

    @Override
    public int insertSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig)
    {
        int count = sysDeptApiConfigMapper.checkDeptUnique(sysDeptApiConfig.getDeptId(), null);
        if (count > 0)
        {
            throw new ServiceException("该部门已存在 API 配置，一个部门只能配置一次");
        }

        sysDeptApiConfig.setCreateTime(DateUtils.getNowDate());
        return sysDeptApiConfigMapper.insertSysDeptApiConfig(sysDeptApiConfig);
    }

    @Override
    public int updateSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig)
    {
        int count = sysDeptApiConfigMapper.checkDeptUnique(sysDeptApiConfig.getDeptId(), sysDeptApiConfig.getConfigId());
        if (count > 0)
        {
            throw new ServiceException("该部门已存在 API 配置");
        }

        sysDeptApiConfig.setUpdateTime(DateUtils.getNowDate());
        return sysDeptApiConfigMapper.updateSysDeptApiConfig(sysDeptApiConfig);
    }

    @Override
    public int deleteSysDeptApiConfigByConfigIds(Long[] configIds)
    {
        return sysDeptApiConfigMapper.deleteSysDeptApiConfigByConfigIds(configIds);
    }

    @Override
    public int deleteSysDeptApiConfigByConfigId(Long configId)
    {
        return sysDeptApiConfigMapper.deleteSysDeptApiConfigByConfigId(configId);
    }

    private List<Long> getPermittedDeptIds()
    {
        if (SecurityUtils.isAdmin())
        {
            return null;
        }
        Long deptId = SecurityUtils.getDeptId();
        if (deptId != null)
        {
            return deptService.selectDeptAndChildrenIds(deptId);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getStatusStatistics()
    {
        return sysDeptApiConfigMapper.getStatusStatistics(getPermittedDeptIds());
    }

    @Override
    public List<Map<String, Object>> getExpireStatusStatistics()
    {
        return sysDeptApiConfigMapper.getExpireStatusStatistics(getPermittedDeptIds());
    }

    @Override
    public List<Map<String, Object>> getTopDeptStatistics(int limit)
    {
        return sysDeptApiConfigMapper.getTopDeptStatistics(getPermittedDeptIds(), limit);
    }

    @Override
    public List<Map<String, Object>> getExpiryTrendStatistics(int months)
    {
        return sysDeptApiConfigMapper.getExpiryTrendStatistics(getPermittedDeptIds(), months);
    }
}
