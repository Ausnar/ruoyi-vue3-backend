package com.ruoyi.manage.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 灭火器信息对象 fe_extinguisher
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
public class FeExtinguisher extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 灭火器ID */
    private Long extinguisherId;

    /** 标志铭码(唯一标识) */
    @Excel(name = "标志铭码(唯一标识)")
    private String labelCode;

    /** 规格型号 */
    @Excel(name = "规格型号")
    private String specification;

    /** 传感器ID */
    @Excel(name = "传感器ID")
    private Long sensorId;

    /** 传感器编号 */
    @Excel(name = "传感器编号")
    private String sensorCode;

    /** 所属部门ID(关联sys_dept) */
    @Excel(name = "所属部门ID(关联sys_dept)")
    private Long deptId;

    /** 所属部门名称 */
    @Excel(name = "所属部门名称")
    private String deptName;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 生产厂家 */
    @Excel(name = "生产厂家")
    private String manufacturer;

    /** 服务商 */
    @Excel(name = "服务商")
    private String serviceProvider;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date productionDate;

    /** 检验日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "检验日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date inspectionDate;

    /** 有效期至 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期至", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expiryDate;

    /** 报废日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报废日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date scrapDate;

    /** 消防点ID */
    @Excel(name = "消防点ID")
    private Long firePointId;

    /** 安装位置 */
    @Excel(name = "安装位置")
    private String installLocation;

    /** 二维码 */
    @Excel(name = "二维码")
    private String qrCode;

    /** 状态(0正常 1待检 2过期 3停用 4报废) */
    @Excel(name = "状态(0正常 1待检 2过期 3停用 4报废)")
    private String status;

    /** 删除标志(0存在 2删除) */
    private String delFlag;

    public void setExtinguisherId(Long extinguisherId) 
    {
        this.extinguisherId = extinguisherId;
    }

    public Long getExtinguisherId() 
    {
        return extinguisherId;
    }

    public void setLabelCode(String labelCode) 
    {
        this.labelCode = labelCode;
    }

    public String getLabelCode() 
    {
        return labelCode;
    }

    public void setSpecification(String specification) 
    {
        this.specification = specification;
    }

    public String getSpecification() 
    {
        return specification;
    }

    public void setSensorId(Long sensorId) 
    {
        this.sensorId = sensorId;
    }

    public Long getSensorId() 
    {
        return sensorId;
    }

    public void setSensorCode(String sensorCode) 
    {
        this.sensorCode = sensorCode;
    }

    public String getSensorCode() 
    {
        return sensorCode;
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

    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }

    public void setManufacturer(String manufacturer) 
    {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() 
    {
        return manufacturer;
    }

    public void setServiceProvider(String serviceProvider) 
    {
        this.serviceProvider = serviceProvider;
    }

    public String getServiceProvider() 
    {
        return serviceProvider;
    }

    public void setProductionDate(Date productionDate) 
    {
        this.productionDate = productionDate;
    }

    public Date getProductionDate() 
    {
        return productionDate;
    }

    public void setInspectionDate(Date inspectionDate) 
    {
        this.inspectionDate = inspectionDate;
    }

    public Date getInspectionDate() 
    {
        return inspectionDate;
    }

    public void setExpiryDate(Date expiryDate) 
    {
        this.expiryDate = expiryDate;
    }

    public Date getExpiryDate() 
    {
        return expiryDate;
    }

    public void setScrapDate(Date scrapDate) 
    {
        this.scrapDate = scrapDate;
    }

    public Date getScrapDate() 
    {
        return scrapDate;
    }

    public void setFirePointId(Long firePointId) 
    {
        this.firePointId = firePointId;
    }

    public Long getFirePointId() 
    {
        return firePointId;
    }

    public void setInstallLocation(String installLocation) 
    {
        this.installLocation = installLocation;
    }

    public String getInstallLocation() 
    {
        return installLocation;
    }

    public void setQrCode(String qrCode) 
    {
        this.qrCode = qrCode;
    }

    public String getQrCode() 
    {
        return qrCode;
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
            .append("extinguisherId", getExtinguisherId())
            .append("labelCode", getLabelCode())
            .append("specification", getSpecification())
            .append("sensorId", getSensorId())
            .append("sensorCode", getSensorCode())
            .append("deptId", getDeptId())
            .append("productName", getProductName())
            .append("manufacturer", getManufacturer())
            .append("serviceProvider", getServiceProvider())
            .append("productionDate", getProductionDate())
            .append("inspectionDate", getInspectionDate())
            .append("expiryDate", getExpiryDate())
            .append("scrapDate", getScrapDate())
            .append("firePointId", getFirePointId())
            .append("installLocation", getInstallLocation())
            .append("qrCode", getQrCode())
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
