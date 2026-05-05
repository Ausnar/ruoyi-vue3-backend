package com.ruoyi.manage.service;

import java.util.Map;

public interface IFeDeviceSdkSyncService
{
    Map<String, Object> syncAllActiveConfigs(String operator);

    Map<String, Object> syncByConfigId(Long configId, String operator);

    Map<String, Object> refreshExtinguisherProfile(Long extinguisherId, String operator);
}
