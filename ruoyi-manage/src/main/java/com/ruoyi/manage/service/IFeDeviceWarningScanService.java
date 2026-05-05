package com.ruoyi.manage.service;

import java.util.Map;

public interface IFeDeviceWarningScanService
{
    Map<String, Object> scanAfterSdkSync(Long sourceDeptId, String operator);
}
