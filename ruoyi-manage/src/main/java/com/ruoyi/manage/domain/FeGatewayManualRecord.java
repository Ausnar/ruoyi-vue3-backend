package com.ruoyi.manage.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeGatewayManualRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "记录ID")
    private Long recordId;
    private Long externalCompanyId;
    @Excel(name = "外部单位")
    private String externalCompanyNameSnapshot;
    private Long deptId;
    @Excel(name = "录入归口单位")
    private String deptName;
    private Long sourceDeptId;
    private String sourceDeptName;
    private Long gatewayId;
    @Excel(name = "已匹配网关IMEI")
    private String matchedGatewayImei;
    @Excel(name = "已匹配消防点")
    private String matchedFirePointName;
    private Long mappedDeptId;
    @Excel(name = "当前映射合同单位")
    private String mappedDeptName;
    @Excel(name = "TBOX配置号")
    private String gatewayConfigCode;
    @Excel(name = "二维码号")
    private String gatewayQrCode;
    @Excel(name = "4G号")
    private String gatewayImei;
    @Excel(name = "SIM卡号")
    private String simNo;
    @Excel(name = "SIM开卡月")
    private String simOpenMonth;
    @Excel(name = "放置地点")
    private String placementLocation;
    @Excel(name = "状态")
    private String status;
    @Excel(name = "匹配状态")
    private String matchStatus;
    @Excel(name = "施工人员")
    private String installerName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "施工时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date installTime;
    private Integer sensorCount;
    private String sensorCodeSummary;
    private List<FeSensorManualRecord> sensorRecords;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public String getExternalCompanyNameSnapshot() { return externalCompanyNameSnapshot; }
    public void setExternalCompanyNameSnapshot(String externalCompanyNameSnapshot) { this.externalCompanyNameSnapshot = externalCompanyNameSnapshot; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getSourceDeptId() { return sourceDeptId; }
    public void setSourceDeptId(Long sourceDeptId) { this.sourceDeptId = sourceDeptId; }
    public String getSourceDeptName() { return sourceDeptName; }
    public void setSourceDeptName(String sourceDeptName) { this.sourceDeptName = sourceDeptName; }
    public Long getGatewayId() { return gatewayId; }
    public void setGatewayId(Long gatewayId) { this.gatewayId = gatewayId; }
    public String getMatchedGatewayImei() { return matchedGatewayImei; }
    public void setMatchedGatewayImei(String matchedGatewayImei) { this.matchedGatewayImei = matchedGatewayImei; }
    public String getMatchedFirePointName() { return matchedFirePointName; }
    public void setMatchedFirePointName(String matchedFirePointName) { this.matchedFirePointName = matchedFirePointName; }
    public Long getMappedDeptId() { return mappedDeptId; }
    public void setMappedDeptId(Long mappedDeptId) { this.mappedDeptId = mappedDeptId; }
    public String getMappedDeptName() { return mappedDeptName; }
    public void setMappedDeptName(String mappedDeptName) { this.mappedDeptName = mappedDeptName; }
    public String getGatewayConfigCode() { return gatewayConfigCode; }
    public void setGatewayConfigCode(String gatewayConfigCode) { this.gatewayConfigCode = gatewayConfigCode; }
    public String getGatewayQrCode() { return gatewayQrCode; }
    public void setGatewayQrCode(String gatewayQrCode) { this.gatewayQrCode = gatewayQrCode; }
    public String getGatewayImei() { return gatewayImei; }
    public void setGatewayImei(String gatewayImei) { this.gatewayImei = gatewayImei; }
    public String getSimNo() { return simNo; }
    public void setSimNo(String simNo) { this.simNo = simNo; }
    public String getSimOpenMonth() { return simOpenMonth; }
    public void setSimOpenMonth(String simOpenMonth) { this.simOpenMonth = simOpenMonth; }
    public String getPlacementLocation() { return placementLocation; }
    public void setPlacementLocation(String placementLocation) { this.placementLocation = placementLocation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMatchStatus() { return matchStatus; }
    public void setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }
    public String getInstallerName() { return installerName; }
    public void setInstallerName(String installerName) { this.installerName = installerName; }
    public Date getInstallTime() { return installTime; }
    public void setInstallTime(Date installTime) { this.installTime = installTime; }
    public Integer getSensorCount() { return sensorCount; }
    public void setSensorCount(Integer sensorCount) { this.sensorCount = sensorCount; }
    public String getSensorCodeSummary() { return sensorCodeSummary; }
    public void setSensorCodeSummary(String sensorCodeSummary) { this.sensorCodeSummary = sensorCodeSummary; }
    public List<FeSensorManualRecord> getSensorRecords() { return sensorRecords; }
    public void setSensorRecords(List<FeSensorManualRecord> sensorRecords) { this.sensorRecords = sensorRecords; }
}
