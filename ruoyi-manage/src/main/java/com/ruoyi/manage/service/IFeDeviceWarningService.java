package com.ruoyi.manage.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.manage.domain.FeDeviceWarning;

public interface IFeDeviceWarningService
{
    String STATUS_PENDING = "pending";
    String STATUS_PROCESSING = "processing";
    String STATUS_RESOLVED = "resolved";
    String STATUS_FALSE_ALARM = "false_alarm";

    FeDeviceWarning selectFeDeviceWarningByWarningId(Long warningId);

    List<FeDeviceWarning> selectFeDeviceWarningList(FeDeviceWarning warning);

    Map<String, Object> selectDashboardOverview(FeDeviceWarning warning);

    FeDeviceWarning selectOpenWarningByObject(String warningType, String objectType, Long objectId);

    int insertFeDeviceWarning(FeDeviceWarning warning);

    int updateFeDeviceWarning(FeDeviceWarning warning);

    FeDeviceWarning saveOrRefreshOpenWarning(FeDeviceWarning warning, String operator);
}
