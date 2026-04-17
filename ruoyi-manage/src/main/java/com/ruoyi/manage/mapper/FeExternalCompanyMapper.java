package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.domain.FeExternalCompanyMergeLog;

public interface FeExternalCompanyMapper
{
    FeExternalCompany selectByCompanyRecordId(Long companyRecordId);

    FeExternalCompany selectByExternalCompanyId(Long externalCompanyId);

    Long selectMinExternalCompanyId();

    List<FeExternalCompany> selectFeExternalCompanyList(FeExternalCompany company);

    List<FeExternalCompany> selectActiveExternalCompaniesForMerge(@Param("excludeCompanyRecordId") Long excludeCompanyRecordId);

    int insertFeExternalCompany(FeExternalCompany company);

    int updateFeExternalCompany(FeExternalCompany company);

    int insertMergeLog(FeExternalCompanyMergeLog mergeLog);
}
