package com.ruoyi.manage.domain.report;

import java.io.Serializable;

public class RuntimeDetailDeviceOption implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long deviceId;
    private String deviceCode;
    private String secondaryCode;
    private Long firePointId;
    private String firePointName;
    private String deptName;

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }
    public String getSecondaryCode() { return secondaryCode; }
    public void setSecondaryCode(String secondaryCode) { this.secondaryCode = secondaryCode; }
    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
}
