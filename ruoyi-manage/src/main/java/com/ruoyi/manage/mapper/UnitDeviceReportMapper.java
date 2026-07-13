package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.report.UnitDeviceReportPreview;
import com.ruoyi.manage.domain.report.UnitDeviceReportQuery;

public interface UnitDeviceReportMapper
{
    UnitDeviceReportPreview.UnitInfo selectUnitInfo(UnitDeviceReportQuery query);

    UnitDeviceReportPreview.Overview selectOverview(UnitDeviceReportQuery query);

    List<UnitDeviceReportPreview.FirePointRow> selectFirePoints(UnitDeviceReportQuery query);

    List<UnitDeviceReportPreview.GatewayRow> selectGateways(UnitDeviceReportQuery query);

    List<UnitDeviceReportPreview.SensorRow> selectSensors(UnitDeviceReportQuery query);

    List<UnitDeviceReportPreview.ExtinguisherRow> selectExtinguishers(UnitDeviceReportQuery query);
}
