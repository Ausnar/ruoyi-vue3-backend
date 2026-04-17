package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.FeExtinguisher;

public interface FeExtinguisherMapper
{
    FeExtinguisher selectFeExtinguisherByExtinguisherId(Long extinguisherId);

    List<FeExtinguisher> selectFeExtinguisherList(FeExtinguisher feExtinguisher);

    FeExtinguisher selectByExternalExtinguisherId(Long externalExtinguisherId);

    FeExtinguisher selectByLabelCode(String labelCode);

    int insertFeExtinguisher(FeExtinguisher feExtinguisher);

    int updateFeExtinguisher(FeExtinguisher feExtinguisher);

    int updateDeptIdByExternalCompanyId(@Param("externalCompanyId") Long externalCompanyId, @Param("deptId") Long deptId);

    int updateExternalCompanyRef(@Param("sourceExternalCompanyId") Long sourceExternalCompanyId,
        @Param("targetExternalCompanyId") Long targetExternalCompanyId);

    int deleteFeExtinguisherByExtinguisherId(Long extinguisherId);

    int deleteFeExtinguisherByExtinguisherIds(Long[] extinguisherIds);
}
