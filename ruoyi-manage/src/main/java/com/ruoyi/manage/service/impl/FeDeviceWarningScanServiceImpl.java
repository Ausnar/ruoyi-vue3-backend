package com.ruoyi.manage.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.manage.domain.FeDeviceWarning;
import com.ruoyi.manage.mapper.FeDeviceWarningMapper;
import com.ruoyi.manage.service.IFeDeviceWarningService;
import com.ruoyi.manage.service.IFeDeviceWarningScanService;

@Service
public class FeDeviceWarningScanServiceImpl implements IFeDeviceWarningScanService
{
    @Autowired
    private FeDeviceWarningMapper feDeviceWarningMapper;

    @Autowired
    private IFeDeviceWarningService feDeviceWarningService;

    @Override
    public Map<String, Object> scanAfterSdkSync(Long sourceDeptId, String operator)
    {
        int suspectedFireCount = saveWarnings(feDeviceWarningMapper.selectSuspectedFireCandidates(sourceDeptId), operator);
        int lowBatteryCount = saveWarnings(feDeviceWarningMapper.selectLowBatteryCandidates(sourceDeptId), operator);
        int lowPressureCount = saveWarnings(feDeviceWarningMapper.selectLowPressureCandidates(sourceDeptId), operator);
        int highPressureCount = saveWarnings(feDeviceWarningMapper.selectHighPressureCandidates(sourceDeptId), operator);
        int insufficientExtinguisherCount = saveWarnings(feDeviceWarningMapper.selectInsufficientExtinguisherCandidates(sourceDeptId), operator);
        int extinguisherExpiredCount = saveWarnings(feDeviceWarningMapper.selectExpiredExtinguisherCandidates(sourceDeptId), operator);
        int abnormalTemperatureCount = saveWarnings(feDeviceWarningMapper.selectAbnormalTemperatureCandidates(sourceDeptId), operator);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sourceDeptId", sourceDeptId);
        result.put("success", true);
        result.put("message", "Device warning scan completed");
        result.put("suspectedFireCount", suspectedFireCount);
        result.put("lowBatteryCount", lowBatteryCount);
        result.put("lowPressureCount", lowPressureCount);
        result.put("highPressureCount", highPressureCount);
        result.put("insufficientExtinguisherCount", insufficientExtinguisherCount);
        result.put("extinguisherExpiredCount", extinguisherExpiredCount);
        result.put("abnormalTemperatureCount", abnormalTemperatureCount);
        result.put("gatewayOfflineEnabled", false);
        return result;
    }

    private int saveWarnings(List<FeDeviceWarning> warnings, String operator)
    {
        int count = 0;
        for (FeDeviceWarning warning : warnings)
        {
            feDeviceWarningService.saveOrRefreshOpenWarning(warning, operator);
            count++;
        }
        return count;
    }
}
