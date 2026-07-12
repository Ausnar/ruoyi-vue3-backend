package com.ruoyi.manage.service;

import java.util.Map;

import com.ruoyi.system.domain.SysDeptApiConfig;

public interface IFeDeviceSdkSyncService
{
    Map<String, Object> syncAllActiveConfigs(String operator);

    Map<String, Object> syncByConfigId(Long configId, String operator);

    SysDeptApiConfig createContractWithSdkMirror(SysDeptApiConfig contract, String operator);

    SysDeptApiConfig updateContractWithSdkMirror(SysDeptApiConfig contract, String operator);

    Map<String, Object> refreshExtinguisherProfile(Long extinguisherId, String operator);
}
