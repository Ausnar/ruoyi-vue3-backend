package com.ruoyi.manage.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeGateway extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long gatewayId;
    @Excel(name = "外部TBox ID")
    private Long externalTboxId;
    @Excel(name = "网关IMEI")
    private String imei;
    @Excel(name = "SIM卡号")
    private String sim;
    private Long firePointId;
    @Excel(name = "消防点")
    private String firePointName;
    private Long deptId;
    @Excel(name = "归属单位")
    private String deptName;
    private Long sourceDeptId;
    @Excel(name = "来源单位")
    private String sourceDeptName;
    private Long externalCompanyId;
    @Excel(name = "外部单位")
    private String externalCompanyName;
    @Excel(name = "经度")
    private BigDecimal gpsLongitude;
    @Excel(name = "纬度")
    private BigDecimal gpsLatitude;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后在线时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastOnlineTime;
    @Excel(name = "同步状态")
    private String syncStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后同步时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastSyncTime;
    @Excel(name = "状态")
    private String status;
    private String delFlag;

    public Long getGatewayId() { return gatewayId; }
    public void setGatewayId(Long gatewayId) { this.gatewayId = gatewayId; }
    public Long getExternalTboxId() { return externalTboxId; }
    public void setExternalTboxId(Long externalTboxId) { this.externalTboxId = externalTboxId; }
    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }
    public String getSim() { return sim; }
    public void setSim(String sim) { this.sim = sim; }
    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getSourceDeptId() { return sourceDeptId; }
    public void setSourceDeptId(Long sourceDeptId) { this.sourceDeptId = sourceDeptId; }
    public String getSourceDeptName() { return sourceDeptName; }
    public void setSourceDeptName(String sourceDeptName) { this.sourceDeptName = sourceDeptName; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public String getExternalCompanyName() { return externalCompanyName; }
    public void setExternalCompanyName(String externalCompanyName) { this.externalCompanyName = externalCompanyName; }
    public BigDecimal getGpsLongitude() { return gpsLongitude; }
    public void setGpsLongitude(BigDecimal gpsLongitude) { this.gpsLongitude = gpsLongitude; }
    public BigDecimal getGpsLatitude() { return gpsLatitude; }
    public void setGpsLatitude(BigDecimal gpsLatitude) { this.gpsLatitude = gpsLatitude; }
    public Date getLastOnlineTime() { return lastOnlineTime; }
    public void setLastOnlineTime(Date lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    public Date getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("gatewayId", gatewayId)
            .append("externalTboxId", externalTboxId)
            .append("imei", imei)
            .append("sim", sim)
            .append("firePointId", firePointId)
            .append("deptId", deptId)
            .append("sourceDeptId", sourceDeptId)
            .append("externalCompanyId", externalCompanyId)
            .append("externalCompanyName", externalCompanyName)
            .append("gpsLongitude", gpsLongitude)
            .append("gpsLatitude", gpsLatitude)
            .append("lastOnlineTime", lastOnlineTime)
            .append("syncStatus", syncStatus)
            .append("lastSyncTime", lastSyncTime)
            .append("status", status)
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("delFlag", delFlag)
            .toString();
    }
}
