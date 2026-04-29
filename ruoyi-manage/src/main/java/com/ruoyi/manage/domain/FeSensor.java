package com.ruoyi.manage.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeSensor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long sensorId;
    private Long externalSensorId;
    @Excel(name = "传感器编号")
    private String sensorCode;
    @Excel(name = "所属部门ID")
    private Long deptId;
    private String deptName;
    @Excel(name = "归属省")
    private String deptProvince;
    @Excel(name = "归属市")
    private String deptCity;
    @Excel(name = "归属区县")
    private String deptArea;
    private Long sourceDeptId;
    private String sourceDeptName;
    private String externalCompanyName;
    private Long gatewayId;
    @Excel(name = "网关编号")
    private String gatewayCode;
    private String mac;
    @Excel(name = "压力")
    private BigDecimal pressure;
    @Excel(name = "温度")
    private BigDecimal temperature;
    @Excel(name = "电量")
    private Integer batteryLevel;
    private Integer signalStrength;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastOnlineTime;
    @Excel(name = "状态")
    private String status;
    private String syncStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSyncTime;
    private String delFlag;

    public Long getSensorId() { return sensorId; }
    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
    public Long getExternalSensorId() { return externalSensorId; }
    public void setExternalSensorId(Long externalSensorId) { this.externalSensorId = externalSensorId; }
    public String getSensorCode() { return sensorCode; }
    public void setSensorCode(String sensorCode) { this.sensorCode = sensorCode; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptProvince() { return deptProvince; }
    public void setDeptProvince(String deptProvince) { this.deptProvince = deptProvince; }
    public String getDeptCity() { return deptCity; }
    public void setDeptCity(String deptCity) { this.deptCity = deptCity; }
    public String getDeptArea() { return deptArea; }
    public void setDeptArea(String deptArea) { this.deptArea = deptArea; }
    public Long getSourceDeptId() { return sourceDeptId; }
    public void setSourceDeptId(Long sourceDeptId) { this.sourceDeptId = sourceDeptId; }
    public String getSourceDeptName() { return sourceDeptName; }
    public void setSourceDeptName(String sourceDeptName) { this.sourceDeptName = sourceDeptName; }
    public String getExternalCompanyName() { return externalCompanyName; }
    public void setExternalCompanyName(String externalCompanyName) { this.externalCompanyName = externalCompanyName; }
    public Long getGatewayId() { return gatewayId; }
    public void setGatewayId(Long gatewayId) { this.gatewayId = gatewayId; }
    public String getGatewayCode() { return gatewayCode; }
    public void setGatewayCode(String gatewayCode) { this.gatewayCode = gatewayCode; }
    public String getMac() { return mac; }
    public void setMac(String mac) { this.mac = mac; }
    public BigDecimal getPressure() { return pressure; }
    public void setPressure(BigDecimal pressure) { this.pressure = pressure; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public Integer getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Integer batteryLevel) { this.batteryLevel = batteryLevel; }
    public Integer getSignalStrength() { return signalStrength; }
    public void setSignalStrength(Integer signalStrength) { this.signalStrength = signalStrength; }
    public Date getLastOnlineTime() { return lastOnlineTime; }
    public void setLastOnlineTime(Date lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    public Date getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("sensorId", getSensorId())
            .append("externalSensorId", getExternalSensorId())
            .append("sensorCode", getSensorCode())
            .append("deptId", getDeptId())
            .append("deptProvince", getDeptProvince())
            .append("deptCity", getDeptCity())
            .append("deptArea", getDeptArea())
            .append("sourceDeptId", getSourceDeptId())
            .append("externalCompanyName", getExternalCompanyName())
            .append("gatewayId", getGatewayId())
            .append("gatewayCode", getGatewayCode())
            .append("mac", getMac())
            .append("pressure", getPressure())
            .append("temperature", getTemperature())
            .append("batteryLevel", getBatteryLevel())
            .append("signalStrength", getSignalStrength())
            .append("lastOnlineTime", getLastOnlineTime())
            .append("status", getStatus())
            .append("syncStatus", getSyncStatus())
            .append("lastSyncTime", getLastSyncTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
