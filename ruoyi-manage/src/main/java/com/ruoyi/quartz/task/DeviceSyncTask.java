package com.ruoyi.quartz.task;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.manage.service.IFeDeviceSdkSyncService;

@Component("deviceSyncTask")
public class DeviceSyncTask
{
    private static final Logger log = LoggerFactory.getLogger(DeviceSyncTask.class);
    private static final String OPERATOR = "quartz:deviceSync";

    @Autowired
    private IFeDeviceSdkSyncService feDeviceSdkSyncService;

    public void syncAllActiveConfigs()
    {
        Map<String, Object> result = feDeviceSdkSyncService.syncAllActiveConfigs(OPERATOR);
        log.info("[DeviceSyncTask] syncAllActiveConfigs result={}", result);
    }

    public void syncByConfigId(Long configId)
    {
        Map<String, Object> result = feDeviceSdkSyncService.syncByConfigId(configId, OPERATOR);
        log.info("[DeviceSyncTask] syncByConfigId configId={}, result={}", configId, result);
    }

    public void syncByConfigId(Integer configId)
    {
        syncByConfigId(configId == null ? null : Long.valueOf(configId));
    }
}
