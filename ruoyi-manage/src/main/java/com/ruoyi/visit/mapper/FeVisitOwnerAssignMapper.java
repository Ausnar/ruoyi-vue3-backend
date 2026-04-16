package com.ruoyi.visit.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.visit.domain.FeVisitOwnerAssign;

public interface FeVisitOwnerAssignMapper
{
    FeVisitOwnerAssign selectFeVisitOwnerAssignByAssignId(Long assignId);

    List<FeVisitOwnerAssign> selectFeVisitOwnerAssignList(FeVisitOwnerAssign assign);

    List<FeVisitOwnerAssign> selectEnabledAssignsByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    int countEnabledAssignByOwnerUserId(Long ownerUserId);

    int insertFeVisitOwnerAssign(FeVisitOwnerAssign assign);

    int updateFeVisitOwnerAssign(FeVisitOwnerAssign assign);

    int deleteFeVisitOwnerAssignByAssignIds(Long[] assignIds);
}
