package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.manage.domain.FeCompanyDeptMapping;
import com.ruoyi.manage.domain.FeExternalCompany;

public interface IFeCompanyDeptMappingService
{
    FeCompanyDeptMapping selectFeCompanyDeptMappingByMappingId(Long mappingId);

    List<FeCompanyDeptMapping> selectFeCompanyDeptMappingList(FeCompanyDeptMapping mapping);

    int insertFeCompanyDeptMapping(FeCompanyDeptMapping mapping);

    int updateFeCompanyDeptMapping(FeCompanyDeptMapping mapping);

    int deleteFeCompanyDeptMappingByMappingIds(Long[] mappingIds);

    int deleteFeCompanyDeptMappingByMappingId(Long mappingId);

    List<FeExternalCompany> selectExternalCompanyOptions(FeExternalCompany company);
}
