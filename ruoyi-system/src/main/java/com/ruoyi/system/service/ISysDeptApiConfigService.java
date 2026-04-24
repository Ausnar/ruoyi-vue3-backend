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

    public Map<String, Object> getContractOverview();

    public List<Map<String, Object>> getStatusStatistics(String contractType);

    public List<Map<String, Object>> getExpireStatusStatistics(String contractType);

    public List<Map<String, Object>> getTopDeptStatistics(int limit, String contractType);

    public List<Map<String, Object>> getExpiryTrendStatistics(int months, String contractType);
}
