package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.report.DeviceOperationReportPreview;
import com.ruoyi.manage.domain.report.DeviceOperationReportQuery;

public interface DeviceOperationReportMapper
{
    DeviceOperationReportPreview.UnitInfo selectUnitInfo(DeviceOperationReportQuery query);

    DeviceOperationReportPreview.AssetOverview selectAssetOverview(DeviceOperationReportQuery query);

    DeviceOperationReportPreview.SensorSummary selectSensorSummary(DeviceOperationReportQuery query);

    DeviceOperationReportPreview.GatewaySummary selectGatewaySummary(DeviceOperationReportQuery query);

    DeviceOperationReportPreview.ExtinguisherSummary selectExtinguisherSummary(DeviceOperationReportQuery query);

    DeviceOperationReportPreview.FirePointSummary selectFirePointSummary(DeviceOperationReportQuery query);
}
