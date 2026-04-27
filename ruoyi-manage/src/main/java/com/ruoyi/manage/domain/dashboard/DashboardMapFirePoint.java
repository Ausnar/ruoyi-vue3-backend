package com.ruoyi.manage.domain.dashboard;

import java.math.BigDecimal;

public class DashboardMapFirePoint
{
    private Long firePointId;
    private String firePointName;
    private String location;
    private String building;
    private String floor;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Long deptId;
    private String deptName;
    private Long resolvedDeptId;
    private String resolvedDeptName;
    private Long externalCompanyId;
    private String externalCompanyName;
    private String contactPerson;
    private String contactPhone;
    private String status;

    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getResolvedDeptId() { return resolvedDeptId; }
    public void setResolvedDeptId(Long resolvedDeptId) { this.resolvedDeptId = resolvedDeptId; }
    public String getResolvedDeptName() { return resolvedDeptName; }
    public void setResolvedDeptName(String resolvedDeptName) { this.resolvedDeptName = resolvedDeptName; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public String getExternalCompanyName() { return externalCompanyName; }
    public void setExternalCompanyName(String externalCompanyName) { this.externalCompanyName = externalCompanyName; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
