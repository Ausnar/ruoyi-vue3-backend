package com.ruoyi.manage.service;

import javax.servlet.http.HttpServletResponse;

import com.ruoyi.manage.domain.report.DeviceOperationReportPreview;
import com.ruoyi.manage.domain.report.DeviceOperationReportQuery;

public interface IDeviceOperationReportService
{
    DeviceOperationReportPreview preview(DeviceOperationReportQuery query);

    void exportExcel(HttpServletResponse response, DeviceOperationReportQuery query);

    void exportPdf(HttpServletResponse response, DeviceOperationReportQuery query);
}
