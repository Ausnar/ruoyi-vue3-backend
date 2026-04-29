package com.ruoyi.manage.service;

import javax.servlet.http.HttpServletResponse;

import com.ruoyi.manage.domain.report.DeviceReportPreview;
import com.ruoyi.manage.domain.report.DeviceReportQuery;

/**
 * 设备报告服务。
 */
public interface IDeviceReportService
{
    DeviceReportPreview preview(DeviceReportQuery query);

    void export(HttpServletResponse response, DeviceReportQuery query);
}
