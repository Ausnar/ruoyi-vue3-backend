package com.ruoyi.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.manage.service.IFeDeviceDeptService;

@RestController
@RequestMapping("/manage/device/dept")
public class FeDeviceDeptController extends BaseController
{
    @Autowired
    private IFeDeviceDeptService deviceDeptService;

    @PreAuthorize("@ss.hasAnyPermi('manage:sensor:list,manage:extinguisher:list,manage:point:list,manage:gateway:list,manage:deviceWarning:list,manage:deviceReport:list')")
    @GetMapping("/tree")
    public AjaxResult tree(SysDept dept)
    {
        return success(deviceDeptService.selectDeviceDeptTreeList(dept));
    }
}
