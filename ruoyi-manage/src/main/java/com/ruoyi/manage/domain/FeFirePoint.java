package com.ruoyi.manage.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeFirePoint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long firePointId;
    private Long externalStationId;
    @Excel(name = "消防点编码")
    private String firePointCode;
    @Excel(name = "消防点名称")
    private String firePointName;
    private String stationNumber;
    private String stationType;
    private Long externalCompanyId;
    private String externalCompanyName;
    private Long sourceDeptId;
    private String sourceDeptName;
    @Excel(name = "所属部门ID")
    private Long deptId;
    private String deptName;
    @Excel(name = "类型")
    private String pointType;
    @Excel(name = "位置")
    private String location;
    @Excel(name = "楼层")
    private String floor;
    @Excel(name = "建筑")
    private String building;
    @Excel(name = "经度")
    private BigDecimal longitude;
    @Excel(name = "纬度")
    private BigDecimal latitude;
    @Excel(name = "负责人")
    private String contactPerson;
    @Excel(name = "联系电话")
    private String contactPhone;
    @Excel(name = "二维码")
    private String qrCode;
    @Excel(name = "排序")
    private Integer sortOrder;
    @Excel(name = "状态")
    private String status;
    private String syncStatus;
    private Date lastSyncTime;
    private String delFlag;

    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public Long getExternalStationId() { return externalStationId; }
    public void setExternalStationId(Long externalStationId) { this.externalStationId = externalStationId; }
    public String getFirePointCode() { return firePointCode; }
    public void setFirePointCode(String firePointCode) { this.firePointCode = firePointCode; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public String getStationNumber() { return stationNumber; }
    public void setStationNumber(String stationNumber) { this.stationNumber = stationNumber; }
    public String getStationType() { return stationType; }
    public void setStationType(String stationType) { this.stationType = stationType; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public String getExternalCompanyName() { return externalCompanyName; }
    public void setExternalCompanyName(String externalCompanyName) { this.externalCompanyName = externalCompanyName; }
    public Long getSourceDeptId() { return sourceDeptId; }
    public void setSourceDeptId(Long sourceDeptId) { this.sourceDeptId = sourceDeptId; }
    public String getSourceDeptName() { return sourceDeptName; }
    public void setSourceDeptName(String sourceDeptName) { this.sourceDeptName = sourceDeptName; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getPointType() { return pointType; }
    public void setPointType(String pointType) { this.pointType = pointType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
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
            .append("firePointId", getFirePointId())
            .append("externalStationId", getExternalStationId())
            .append("firePointCode", getFirePointCode())
            .append("firePointName", getFirePointName())
            .append("stationNumber", getStationNumber())
            .append("stationType", getStationType())
            .append("externalCompanyId", getExternalCompanyId())
            .append("externalCompanyName", getExternalCompanyName())
            .append("sourceDeptId", getSourceDeptId())
            .append("deptId", getDeptId())
            .append("pointType", getPointType())
            .append("location", getLocation())
            .append("floor", getFloor())
            .append("building", getBuilding())
            .append("longitude", getLongitude())
            .append("latitude", getLatitude())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("qrCode", getQrCode())
            .append("sortOrder", getSortOrder())
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
