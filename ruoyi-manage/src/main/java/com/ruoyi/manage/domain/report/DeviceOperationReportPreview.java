package com.ruoyi.manage.domain.report;

import java.math.BigDecimal;

public class DeviceOperationReportPreview
{
    private String reportTitle;
    private String periodTypeName;
    private String periodLabel;
    private String scopeName;
    private String generatedTime;
    private UnitInfo unitInfo = new UnitInfo();
    private AssetOverview assetOverview = new AssetOverview();
    private SensorSummary sensorSummary = new SensorSummary();
    private GatewaySummary gatewaySummary = new GatewaySummary();
    private ExtinguisherSummary extinguisherSummary = new ExtinguisherSummary();
    private FirePointSummary firePointSummary = new FirePointSummary();

    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public String getPeriodTypeName() { return periodTypeName; }
    public void setPeriodTypeName(String periodTypeName) { this.periodTypeName = periodTypeName; }
    public String getPeriodLabel() { return periodLabel; }
    public void setPeriodLabel(String periodLabel) { this.periodLabel = periodLabel; }
    public String getScopeName() { return scopeName; }
    public void setScopeName(String scopeName) { this.scopeName = scopeName; }
    public String getGeneratedTime() { return generatedTime; }
    public void setGeneratedTime(String generatedTime) { this.generatedTime = generatedTime; }
    public UnitInfo getUnitInfo() { return unitInfo; }
    public void setUnitInfo(UnitInfo unitInfo) { this.unitInfo = unitInfo; }
    public AssetOverview getAssetOverview() { return assetOverview; }
    public void setAssetOverview(AssetOverview assetOverview) { this.assetOverview = assetOverview; }
    public SensorSummary getSensorSummary() { return sensorSummary; }
    public void setSensorSummary(SensorSummary sensorSummary) { this.sensorSummary = sensorSummary; }
    public GatewaySummary getGatewaySummary() { return gatewaySummary; }
    public void setGatewaySummary(GatewaySummary gatewaySummary) { this.gatewaySummary = gatewaySummary; }
    public ExtinguisherSummary getExtinguisherSummary() { return extinguisherSummary; }
    public void setExtinguisherSummary(ExtinguisherSummary extinguisherSummary) { this.extinguisherSummary = extinguisherSummary; }
    public FirePointSummary getFirePointSummary() { return firePointSummary; }
    public void setFirePointSummary(FirePointSummary firePointSummary) { this.firePointSummary = firePointSummary; }

    public static class UnitInfo
    {
        private Long deptId;
        private String deptName;
        private String parentDeptName;
        private String province;
        private String city;
        private String area;

        public Long getDeptId() { return deptId; }
        public void setDeptId(Long deptId) { this.deptId = deptId; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }
        public String getParentDeptName() { return parentDeptName; }
        public void setParentDeptName(String parentDeptName) { this.parentDeptName = parentDeptName; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }
    }

    public static class AssetOverview
    {
        private Long firePointCount = 0L;
        private Long gatewayCount = 0L;
        private Long sensorCount = 0L;
        private Long extinguisherCount = 0L;

        public Long getFirePointCount() { return firePointCount; }
        public void setFirePointCount(Long value) { this.firePointCount = value; }
        public Long getGatewayCount() { return gatewayCount; }
        public void setGatewayCount(Long value) { this.gatewayCount = value; }
        public Long getSensorCount() { return sensorCount; }
        public void setSensorCount(Long value) { this.sensorCount = value; }
        public Long getExtinguisherCount() { return extinguisherCount; }
        public void setExtinguisherCount(Long value) { this.extinguisherCount = value; }
    }

    public static class SensorSummary
    {
        private Long sensorCount = 0L;
        private Long reportingSensorCount = 0L;
        private Long noSampleSensorCount = 0L;
        private Long sampleCount = 0L;
        private Long validPressureCount = 0L;
        private Long invalidPressureCount = 0L;
        private Long missingPressureCount = 0L;
        private BigDecimal avgPressure;
        private BigDecimal avgTemperature;
        private BigDecimal avgBatteryLevel;

        public Long getSensorCount() { return sensorCount; }
        public void setSensorCount(Long value) { this.sensorCount = value; }
        public Long getReportingSensorCount() { return reportingSensorCount; }
        public void setReportingSensorCount(Long value) { this.reportingSensorCount = value; }
        public Long getNoSampleSensorCount() { return noSampleSensorCount; }
        public void setNoSampleSensorCount(Long value) { this.noSampleSensorCount = value; }
        public Long getSampleCount() { return sampleCount; }
        public void setSampleCount(Long value) { this.sampleCount = value; }
        public Long getValidPressureCount() { return validPressureCount; }
        public void setValidPressureCount(Long value) { this.validPressureCount = value; }
        public Long getInvalidPressureCount() { return invalidPressureCount; }
        public void setInvalidPressureCount(Long value) { this.invalidPressureCount = value; }
        public Long getMissingPressureCount() { return missingPressureCount; }
        public void setMissingPressureCount(Long value) { this.missingPressureCount = value; }
        public BigDecimal getAvgPressure() { return avgPressure; }
        public void setAvgPressure(BigDecimal value) { this.avgPressure = value; }
        public BigDecimal getAvgTemperature() { return avgTemperature; }
        public void setAvgTemperature(BigDecimal value) { this.avgTemperature = value; }
        public BigDecimal getAvgBatteryLevel() { return avgBatteryLevel; }
        public void setAvgBatteryLevel(BigDecimal value) { this.avgBatteryLevel = value; }
    }

    public static class GatewaySummary
    {
        private Long gatewayCount = 0L;
        private Long reportingGatewayCount = 0L;
        private Long noGpsGatewayCount = 0L;
        private Long gpsRecordCount = 0L;

        public Long getGatewayCount() { return gatewayCount; }
        public void setGatewayCount(Long value) { this.gatewayCount = value; }
        public Long getReportingGatewayCount() { return reportingGatewayCount; }
        public void setReportingGatewayCount(Long value) { this.reportingGatewayCount = value; }
        public Long getNoGpsGatewayCount() { return noGpsGatewayCount; }
        public void setNoGpsGatewayCount(Long value) { this.noGpsGatewayCount = value; }
        public Long getGpsRecordCount() { return gpsRecordCount; }
        public void setGpsRecordCount(Long value) { this.gpsRecordCount = value; }
    }

    public static class ExtinguisherSummary
    {
        private Long extinguisherCount = 0L;
        private Long syncedCount = 0L;
        private Long profileCompleteCount = 0L;
        private Long profileIncompleteCount = 0L;
        private Long boundSensorCount = 0L;
        private Long unboundSensorCount = 0L;

        public Long getExtinguisherCount() { return extinguisherCount; }
        public void setExtinguisherCount(Long value) { this.extinguisherCount = value; }
        public Long getSyncedCount() { return syncedCount; }
        public void setSyncedCount(Long value) { this.syncedCount = value; }
        public Long getProfileCompleteCount() { return profileCompleteCount; }
        public void setProfileCompleteCount(Long value) { this.profileCompleteCount = value; }
        public Long getProfileIncompleteCount() { return profileIncompleteCount; }
        public void setProfileIncompleteCount(Long value) { this.profileIncompleteCount = value; }
        public Long getBoundSensorCount() { return boundSensorCount; }
        public void setBoundSensorCount(Long value) { this.boundSensorCount = value; }
        public Long getUnboundSensorCount() { return unboundSensorCount; }
        public void setUnboundSensorCount(Long value) { this.unboundSensorCount = value; }
    }

    public static class FirePointSummary
    {
        private Long firePointCount = 0L;
        private Long snapshotCoveredCount = 0L;
        private Long noSnapshotCount = 0L;
        private Long snapshotCount = 0L;
        private Long expectedConfiguredCount = 0L;

        public Long getFirePointCount() { return firePointCount; }
        public void setFirePointCount(Long value) { this.firePointCount = value; }
        public Long getSnapshotCoveredCount() { return snapshotCoveredCount; }
        public void setSnapshotCoveredCount(Long value) { this.snapshotCoveredCount = value; }
        public Long getNoSnapshotCount() { return noSnapshotCount; }
        public void setNoSnapshotCount(Long value) { this.noSnapshotCount = value; }
        public Long getSnapshotCount() { return snapshotCount; }
        public void setSnapshotCount(Long value) { this.snapshotCount = value; }
        public Long getExpectedConfiguredCount() { return expectedConfiguredCount; }
        public void setExpectedConfiguredCount(Long value) { this.expectedConfiguredCount = value; }
    }
}
