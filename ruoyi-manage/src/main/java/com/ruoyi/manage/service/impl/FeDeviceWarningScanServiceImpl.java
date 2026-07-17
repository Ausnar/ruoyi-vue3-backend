package com.ruoyi.manage.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruoyi.manage.domain.FeDeviceWarning;
import com.ruoyi.manage.mapper.FeDeviceWarningMapper;
import com.ruoyi.manage.service.IFeDeviceWarningService;
import com.ruoyi.manage.service.IFeDeviceWarningScanService;
import com.ruoyi.manage.service.IFeDeviceWarningTaskService;

@Service
public class FeDeviceWarningScanServiceImpl implements IFeDeviceWarningScanService
{
    private static final Logger log = LoggerFactory.getLogger(FeDeviceWarningScanServiceImpl.class);

    @Autowired
    private FeDeviceWarningMapper feDeviceWarningMapper;

    @Autowired
    private IFeDeviceWarningService feDeviceWarningService;

    @Autowired
    private IFeDeviceWarningTaskService feDeviceWarningTaskService;

    @Override
    public Map<String, Object> scanAfterSdkSync(Long sourceDeptId, String operator)
    {
        List<String> failedStages = new ArrayList<>();
        int suspectedFireCount = runStage("suspectedFire", sourceDeptId, failedStages,
            () -> saveWarnings(feDeviceWarningMapper.selectSuspectedFireCandidates(sourceDeptId), operator));
        int lowBatteryCount = runStage("lowBattery", sourceDeptId, failedStages,
            () -> saveWarnings(feDeviceWarningMapper.selectLowBatteryCandidates(sourceDeptId), operator));
        int lowPressureCount = runStage("lowPressure", sourceDeptId, failedStages,
            () -> saveWarnings(feDeviceWarningMapper.selectLowPressureCandidates(sourceDeptId), operator));
        int highPressureCount = runStage("highPressure", sourceDeptId, failedStages,
            () -> saveWarnings(feDeviceWarningMapper.selectHighPressureCandidates(sourceDeptId), operator));
        int insufficientExtinguisherCount = runStage("insufficientExtinguisher", sourceDeptId, failedStages,
            () -> saveWarnings(feDeviceWarningMapper.selectInsufficientExtinguisherCandidates(sourceDeptId), operator));
        int extinguisherScrapDueCount = runStage("extinguisherScrapDue", sourceDeptId, failedStages,
            () -> saveWarnings(feDeviceWarningMapper.selectExtinguisherScrapDueCandidates(sourceDeptId), operator));
        int abnormalTemperatureCount = runStage("abnormalTemperature", sourceDeptId, failedStages,
            () -> saveWarnings(feDeviceWarningMapper.selectAbnormalTemperatureCandidates(sourceDeptId), operator));
        int recoveredCount = runStage("warningRecovery", sourceDeptId, failedStages,
            () -> feDeviceWarningService.recoverWarnings(
                feDeviceWarningMapper.selectRecoverableWarnings(sourceDeptId), operator));
        int recoveredTaskCount = runStage("taskRecovery", sourceDeptId, failedStages,
            () -> feDeviceWarningTaskService.closeRecoveredTasks(sourceDeptId, operator));
        int dispatchedTaskCount = runStage("taskDispatch", sourceDeptId, failedStages,
            () -> feDeviceWarningTaskService.dispatchActiveWarnings(sourceDeptId, operator));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sourceDeptId", sourceDeptId);
        result.put("success", failedStages.isEmpty());
        result.put("message", failedStages.isEmpty()
            ? "Device warning scan completed" : "Device warning scan completed with failed stages");
        result.put("failedStages", String.join(",", failedStages));
        result.put("suspectedFireCount", suspectedFireCount);
        result.put("lowBatteryCount", lowBatteryCount);
        result.put("lowPressureCount", lowPressureCount);
        result.put("highPressureCount", highPressureCount);
        result.put("insufficientExtinguisherCount", insufficientExtinguisherCount);
        result.put("extinguisherScrapDueCount", extinguisherScrapDueCount);
        result.put("abnormalTemperatureCount", abnormalTemperatureCount);
        result.put("recoveredCount", recoveredCount);
        result.put("recoveredTaskCount", recoveredTaskCount);
        result.put("dispatchedTaskCount", dispatchedTaskCount);
        result.put("gatewayOfflineEnabled", false);
        return result;
    }

    private int runStage(String stageName, Long sourceDeptId, List<String> failedStages, ScanStage stage)
    {
        try
        {
            return stage.execute();
        }
        catch (Exception e)
        {
            failedStages.add(stageName);
            log.warn("Device warning scan stage failed, sourceDeptId={}, stage={}", sourceDeptId, stageName, e);
            return 0;
        }
    }

    @FunctionalInterface
    private interface ScanStage
    {
        int execute();
    }

    private int saveWarnings(List<FeDeviceWarning> warnings, String operator)
    {
        int count = 0;
        for (FeDeviceWarning warning : warnings)
        {
            feDeviceWarningService.saveOrRefreshActiveWarning(warning, operator);
            count++;
        }
        return count;
    }
}
