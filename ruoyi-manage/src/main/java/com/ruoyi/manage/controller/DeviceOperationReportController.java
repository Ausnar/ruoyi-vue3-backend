package com.ruoyi.manage.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.report.DeviceOperationReportQuery;
import com.ruoyi.manage.service.IDeviceOperationReportService;

@RestController
@RequestMapping("/report/device-operation")
public class DeviceOperationReportController extends BaseController
{
    @Autowired
    private IDeviceOperationReportService deviceOperationReportService;

    @PreAuthorize("@ss.hasPermi('report:deviceOperation:list')")
    @GetMapping("/preview")
    public AjaxResult preview(DeviceOperationReportQuery query)
    {
        return success(deviceOperationReportService.preview(query));
    }

    @PreAuthorize("@ss.hasPermi('report:deviceOperation:export')")
    @Log(title = "设备运行报告Excel", businessType = BusinessType.EXPORT)
    @PostMapping("/export/excel")
    public void exportExcel(HttpServletResponse response, DeviceOperationReportQuery query)
    {
        deviceOperationReportService.exportExcel(response, query);
    }

    @PreAuthorize("@ss.hasPermi('report:deviceOperation:export')")
    @Log(title = "设备运行报告PDF", businessType = BusinessType.EXPORT)
    @PostMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response, DeviceOperationReportQuery query)
    {
        deviceOperationReportService.exportPdf(response, query);
    }
}
