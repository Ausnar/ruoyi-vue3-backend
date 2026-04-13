package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysDeptApiConfig;
import org.apache.ibatis.annotations.Param;

public interface SysDeptApiConfigMapper
{
    public SysDeptApiConfig selectSysDeptApiConfigByConfigId(Long configId);

    public List<SysDeptApiConfig> selectSysDeptApiConfigList(SysDeptApiConfig sysDeptApiConfig);

    public List<SysDeptApiConfig> selectActiveSysDeptApiConfigs();

    public int insertSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig);

    public int updateSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig);

    public int deleteSysDeptApiConfigByConfigId(Long configId);

    public int deleteSysDeptApiConfigByConfigIds(Long[] configIds);

    public int checkDeptUnique(@Param("deptId") Long deptId, @Param("configId") Long configId);

    public List<Map<String, Object>> getStatusStatistics(@Param("deptIds") List<Long> deptIds);

    public List<Map<String, Object>> getExpireStatusStatistics(@Param("deptIds") List<Long> deptIds);

    public List<Map<String, Object>> getTopDeptStatistics(@Param("deptIds") List<Long> deptIds, @Param("limit") int limit);

    public List<Map<String, Object>> getExpiryTrendStatistics(@Param("deptIds") List<Long> deptIds, @Param("months") int months);
}
