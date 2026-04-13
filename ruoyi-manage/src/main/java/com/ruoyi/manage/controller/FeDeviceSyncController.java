package com.ruoyi.manage.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.service.IFeDeviceSdkSyncService;

@RestController
@RequestMapping("/manage/device-sync")
public class FeDeviceSyncController extends BaseController
{
    @Autowired
    private IFeDeviceSdkSyncService feDeviceSdkSyncService;

    @PreAuthorize("@ss.hasPermi('manage:deviceSync:sync')")
    @PostMapping("/full")
    public AjaxResult syncAll()
    {
        String operator = getUsername();
        Map<String, Object> result = feDeviceSdkSyncService.syncAllActiveConfigs(operator);
        return AjaxResult.success("设备全量同步完成", result);
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceSync:sync')")
    @PostMapping("/config/{configId}")
    public AjaxResult syncByConfig(@PathVariable Long configId)
    {
        String operator = getUsername();
        Map<String, Object> result = feDeviceSdkSyncService.syncByConfigId(configId, operator);
        return AjaxResult.success("设备同步完成", result);
    }
}
