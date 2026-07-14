package com.ruoyi.manage.domain.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 设备运行明细展示行。
 */
public class RuntimeDetailRow implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long rowId;
    private String detailType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;
    private Long deptId;
    private String deptName;
    private Long firePointId;
    private String firePointName;
    private String deviceCode;
    private String gatewayCode;
    private String gatewayImei;
    private BigDecimal pressure;
    private BigDecimal temperature;
    private Integer batteryLevel;
    private Integer signalStrength;
    private String status;
    private String dataQuality;
    private BigDecimal longitude;
    private BigDecimal latitude;

    public Long getRowId() { return rowId; }
    public void setRowId(Long rowId) { this.rowId = rowId; }
    public String getDetailType() { return detailType; }
    public void setDetailType(String detailType) { this.detailType = detailType; }
    public Date getRecordTime() { return recordTime; }
    public void setRecordTime(Date recordTime) { this.recordTime = recordTime; }
    public Date getSyncTime() { return syncTime; }
    public void setSyncTime(Date syncTime) { this.syncTime = syncTime; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }
    public String getGatewayCode() { return gatewayCode; }
    public void setGatewayCode(String gatewayCode) { this.gatewayCode = gatewayCode; }
    public String getGatewayImei() { return gatewayImei; }
    public void setGatewayImei(String gatewayImei) { this.gatewayImei = gatewayImei; }
    public BigDecimal getPressure() { return pressure; }
    public void setPressure(BigDecimal pressure) { this.pressure = pressure; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public Integer getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Integer batteryLevel) { this.batteryLevel = batteryLevel; }
    public Integer getSignalStrength() { return signalStrength; }
    public void setSignalStrength(Integer signalStrength) { this.signalStrength = signalStrength; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDataQuality() { return dataQuality; }
    public void setDataQuality(String dataQuality) { this.dataQuality = dataQuality; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
}
