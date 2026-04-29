package com.ruoyi.manage.domain.report;

/**
 * 单位维度设备与风险统计。
 */
public class DeviceReportUnitStat
{
    private Long deptId;
    private String deptName;
    private Long firePointCount = 0L;
    private Long gatewayCount = 0L;
    private Long sensorCount = 0L;
    private Long extinguisherCount = 0L;
    private Long totalCount = 0L;
    private Long riskCount = 0L;

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getFirePointCount() { return firePointCount; }
    public void setFirePointCount(Long firePointCount) { this.firePointCount = firePointCount; }
    public Long getGatewayCount() { return gatewayCount; }
    public void setGatewayCount(Long gatewayCount) { this.gatewayCount = gatewayCount; }
    public Long getSensorCount() { return sensorCount; }
    public void setSensorCount(Long sensorCount) { this.sensorCount = sensorCount; }
    public Long getExtinguisherCount() { return extinguisherCount; }
    public void setExtinguisherCount(Long extinguisherCount) { this.extinguisherCount = extinguisherCount; }
    public Long getTotalCount() { return totalCount; }
    public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
    public Long getRiskCount() { return riskCount; }
    public void setRiskCount(Long riskCount) { this.riskCount = riskCount; }
}
