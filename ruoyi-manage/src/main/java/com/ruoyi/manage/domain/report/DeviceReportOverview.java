package com.ruoyi.manage.domain.report;

import java.math.BigDecimal;

/**
 * 设备报告总览统计。
 */
public class DeviceReportOverview
{
    private Long firePointCount = 0L;
    private Long gatewayCount = 0L;
    private Long sensorCount = 0L;
    private Long extinguisherCount = 0L;
    private Long riskDeviceCount = 0L;
    private Long sensorNormalCount = 0L;
    private Long sensorAbnormalCount = 0L;
    private Long sensorOfflineCount = 0L;
    private Long sensorLowBatteryCount = 0L;
    private Long sensorLowPressureCount = 0L;
    private Long sensorHighPressureCount = 0L;
    private Long sensorInvalidPressureCount = 0L;
    private Long historySampleCount = 0L;
    private Long historyFutureCount = 0L;
    private Long historyInvalidPressureCount = 0L;
    private BigDecimal avgPressure;
    private BigDecimal minPressure;
    private BigDecimal maxPressure;
    private BigDecimal avgTemperature;
    private BigDecimal avgBatteryLevel;
    private Long gatewayNormalCount = 0L;
    private Long gatewayAbnormalCount = 0L;
    private Long gatewayOfflineCount = 0L;
    private Long gatewayUnboundFirePointCount = 0L;
    private Long gatewayMissingDeptCount = 0L;
    private Long extinguisherNormalCount = 0L;
    private Long extinguisherExpiringSoonCount = 0L;
    private Long extinguisherExpiredCount = 0L;
    private Long extinguisherUnboundSensorCount = 0L;
    private Long extinguisherMissingDeptCount = 0L;
    private Long firePointExtinguisherShortageCount = 0L;

    public Long getFirePointCount() { return firePointCount; }
    public void setFirePointCount(Long firePointCount) { this.firePointCount = firePointCount; }
    public Long getGatewayCount() { return gatewayCount; }
    public void setGatewayCount(Long gatewayCount) { this.gatewayCount = gatewayCount; }
    public Long getSensorCount() { return sensorCount; }
    public void setSensorCount(Long sensorCount) { this.sensorCount = sensorCount; }
    public Long getExtinguisherCount() { return extinguisherCount; }
    public void setExtinguisherCount(Long extinguisherCount) { this.extinguisherCount = extinguisherCount; }
    public Long getRiskDeviceCount() { return riskDeviceCount; }
    public void setRiskDeviceCount(Long riskDeviceCount) { this.riskDeviceCount = riskDeviceCount; }
    public Long getSensorNormalCount() { return sensorNormalCount; }
    public void setSensorNormalCount(Long sensorNormalCount) { this.sensorNormalCount = sensorNormalCount; }
    public Long getSensorAbnormalCount() { return sensorAbnormalCount; }
    public void setSensorAbnormalCount(Long sensorAbnormalCount) { this.sensorAbnormalCount = sensorAbnormalCount; }
    public Long getSensorOfflineCount() { return sensorOfflineCount; }
    public void setSensorOfflineCount(Long sensorOfflineCount) { this.sensorOfflineCount = sensorOfflineCount; }
    public Long getSensorLowBatteryCount() { return sensorLowBatteryCount; }
    public void setSensorLowBatteryCount(Long sensorLowBatteryCount) { this.sensorLowBatteryCount = sensorLowBatteryCount; }
    public Long getSensorLowPressureCount() { return sensorLowPressureCount; }
    public void setSensorLowPressureCount(Long sensorLowPressureCount) { this.sensorLowPressureCount = sensorLowPressureCount; }
    public Long getSensorHighPressureCount() { return sensorHighPressureCount; }
    public void setSensorHighPressureCount(Long sensorHighPressureCount) { this.sensorHighPressureCount = sensorHighPressureCount; }
    public Long getSensorInvalidPressureCount() { return sensorInvalidPressureCount; }
    public void setSensorInvalidPressureCount(Long sensorInvalidPressureCount) { this.sensorInvalidPressureCount = sensorInvalidPressureCount; }
    public Long getHistorySampleCount() { return historySampleCount; }
    public void setHistorySampleCount(Long historySampleCount) { this.historySampleCount = historySampleCount; }
    public Long getHistoryFutureCount() { return historyFutureCount; }
    public void setHistoryFutureCount(Long historyFutureCount) { this.historyFutureCount = historyFutureCount; }
    public Long getHistoryInvalidPressureCount() { return historyInvalidPressureCount; }
    public void setHistoryInvalidPressureCount(Long historyInvalidPressureCount) { this.historyInvalidPressureCount = historyInvalidPressureCount; }
    public BigDecimal getAvgPressure() { return avgPressure; }
    public void setAvgPressure(BigDecimal avgPressure) { this.avgPressure = avgPressure; }
    public BigDecimal getMinPressure() { return minPressure; }
    public void setMinPressure(BigDecimal minPressure) { this.minPressure = minPressure; }
    public BigDecimal getMaxPressure() { return maxPressure; }
    public void setMaxPressure(BigDecimal maxPressure) { this.maxPressure = maxPressure; }
    public BigDecimal getAvgTemperature() { return avgTemperature; }
    public void setAvgTemperature(BigDecimal avgTemperature) { this.avgTemperature = avgTemperature; }
    public BigDecimal getAvgBatteryLevel() { return avgBatteryLevel; }
    public void setAvgBatteryLevel(BigDecimal avgBatteryLevel) { this.avgBatteryLevel = avgBatteryLevel; }
    public Long getGatewayNormalCount() { return gatewayNormalCount; }
    public void setGatewayNormalCount(Long gatewayNormalCount) { this.gatewayNormalCount = gatewayNormalCount; }
    public Long getGatewayAbnormalCount() { return gatewayAbnormalCount; }
    public void setGatewayAbnormalCount(Long gatewayAbnormalCount) { this.gatewayAbnormalCount = gatewayAbnormalCount; }
    public Long getGatewayOfflineCount() { return gatewayOfflineCount; }
    public void setGatewayOfflineCount(Long gatewayOfflineCount) { this.gatewayOfflineCount = gatewayOfflineCount; }
    public Long getGatewayUnboundFirePointCount() { return gatewayUnboundFirePointCount; }
    public void setGatewayUnboundFirePointCount(Long gatewayUnboundFirePointCount) { this.gatewayUnboundFirePointCount = gatewayUnboundFirePointCount; }
    public Long getGatewayMissingDeptCount() { return gatewayMissingDeptCount; }
    public void setGatewayMissingDeptCount(Long gatewayMissingDeptCount) { this.gatewayMissingDeptCount = gatewayMissingDeptCount; }
    public Long getExtinguisherNormalCount() { return extinguisherNormalCount; }
    public void setExtinguisherNormalCount(Long extinguisherNormalCount) { this.extinguisherNormalCount = extinguisherNormalCount; }
    public Long getExtinguisherExpiringSoonCount() { return extinguisherExpiringSoonCount; }
    public void setExtinguisherExpiringSoonCount(Long extinguisherExpiringSoonCount) { this.extinguisherExpiringSoonCount = extinguisherExpiringSoonCount; }
    public Long getExtinguisherExpiredCount() { return extinguisherExpiredCount; }
    public void setExtinguisherExpiredCount(Long extinguisherExpiredCount) { this.extinguisherExpiredCount = extinguisherExpiredCount; }
    public Long getExtinguisherUnboundSensorCount() { return extinguisherUnboundSensorCount; }
    public void setExtinguisherUnboundSensorCount(Long extinguisherUnboundSensorCount) { this.extinguisherUnboundSensorCount = extinguisherUnboundSensorCount; }
    public Long getExtinguisherMissingDeptCount() { return extinguisherMissingDeptCount; }
    public void setExtinguisherMissingDeptCount(Long extinguisherMissingDeptCount) { this.extinguisherMissingDeptCount = extinguisherMissingDeptCount; }
    public Long getFirePointExtinguisherShortageCount() { return firePointExtinguisherShortageCount; }
    public void setFirePointExtinguisherShortageCount(Long firePointExtinguisherShortageCount) { this.firePointExtinguisherShortageCount = firePointExtinguisherShortageCount; }
}
