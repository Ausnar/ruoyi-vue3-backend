package com.ruoyi.manage.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.manage.domain.FeGateway;
import com.ruoyi.manage.service.IFeGatewayService;

@RestController
@RequestMapping("/manage/gateway")
public class FeGatewayController extends BaseController
{
    @Autowired
    private IFeGatewayService feGatewayService;

    @PreAuthorize("@ss.hasPermi('manage:gateway:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeGateway feGateway)
    {
        startPage();
        List<FeGateway> list = feGatewayService.selectFeGatewayList(feGateway);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:gateway:export')")
    @Log(title = "网关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeGateway feGateway)
    {
        List<FeGateway> list = feGatewayService.selectFeGatewayList(feGateway);
        ExcelUtil<FeGateway> util = new ExcelUtil<FeGateway>(FeGateway.class);
        util.exportExcel(response, list, "网关信息数据");
    }

    @PreAuthorize("@ss.hasPermi('manage:gateway:query')")
    @GetMapping("/{gatewayId}")
    public AjaxResult getInfo(@PathVariable("gatewayId") Long gatewayId)
    {
        return success(feGatewayService.selectFeGatewayByGatewayId(gatewayId));
    }
}
