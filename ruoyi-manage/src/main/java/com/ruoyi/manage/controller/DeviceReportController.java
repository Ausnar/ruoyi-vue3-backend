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
import com.ruoyi.manage.domain.report.DeviceReportQuery;
import com.ruoyi.manage.service.IDeviceReportService;

/**
 * 设备报告。
 */
@RestController
@RequestMapping("/manage/device-report")
public class DeviceReportController extends BaseController
{
    @Autowired
    private IDeviceReportService deviceReportService;

    @PreAuthorize("@ss.hasPermi('manage:deviceReport:list')")
    @GetMapping("/preview")
    public AjaxResult preview(DeviceReportQuery query)
    {
        return success(deviceReportService.preview(query));
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceReport:export')")
    @Log(title = "设备报告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceReportQuery query)
    {
        deviceReportService.export(response, query);
    }
}
