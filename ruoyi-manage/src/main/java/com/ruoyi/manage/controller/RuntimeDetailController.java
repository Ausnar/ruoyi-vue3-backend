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
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.report.RuntimeDetailQuery;
import com.ruoyi.manage.service.IRuntimeDetailService;

@RestController
@RequestMapping("/report/runtime-detail")
public class RuntimeDetailController extends BaseController
{
    @Autowired
    private IRuntimeDetailService runtimeDetailService;

    @PreAuthorize("@ss.hasPermi('report:runtimeDetail:list')")
    @GetMapping("/list")
    public TableDataInfo list(RuntimeDetailQuery query)
    {
        startPage();
        return getDataTable(runtimeDetailService.selectRuntimeDetailList(query));
    }

    @PreAuthorize("@ss.hasPermi('report:runtimeDetail:list')")
    @GetMapping("/fire-points")
    public AjaxResult firePoints(RuntimeDetailQuery query)
    {
        return success(runtimeDetailService.selectFirePointOptions(query));
    }

    @PreAuthorize("@ss.hasPermi('report:runtimeDetail:list')")
    @GetMapping("/devices")
    public AjaxResult devices(RuntimeDetailQuery query)
    {
        return success(runtimeDetailService.selectDeviceOptions(query));
    }

    @PreAuthorize("@ss.hasPermi('report:runtimeDetail:export')")
    @Log(title = "设备运行明细Excel", businessType = BusinessType.EXPORT)
    @PostMapping("/export/excel")
    public void exportExcel(HttpServletResponse response, RuntimeDetailQuery query)
    {
        runtimeDetailService.exportExcel(response, query);
    }

    @PreAuthorize("@ss.hasPermi('report:runtimeDetail:export')")
    @Log(title = "设备运行明细PDF", businessType = BusinessType.EXPORT)
    @PostMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response, RuntimeDetailQuery query)
    {
        runtimeDetailService.exportPdf(response, query);
    }
}
