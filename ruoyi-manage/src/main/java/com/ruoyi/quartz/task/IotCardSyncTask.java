package com.ruoyi.quartz.task;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.manage.service.IFeIotCardService;

@Component("iotCardSyncTask")
public class IotCardSyncTask
{
    private static final Logger log = LoggerFactory.getLogger(IotCardSyncTask.class);
    private static final String OPERATOR = "quartz:iotCardSync";

    @Autowired
    private IFeIotCardService feIotCardService;

    public void syncAll()
    {
        Map<String, Object> result = feIotCardService.syncAll(OPERATOR);
        log.info("[IotCardSyncTask] syncAll result={}", result);
    }
}
