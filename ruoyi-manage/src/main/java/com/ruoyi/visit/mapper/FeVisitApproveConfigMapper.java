package com.ruoyi.visit.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.visit.domain.FeVisitApproveConfig;

public interface FeVisitApproveConfigMapper
{
    FeVisitApproveConfig selectFeVisitApproveConfigByConfigId(Long configId);

    FeVisitApproveConfig selectFeVisitApproveConfigByDeptId(Long deptId);

    List<FeVisitApproveConfig> selectFeVisitApproveConfigList(FeVisitApproveConfig config);

    List<SysRole> selectRoleOptions();

    int countEnabledUsersByDeptAndRole(@Param("deptId") Long deptId, @Param("roleId") Long roleId);

    int insertFeVisitApproveConfig(FeVisitApproveConfig config);

    int updateFeVisitApproveConfig(FeVisitApproveConfig config);

    int deleteFeVisitApproveConfigByConfigIds(Long[] configIds);
}
