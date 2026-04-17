package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.manage.domain.FeCompanyDeptMapping;

public interface FeCompanyDeptMappingMapper
{
    FeCompanyDeptMapping selectFeCompanyDeptMappingByMappingId(Long mappingId);

    FeCompanyDeptMapping selectByExternalCompanyId(Long externalCompanyId);

    List<FeCompanyDeptMapping> selectFeCompanyDeptMappingList(FeCompanyDeptMapping mapping);

    int insertFeCompanyDeptMapping(FeCompanyDeptMapping mapping);

    int updateFeCompanyDeptMapping(FeCompanyDeptMapping mapping);

    int updateExternalCompanyRef(@Param("sourceExternalCompanyId") Long sourceExternalCompanyId,
        @Param("targetExternalCompanyId") Long targetExternalCompanyId,
        @Param("targetExternalCompanyName") String targetExternalCompanyName);

    int deleteFeCompanyDeptMappingByMappingIds(Long[] mappingIds);

    int deleteFeCompanyDeptMappingByMappingId(Long mappingId);

    int deleteByExternalCompanyId(Long externalCompanyId);
}
