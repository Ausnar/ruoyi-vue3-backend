package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysDeptApiConfig;

public interface ISysDeptApiConfigService
{
    public SysDeptApiConfig selectSysDeptApiConfigByConfigId(Long configId);

    public List<SysDeptApiConfig> selectSysDeptApiConfigList(SysDeptApiConfig sysDeptApiConfig);

    public List<SysDeptApiConfig> selectActiveSysDeptApiConfigs();

    public int insertSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig);

    public int updateSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig);

    public int deleteSysDeptApiConfigByConfigIds(Long[] configIds);

    public int deleteSysDeptApiConfigByConfigId(Long configId);

    public List<Map<String, Object>> getStatusStatistics();

    public List<Map<String, Object>> getExpireStatusStatistics();

    public List<Map<String, Object>> getTopDeptStatistics(int limit);

    public List<Map<String, Object>> getExpiryTrendStatistics(int months);
}
