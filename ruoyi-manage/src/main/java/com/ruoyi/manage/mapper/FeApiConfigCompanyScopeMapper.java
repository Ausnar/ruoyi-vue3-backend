package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.manage.domain.FeApiConfigCompanyScope;

public interface FeApiConfigCompanyScopeMapper
{
    FeApiConfigCompanyScope selectByConfigIdAndExternalCompanyId(Long configId, Long externalCompanyId);

    List<FeApiConfigCompanyScope> selectFeApiConfigCompanyScopeList(FeApiConfigCompanyScope scope);

    int updateExternalCompanyRef(@Param("sourceExternalCompanyId") Long sourceExternalCompanyId,
        @Param("targetExternalCompanyId") Long targetExternalCompanyId,
        @Param("targetExternalCompanyName") String targetExternalCompanyName);

    int updateExternalCompanyRefByScopeId(@Param("scopeId") Long scopeId,
        @Param("targetExternalCompanyId") Long targetExternalCompanyId,
        @Param("targetExternalCompanyName") String targetExternalCompanyName);

    int deleteByExternalCompanyId(Long externalCompanyId);

    int deleteByScopeId(Long scopeId);

    int insertFeApiConfigCompanyScope(FeApiConfigCompanyScope scope);

    int updateFeApiConfigCompanyScope(FeApiConfigCompanyScope scope);
}
