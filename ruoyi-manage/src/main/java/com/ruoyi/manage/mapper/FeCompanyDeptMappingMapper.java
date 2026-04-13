package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.FeCompanyDeptMapping;

public interface FeCompanyDeptMappingMapper
{
    FeCompanyDeptMapping selectFeCompanyDeptMappingByMappingId(Long mappingId);

    FeCompanyDeptMapping selectByExternalCompanyId(Long externalCompanyId);

    List<FeCompanyDeptMapping> selectFeCompanyDeptMappingList(FeCompanyDeptMapping mapping);

    int insertFeCompanyDeptMapping(FeCompanyDeptMapping mapping);

    int updateFeCompanyDeptMapping(FeCompanyDeptMapping mapping);

    int deleteFeCompanyDeptMappingByMappingIds(Long[] mappingIds);

    int deleteFeCompanyDeptMappingByMappingId(Long mappingId);
}
