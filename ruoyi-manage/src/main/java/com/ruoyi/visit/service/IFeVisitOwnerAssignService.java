package com.ruoyi.visit.service;

import java.util.List;
import com.ruoyi.visit.domain.FeVisitContractOption;
import com.ruoyi.visit.domain.FeVisitOwnerAssign;

public interface IFeVisitOwnerAssignService
{
    FeVisitOwnerAssign selectFeVisitOwnerAssignByAssignId(Long assignId);

    List<FeVisitOwnerAssign> selectFeVisitOwnerAssignList(FeVisitOwnerAssign assign);

    int insertFeVisitOwnerAssign(FeVisitOwnerAssign assign);

    int updateFeVisitOwnerAssign(FeVisitOwnerAssign assign);

    int deleteFeVisitOwnerAssignByAssignIds(Long[] assignIds);

    List<FeVisitContractOption> selectContractOptions();

    FeVisitOwnerAssign requireSingleEnabledAssign(String targetType, Long targetId);
}
