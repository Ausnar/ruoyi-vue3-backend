package com.ruoyi.web.controller.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.service.IDashboardMapService;

@RestController
@RequestMapping("/manage/dashboard/map")
public class DashboardMapController extends BaseController
{
    @Autowired
    private IDashboardMapService dashboardMapService;

    @PreAuthorize("@ss.hasPermi('manage:point:list')")
    @GetMapping("/hierarchy")
    public AjaxResult hierarchy()
    {
        return success(dashboardMapService.getMapHierarchy());
    }
}
