package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.report.DeviceReportOverview;
import com.ruoyi.manage.domain.report.DeviceReportQuery;
import com.ruoyi.manage.domain.report.DeviceReportRiskItem;
import com.ruoyi.manage.domain.report.DeviceReportUnitStat;

/**
 * 设备报告统计 Mapper。
 */
public interface DeviceReportMapper
{
    DeviceReportOverview selectOverview(DeviceReportQuery query);

    List<DeviceReportUnitStat> selectUnitStats(DeviceReportQuery query);

    List<DeviceReportRiskItem> selectFirePointRiskItems(DeviceReportQuery query);

    List<DeviceReportRiskItem> selectSensorRiskItems(DeviceReportQuery query);

    List<DeviceReportRiskItem> selectGatewayRiskItems(DeviceReportQuery query);

    List<DeviceReportRiskItem> selectExtinguisherRiskItems(DeviceReportQuery query);
}
