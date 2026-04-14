package com.ruoyi.visit.service;

import java.util.List;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.visit.domain.FeVisitApproveConfig;

public interface IFeVisitApproveConfigService
{
    FeVisitApproveConfig selectFeVisitApproveConfigByConfigId(Long configId);

    List<FeVisitApproveConfig> selectFeVisitApproveConfigList(FeVisitApproveConfig config);

    List<SysRole> selectRoleOptions();

    int insertFeVisitApproveConfig(FeVisitApproveConfig config);

    int updateFeVisitApproveConfig(FeVisitApproveConfig config);

    int deleteFeVisitApproveConfigByConfigIds(Long[] configIds);
}
