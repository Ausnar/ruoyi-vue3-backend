package com.ruoyi.manage.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消防点信息对象 fe_fire_point
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
public class FeFirePoint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消防点ID */
    private Long firePointId;

    /** 消防点编号 */
    @Excel(name = "消防点编号")
    private String firePointCode;

    /** 消防点名称 */
    @Excel(name = "消防点名称")
    private String firePointName;

    /** 所属部门ID(关联sys_dept) */
    @Excel(name = "所属部门ID(关联sys_dept)")
    private Long deptId;

    /** 所属部门名称 */
    @Excel(name = "所属部门名称")
    private String deptName;

    /** 类型(室内/室外/重点区域等) */
    @Excel(name = "类型(室内/室外/重点区域等)")
    private String pointType;

    /** 位置描述 */
    @Excel(name = "位置描述")
    private String location;

    /** 楼层 */
    @Excel(name = "楼层")
    private String floor;

    /** 建筑物 */
    @Excel(name = "建筑物")
    private String building;

    /** 经度 */
    @Excel(name = "经度")
    private BigDecimal longitude;

    /** 纬度 */
    @Excel(name = "纬度")
    private BigDecimal latitude;

    /** 负责人 */
    @Excel(name = "负责人")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 二维码 */
    @Excel(name = "二维码")
    private String qrCode;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Integer sortOrder;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态(0正常 1停用)")
    private String status;

    /** 删除标志(0存在 2删除) */
    private String delFlag;

    public void setFirePointId(Long firePointId) 
    {
        this.firePointId = firePointId;
    }

    public Long getFirePointId() 
    {
        return firePointId;
    }

    public void setFirePointCode(String firePointCode) 
    {
        this.firePointCode = firePointCode;
    }

    public String getFirePointCode() 
    {
        return firePointCode;
    }

    public void setFirePointName(String firePointName) 
    {
        this.firePointName = firePointName;
    }

    public String getFirePointName() 
    {
        return firePointName;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setPointType(String pointType) 
    {
        this.pointType = pointType;
    }

    public String getPointType() 
    {
        return pointType;
    }

    public void setLocation(String location) 
    {
        this.location = location;
    }

    public String getLocation() 
    {
        return location;
    }

    public void setFloor(String floor) 
    {
        this.floor = floor;
    }

    public String getFloor() 
    {
        return floor;
    }

    public void setBuilding(String building) 
    {
        this.building = building;
    }

    public String getBuilding() 
    {
        return building;
    }

    public void setLongitude(BigDecimal longitude) 
    {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude() 
    {
        return longitude;
    }

    public void setLatitude(BigDecimal latitude) 
    {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude() 
    {
        return latitude;
    }

    public void setContactPerson(String contactPerson) 
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson() 
    {
        return contactPerson;
    }

    public void setContactPhone(String contactPhone) 
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone() 
    {
        return contactPhone;
    }

    public void setQrCode(String qrCode) 
    {
        this.qrCode = qrCode;
    }

    public String getQrCode() 
    {
        return qrCode;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("firePointId", getFirePointId())
            .append("firePointCode", getFirePointCode())
            .append("firePointName", getFirePointName())
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
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
