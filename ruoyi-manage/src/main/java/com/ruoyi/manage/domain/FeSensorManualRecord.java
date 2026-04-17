package com.ruoyi.manage.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeSensorManualRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "传感器记录ID")
    private Long sensorRecordId;
    private Long gatewayRecordId;
    @Excel(name = "所属网关记录ID")
    private Long parentGatewayRecordId;
    @Excel(name = "外部单位")
    private String externalCompanyNameSnapshot;
    private Long externalCompanyId;
    private Long deptId;
    @Excel(name = "录入归口单位")
    private String deptName;
    private Long mappedDeptId;
    @Excel(name = "当前映射合同单位")
    private String mappedDeptName;
    private Long sensorId;
    private Long extinguisherId;
    @Excel(name = "传感器号")
    private String sensorCode;
    @Excel(name = "MAC地址")
    private String macAddress;
    @Excel(name = "装配地址")
    private String assemblyAddress;
    @Excel(name = "灭火器瓶体序列号")
    private String extinguisherBodySerialNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "灭火器出产日期", width = 16, dateFormat = "yyyy-MM-dd")
    private Date extinguisherProductionDate;
    @Excel(name = "传感器类型")
    private String sensorVendorType;
    @Excel(name = "状态")
    private String status;
    @Excel(name = "匹配状态")
    private String matchStatus;
    @Excel(name = "已匹配传感器")
    private String matchedSensorCode;
    @Excel(name = "已匹配灭火器")
    private String matchedExtinguisherLabelCode;
    @Excel(name = "所属网关IMEI")
    private String gatewayImei;
    @Excel(name = "所属TBOX配置号")
    private String gatewayConfigCode;

    public Long getSensorRecordId() { return sensorRecordId; }
    public void setSensorRecordId(Long sensorRecordId) { this.sensorRecordId = sensorRecordId; }
    public Long getGatewayRecordId() { return gatewayRecordId; }
    public void setGatewayRecordId(Long gatewayRecordId) { this.gatewayRecordId = gatewayRecordId; }
    public Long getParentGatewayRecordId() { return parentGatewayRecordId; }
    public void setParentGatewayRecordId(Long parentGatewayRecordId) { this.parentGatewayRecordId = parentGatewayRecordId; }
    public String getExternalCompanyNameSnapshot() { return externalCompanyNameSnapshot; }
    public void setExternalCompanyNameSnapshot(String externalCompanyNameSnapshot) { this.externalCompanyNameSnapshot = externalCompanyNameSnapshot; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getMappedDeptId() { return mappedDeptId; }
    public void setMappedDeptId(Long mappedDeptId) { this.mappedDeptId = mappedDeptId; }
    public String getMappedDeptName() { return mappedDeptName; }
    public void setMappedDeptName(String mappedDeptName) { this.mappedDeptName = mappedDeptName; }
    public Long getSensorId() { return sensorId; }
    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
    public Long getExtinguisherId() { return extinguisherId; }
    public void setExtinguisherId(Long extinguisherId) { this.extinguisherId = extinguisherId; }
    public String getSensorCode() { return sensorCode; }
    public void setSensorCode(String sensorCode) { this.sensorCode = sensorCode; }
    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
    public String getAssemblyAddress() { return assemblyAddress; }
    public void setAssemblyAddress(String assemblyAddress) { this.assemblyAddress = assemblyAddress; }
    public String getExtinguisherBodySerialNo() { return extinguisherBodySerialNo; }
    public void setExtinguisherBodySerialNo(String extinguisherBodySerialNo) { this.extinguisherBodySerialNo = extinguisherBodySerialNo; }
    public Date getExtinguisherProductionDate() { return extinguisherProductionDate; }
    public void setExtinguisherProductionDate(Date extinguisherProductionDate) { this.extinguisherProductionDate = extinguisherProductionDate; }
    public String getSensorVendorType() { return sensorVendorType; }
    public void setSensorVendorType(String sensorVendorType) { this.sensorVendorType = sensorVendorType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMatchStatus() { return matchStatus; }
    public void setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }
    public String getMatchedSensorCode() { return matchedSensorCode; }
    public void setMatchedSensorCode(String matchedSensorCode) { this.matchedSensorCode = matchedSensorCode; }
    public String getMatchedExtinguisherLabelCode() { return matchedExtinguisherLabelCode; }
    public void setMatchedExtinguisherLabelCode(String matchedExtinguisherLabelCode) { this.matchedExtinguisherLabelCode = matchedExtinguisherLabelCode; }
    public String getGatewayImei() { return gatewayImei; }
    public void setGatewayImei(String gatewayImei) { this.gatewayImei = gatewayImei; }
    public String getGatewayConfigCode() { return gatewayConfigCode; }
    public void setGatewayConfigCode(String gatewayConfigCode) { this.gatewayConfigCode = gatewayConfigCode; }
}
