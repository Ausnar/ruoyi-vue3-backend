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
import com.ruoyi.manage.domain.report.UnitDeviceReportQuery;
import com.ruoyi.manage.service.IUnitDeviceReportService;

@RestController
@RequestMapping("/report/unit-device")
public class UnitDeviceReportController extends BaseController
{
    @Autowired
    private IUnitDeviceReportService unitDeviceReportService;

    @PreAuthorize("@ss.hasPermi('report:unitDevice:list')")
    @GetMapping("/preview")
    public AjaxResult preview(UnitDeviceReportQuery query)
    {
        return success(unitDeviceReportService.preview(query));
    }

    @PreAuthorize("@ss.hasPermi('report:unitDevice:export')")
    @Log(title = "单位设备报告Excel", businessType = BusinessType.EXPORT)
    @PostMapping("/export/excel")
    public void exportExcel(HttpServletResponse response, UnitDeviceReportQuery query)
    {
        unitDeviceReportService.exportExcel(response, query);
    }

    @PreAuthorize("@ss.hasPermi('report:unitDevice:export')")
    @Log(title = "单位设备报告PDF", businessType = BusinessType.EXPORT)
    @PostMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response, UnitDeviceReportQuery query)
    {
        unitDeviceReportService.exportPdf(response, query);
    }
}
