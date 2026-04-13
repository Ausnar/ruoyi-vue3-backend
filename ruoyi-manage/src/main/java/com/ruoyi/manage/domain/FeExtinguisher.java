package com.ruoyi.manage.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeExtinguisher extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long extinguisherId;
    private Long externalExtinguisherId;
    private Long externalCompanyId;
    @Excel(name = "标志钢码")
    private String labelCode;
    @Excel(name = "规格型号")
    private String specification;
    private Long sensorId;
    private String sensorCode;
    private Long deptId;
    private String deptName;
    private Long sourceDeptId;
    private String sourceDeptName;
    private String externalCompanyName;
    @Excel(name = "产品名称")
    private String productName;
    @Excel(name = "生产厂家")
    private String manufacturer;
    @Excel(name = "服务商")
    private String serviceProvider;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date productionDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inspectionDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date scrapDate;
    private Long firePointId;
    @Excel(name = "安装位置")
    private String installLocation;
    @Excel(name = "二维码")
    private String qrCode;
    @Excel(name = "状态")
    private String status;
    private String syncStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSyncTime;
    private String delFlag;

    public Long getExtinguisherId() { return extinguisherId; }
    public void setExtinguisherId(Long extinguisherId) { this.extinguisherId = extinguisherId; }
    public Long getExternalExtinguisherId() { return externalExtinguisherId; }
    public void setExternalExtinguisherId(Long externalExtinguisherId) { this.externalExtinguisherId = externalExtinguisherId; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public String getLabelCode() { return labelCode; }
    public void setLabelCode(String labelCode) { this.labelCode = labelCode; }
    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }
    public Long getSensorId() { return sensorId; }
    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
    public String getSensorCode() { return sensorCode; }
    public void setSensorCode(String sensorCode) { this.sensorCode = sensorCode; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getSourceDeptId() { return sourceDeptId; }
    public void setSourceDeptId(Long sourceDeptId) { this.sourceDeptId = sourceDeptId; }
    public String getSourceDeptName() { return sourceDeptName; }
    public void setSourceDeptName(String sourceDeptName) { this.sourceDeptName = sourceDeptName; }
    public String getExternalCompanyName() { return externalCompanyName; }
    public void setExternalCompanyName(String externalCompanyName) { this.externalCompanyName = externalCompanyName; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getServiceProvider() { return serviceProvider; }
    public void setServiceProvider(String serviceProvider) { this.serviceProvider = serviceProvider; }
    public Date getProductionDate() { return productionDate; }
    public void setProductionDate(Date productionDate) { this.productionDate = productionDate; }
    public Date getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(Date inspectionDate) { this.inspectionDate = inspectionDate; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public Date getScrapDate() { return scrapDate; }
    public void setScrapDate(Date scrapDate) { this.scrapDate = scrapDate; }
    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public String getInstallLocation() { return installLocation; }
    public void setInstallLocation(String installLocation) { this.installLocation = installLocation; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
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
            .append("extinguisherId", getExtinguisherId())
            .append("externalExtinguisherId", getExternalExtinguisherId())
            .append("externalCompanyId", getExternalCompanyId())
            .append("labelCode", getLabelCode())
            .append("specification", getSpecification())
            .append("sensorId", getSensorId())
            .append("sensorCode", getSensorCode())
            .append("deptId", getDeptId())
            .append("sourceDeptId", getSourceDeptId())
            .append("externalCompanyName", getExternalCompanyName())
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
