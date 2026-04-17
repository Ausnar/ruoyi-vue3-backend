package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.domain.dto.FeExternalCompanyMergeRequest;

public interface IFeExternalCompanyService
{
    FeExternalCompany selectFeExternalCompanyByCompanyRecordId(Long companyRecordId);

    List<FeExternalCompany> selectFeExternalCompanyList(FeExternalCompany company);

    int insertFeExternalCompany(FeExternalCompany company);

    int updateFeExternalCompany(FeExternalCompany company);

    int toggleFeExternalCompany(Long companyRecordId);

    List<FeExternalCompany> selectDuplicateCandidates(Long companyRecordId);

    int mergeFeExternalCompany(FeExternalCompanyMergeRequest request);
}
