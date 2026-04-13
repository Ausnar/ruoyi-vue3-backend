package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.FeApiConfigCompanyScope;

public interface FeApiConfigCompanyScopeMapper
{
    FeApiConfigCompanyScope selectByConfigIdAndExternalCompanyId(Long configId, Long externalCompanyId);

    List<FeApiConfigCompanyScope> selectFeApiConfigCompanyScopeList(FeApiConfigCompanyScope scope);

    int insertFeApiConfigCompanyScope(FeApiConfigCompanyScope scope);

    int updateFeApiConfigCompanyScope(FeApiConfigCompanyScope scope);
}
