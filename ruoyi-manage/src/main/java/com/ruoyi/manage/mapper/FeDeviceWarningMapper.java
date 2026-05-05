package com.ruoyi.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.manage.domain.FeDeviceWarning;

public interface FeDeviceWarningMapper
{
    FeDeviceWarning selectFeDeviceWarningByWarningId(Long warningId);

    List<FeDeviceWarning> selectFeDeviceWarningList(FeDeviceWarning warning);

    int countOpenWarnings(FeDeviceWarning warning);

    int countOpenSevereWarnings(FeDeviceWarning warning);

    List<FeDeviceWarning> selectRecentDashboardWarnings(FeDeviceWarning warning);

    FeDeviceWarning selectOpenWarningByObject(@Param("warningType") String warningType,
                                              @Param("objectType") String objectType,
                                              @Param("objectId") Long objectId);

    List<FeDeviceWarning> selectSuspectedFireCandidates(@Param("sourceDeptId") Long sourceDeptId);

    List<FeDeviceWarning> selectLowBatteryCandidates(@Param("sourceDeptId") Long sourceDeptId);

    List<FeDeviceWarning> selectLowPressureCandidates(@Param("sourceDeptId") Long sourceDeptId);

    List<FeDeviceWarning> selectHighPressureCandidates(@Param("sourceDeptId") Long sourceDeptId);

    List<FeDeviceWarning> selectInsufficientExtinguisherCandidates(@Param("sourceDeptId") Long sourceDeptId);

    List<FeDeviceWarning> selectExpiredExtinguisherCandidates(@Param("sourceDeptId") Long sourceDeptId);

    List<FeDeviceWarning> selectAbnormalTemperatureCandidates(@Param("sourceDeptId") Long sourceDeptId);

    int insertFeDeviceWarning(FeDeviceWarning warning);

    int updateFeDeviceWarning(FeDeviceWarning warning);
}
