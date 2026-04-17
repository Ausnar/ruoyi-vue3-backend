package com.ruoyi.manage.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeExternalCompany extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long companyRecordId;
    private Long externalCompanyId;
    private String externalCompanyName;
    private String numberPrefix;
    private String orgPath;
    private Long parentExternalCompanyId;
    private String syncStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstSeenTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSeenTime;
    private Long lastSourceDeptId;
    private String lastSourceDeptName;
    private Long mappedDeptId;
    private String mappedDeptName;

    public Long getCompanyRecordId() { return companyRecordId; }
    public void setCompanyRecordId(Long companyRecordId) { this.companyRecordId = companyRecordId; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public String getExternalCompanyName() { return externalCompanyName; }
    public void setExternalCompanyName(String externalCompanyName) { this.externalCompanyName = externalCompanyName; }
    public String getNumberPrefix() { return numberPrefix; }
    public void setNumberPrefix(String numberPrefix) { this.numberPrefix = numberPrefix; }
    public String getOrgPath() { return orgPath; }
    public void setOrgPath(String orgPath) { this.orgPath = orgPath; }
    public Long getParentExternalCompanyId() { return parentExternalCompanyId; }
    public void setParentExternalCompanyId(Long parentExternalCompanyId) { this.parentExternalCompanyId = parentExternalCompanyId; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    public Date getFirstSeenTime() { return firstSeenTime; }
    public void setFirstSeenTime(Date firstSeenTime) { this.firstSeenTime = firstSeenTime; }
    public Date getLastSeenTime() { return lastSeenTime; }
    public void setLastSeenTime(Date lastSeenTime) { this.lastSeenTime = lastSeenTime; }
    public Long getLastSourceDeptId() { return lastSourceDeptId; }
    public void setLastSourceDeptId(Long lastSourceDeptId) { this.lastSourceDeptId = lastSourceDeptId; }
    public String getLastSourceDeptName() { return lastSourceDeptName; }
    public void setLastSourceDeptName(String lastSourceDeptName) { this.lastSourceDeptName = lastSourceDeptName; }
    public Long getMappedDeptId() { return mappedDeptId; }
    public void setMappedDeptId(Long mappedDeptId) { this.mappedDeptId = mappedDeptId; }
    public String getMappedDeptName() { return mappedDeptName; }
    public void setMappedDeptName(String mappedDeptName) { this.mappedDeptName = mappedDeptName; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("companyRecordId", companyRecordId)
            .append("externalCompanyId", externalCompanyId)
            .append("externalCompanyName", externalCompanyName)
            .append("numberPrefix", numberPrefix)
            .append("orgPath", orgPath)
            .append("parentExternalCompanyId", parentExternalCompanyId)
            .append("syncStatus", syncStatus)
            .append("firstSeenTime", firstSeenTime)
            .append("lastSeenTime", lastSeenTime)
            .append("lastSourceDeptId", lastSourceDeptId)
            .append("mappedDeptId", mappedDeptId)
            .append("mappedDeptName", mappedDeptName)
            .append("remark", getRemark())
            .toString();
    }
}
