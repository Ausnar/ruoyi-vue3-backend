package com.ruoyi.system.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysDeptApiConfig;
import com.ruoyi.system.mapper.SysDeptApiConfigMapper;
import com.ruoyi.system.service.ISysDeptApiConfigService;
import com.ruoyi.system.service.ISysDeptService;

@Service
public class SysDeptApiConfigServiceImpl implements ISysDeptApiConfigService
{
    private static final Set<String> PERSISTED_CONTRACT_TYPES = new HashSet<String>();
    private static final Set<String> QUERY_CONTRACT_TYPES = new HashSet<String>();

    static
    {
        PERSISTED_CONTRACT_TYPES.add("trial");
        PERSISTED_CONTRACT_TYPES.add("paid");
        QUERY_CONTRACT_TYPES.addAll(PERSISTED_CONTRACT_TYPES);
        QUERY_CONTRACT_TYPES.add("unset");
    }

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
        validateQueryContractType(sysDeptApiConfig == null ? null : sysDeptApiConfig.getContractType());
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
        validatePersistedContractType(sysDeptApiConfig.getContractType(), true);

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
        validatePersistedContractType(sysDeptApiConfig.getContractType(), false);

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

    @Override
    public Map<String, Object> getContractOverview()
    {
        return sysDeptApiConfigMapper.getContractOverview(getPermittedDeptIds());
    }

    @Override
    public List<Map<String, Object>> getStatusStatistics(String contractType)
    {
        validateQueryContractType(contractType);
        return sysDeptApiConfigMapper.getStatusStatistics(getPermittedDeptIds(), contractType);
    }

    @Override
    public List<Map<String, Object>> getExpireStatusStatistics(String contractType)
    {
        validateQueryContractType(contractType);
        return sysDeptApiConfigMapper.getExpireStatusStatistics(getPermittedDeptIds(), contractType);
    }

    @Override
    public List<Map<String, Object>> getTopDeptStatistics(int limit, String contractType)
    {
        validateQueryContractType(contractType);
        return sysDeptApiConfigMapper.getTopDeptStatistics(getPermittedDeptIds(), limit, contractType);
    }

    @Override
    public List<Map<String, Object>> getExpiryTrendStatistics(int months, String contractType)
    {
        validateQueryContractType(contractType);
        return sysDeptApiConfigMapper.getExpiryTrendStatistics(getPermittedDeptIds(), months, contractType);
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

    private void validatePersistedContractType(String contractType, boolean required)
    {
        if (required && contractType == null)
        {
            throw new ServiceException("合同性质不能为空");
        }
        if (contractType == null)
        {
            return;
        }
        if (!PERSISTED_CONTRACT_TYPES.contains(contractType))
        {
            throw new ServiceException("合同性质仅支持 trial 或 paid");
        }
    }

    private void validateQueryContractType(String contractType)
    {
        if (contractType == null || contractType.trim().isEmpty())
        {
            return;
        }
        if (!QUERY_CONTRACT_TYPES.contains(contractType))
        {
            throw new ServiceException("合同性质筛选值不合法");
        }
    }
}
