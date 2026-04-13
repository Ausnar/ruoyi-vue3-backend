package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.FeExternalCompany;

public interface FeExternalCompanyMapper
{
    FeExternalCompany selectByExternalCompanyId(Long externalCompanyId);

    List<FeExternalCompany> selectFeExternalCompanyList(FeExternalCompany company);

    int insertFeExternalCompany(FeExternalCompany company);

    int updateFeExternalCompany(FeExternalCompany company);
}
