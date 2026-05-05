package com.ruoyi.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.FeDeviceWarning;
import com.ruoyi.manage.service.IFeDeviceWarningService;

/**
 * 设备预警只读Controller
 */
@RestController
@RequestMapping("/manage/device-warning")
public class FeDeviceWarningController extends BaseController
{
    @Autowired
    private IFeDeviceWarningService feDeviceWarningService;

    /**
     * 查询设备预警列表
     */
    @PreAuthorize("@ss.hasPermi('manage:deviceWarning:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeDeviceWarning feDeviceWarning)
    {
        startPage();
        List<FeDeviceWarning> list = feDeviceWarningService.selectFeDeviceWarningList(feDeviceWarning);
        return getDataTable(list);
    }

    /**
     * 查询首页设备预警概览
     */
    @PreAuthorize("@ss.hasPermi('dashboard:card:alarm')")
    @GetMapping("/dashboard")
    public AjaxResult dashboard()
    {
        return success(feDeviceWarningService.selectDashboardOverview(new FeDeviceWarning()));
    }

    /**
     * 获取设备预警详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:deviceWarning:query')")
    @GetMapping(value = "/{warningId}")
    public AjaxResult getInfo(@PathVariable("warningId") Long warningId)
    {
        return success(feDeviceWarningService.selectFeDeviceWarningByWarningId(warningId));
    }
}
