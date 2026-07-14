package com.ruoyi.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.service.IFeDeviceDeptService;
import com.ruoyi.system.service.ISysDeptService;

@RestController
@RequestMapping("/manage/device/dept")
public class FeDeviceDeptController extends BaseController
{
    @Autowired
    private IFeDeviceDeptService deviceDeptService;

    @Autowired
    private ISysDeptService sysDeptService;

    @PreAuthorize("@ss.hasAnyPermi('manage:sensor:list,manage:extinguisher:list,manage:point:list,manage:gateway:list,manage:deviceWarning:list,manage:deviceReport:list,report:unitDevice:list,report:runtimeDetail:list')")
    @GetMapping("/tree")
    public AjaxResult tree(SysDept dept)
    {
        AjaxResult result = success(deviceDeptService.selectDeviceDeptTreeList(dept));
        Long currentDeptId = SecurityUtils.getDeptId();
        SysDept currentDept = currentDeptId == null ? null : sysDeptService.selectDeptById(currentDeptId);
        result.put("currentDeptId", currentDeptId);
        result.put("currentDeptSource", currentDept == null ? null : currentDept.getDeptSource());
        return result;
    }
}
