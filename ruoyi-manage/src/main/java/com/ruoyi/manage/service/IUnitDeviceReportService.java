package com.ruoyi.manage.service;

import javax.servlet.http.HttpServletResponse;

import com.ruoyi.manage.domain.report.UnitDeviceReportPreview;
import com.ruoyi.manage.domain.report.UnitDeviceReportQuery;

public interface IUnitDeviceReportService
{
    UnitDeviceReportPreview preview(UnitDeviceReportQuery query);

    void exportExcel(HttpServletResponse response, UnitDeviceReportQuery query);

    void exportPdf(HttpServletResponse response, UnitDeviceReportQuery query);
}
