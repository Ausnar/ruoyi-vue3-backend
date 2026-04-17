package com.ruoyi.manage.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeExternalCompany extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "记录ID")
    private Long companyRecordId;

    @Excel(name = "外部单位ID")
    private Long externalCompanyId;

    @Excel(name = "SDK原始名称")
    private String externalCompanyName;

    @Excel(name = "人工治理名")
    private String manualConfirmedName;

    @Excel(name = "编号前缀")
    private String numberPrefix;

    @Excel(name = "组织路径")
    private String orgPath;

    private Long parentExternalCompanyId;

    @Excel(name = "同步状态")
    private String syncStatus;

    @Excel(name = "首次来源")
    private String firstSourceType;

    @Excel(name = "SDK已观测")
    private String sdkObserved;

    @Excel(name = "人工已创建")
    private String manualCreated;

    private String manualCreatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date manualCreatedTime;

    @Excel(name = "记录状态")
    private String recordStatus;

    private Long mergedToCompanyRecordId;

    private Long mergedToExternalCompanyId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "首次SDK观测时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date firstSeenTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最近SDK观测时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastSeenTime;

    private Long lastSourceDeptId;

    @Excel(name = "最近来源单位")
    private String lastSourceDeptName;

    private Long mappedDeptId;

    @Excel(name = "当前映射合同单位")
    private String mappedDeptName;

    private String duplicateMatchType;

    private String duplicateMatchReason;

    private Integer duplicatePriority;

    public Long getCompanyRecordId()
    {
        return companyRecordId;
    }

    public void setCompanyRecordId(Long companyRecordId)
    {
        this.companyRecordId = companyRecordId;
    }

    public Long getExternalCompanyId()
    {
        return externalCompanyId;
    }

    public void setExternalCompanyId(Long externalCompanyId)
    {
        this.externalCompanyId = externalCompanyId;
    }

    public String getExternalCompanyName()
    {
        return externalCompanyName;
    }

    public void setExternalCompanyName(String externalCompanyName)
    {
        this.externalCompanyName = externalCompanyName;
    }

    public String getManualConfirmedName()
    {
        return manualConfirmedName;
    }

    public void setManualConfirmedName(String manualConfirmedName)
    {
        this.manualConfirmedName = manualConfirmedName;
    }

    public String getNumberPrefix()
    {
        return numberPrefix;
    }

    public void setNumberPrefix(String numberPrefix)
    {
        this.numberPrefix = numberPrefix;
    }

    public String getOrgPath()
    {
        return orgPath;
    }

    public void setOrgPath(String orgPath)
    {
        this.orgPath = orgPath;
    }

    public Long getParentExternalCompanyId()
    {
        return parentExternalCompanyId;
    }

    public void setParentExternalCompanyId(Long parentExternalCompanyId)
    {
        this.parentExternalCompanyId = parentExternalCompanyId;
    }

    public String getSyncStatus()
    {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus)
    {
        this.syncStatus = syncStatus;
    }

    public String getFirstSourceType()
    {
        return firstSourceType;
    }

    public void setFirstSourceType(String firstSourceType)
    {
        this.firstSourceType = firstSourceType;
    }

    public String getSdkObserved()
    {
        return sdkObserved;
    }

    public void setSdkObserved(String sdkObserved)
    {
        this.sdkObserved = sdkObserved;
    }

    public String getManualCreated()
    {
        return manualCreated;
    }

    public void setManualCreated(String manualCreated)
    {
        this.manualCreated = manualCreated;
    }

    public String getManualCreatedBy()
    {
        return manualCreatedBy;
    }

    public void setManualCreatedBy(String manualCreatedBy)
    {
        this.manualCreatedBy = manualCreatedBy;
    }

    public Date getManualCreatedTime()
    {
        return manualCreatedTime;
    }

    public void setManualCreatedTime(Date manualCreatedTime)
    {
        this.manualCreatedTime = manualCreatedTime;
    }

    public String getRecordStatus()
    {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus)
    {
        this.recordStatus = recordStatus;
    }

    public Long getMergedToCompanyRecordId()
    {
        return mergedToCompanyRecordId;
    }

    public void setMergedToCompanyRecordId(Long mergedToCompanyRecordId)
    {
        this.mergedToCompanyRecordId = mergedToCompanyRecordId;
    }

    public Long getMergedToExternalCompanyId()
    {
        return mergedToExternalCompanyId;
    }

    public void setMergedToExternalCompanyId(Long mergedToExternalCompanyId)
    {
        this.mergedToExternalCompanyId = mergedToExternalCompanyId;
    }

    public Date getFirstSeenTime()
    {
        return firstSeenTime;
    }

    public void setFirstSeenTime(Date firstSeenTime)
    {
        this.firstSeenTime = firstSeenTime;
    }

    public Date getLastSeenTime()
    {
        return lastSeenTime;
    }

    public void setLastSeenTime(Date lastSeenTime)
    {
        this.lastSeenTime = lastSeenTime;
    }

    public Long getLastSourceDeptId()
    {
        return lastSourceDeptId;
    }

    public void setLastSourceDeptId(Long lastSourceDeptId)
    {
        this.lastSourceDeptId = lastSourceDeptId;
    }

    public String getLastSourceDeptName()
    {
        return lastSourceDeptName;
    }

    public void setLastSourceDeptName(String lastSourceDeptName)
    {
        this.lastSourceDeptName = lastSourceDeptName;
    }

    public Long getMappedDeptId()
    {
        return mappedDeptId;
    }

    public void setMappedDeptId(Long mappedDeptId)
    {
        this.mappedDeptId = mappedDeptId;
    }

    public String getMappedDeptName()
    {
        return mappedDeptName;
    }

    public void setMappedDeptName(String mappedDeptName)
    {
        this.mappedDeptName = mappedDeptName;
    }

    public String getDuplicateMatchType()
    {
        return duplicateMatchType;
    }

    public void setDuplicateMatchType(String duplicateMatchType)
    {
        this.duplicateMatchType = duplicateMatchType;
    }

    public String getDuplicateMatchReason()
    {
        return duplicateMatchReason;
    }

    public void setDuplicateMatchReason(String duplicateMatchReason)
    {
        this.duplicateMatchReason = duplicateMatchReason;
    }

    public Integer getDuplicatePriority()
    {
        return duplicatePriority;
    }

    public void setDuplicatePriority(Integer duplicatePriority)
    {
        this.duplicatePriority = duplicatePriority;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("companyRecordId", companyRecordId)
            .append("externalCompanyId", externalCompanyId)
            .append("externalCompanyName", externalCompanyName)
            .append("manualConfirmedName", manualConfirmedName)
            .append("numberPrefix", numberPrefix)
            .append("orgPath", orgPath)
            .append("parentExternalCompanyId", parentExternalCompanyId)
            .append("syncStatus", syncStatus)
            .append("firstSourceType", firstSourceType)
            .append("sdkObserved", sdkObserved)
            .append("manualCreated", manualCreated)
            .append("manualCreatedBy", manualCreatedBy)
            .append("manualCreatedTime", manualCreatedTime)
            .append("recordStatus", recordStatus)
            .append("mergedToCompanyRecordId", mergedToCompanyRecordId)
            .append("mergedToExternalCompanyId", mergedToExternalCompanyId)
            .append("firstSeenTime", firstSeenTime)
            .append("lastSeenTime", lastSeenTime)
            .append("lastSourceDeptId", lastSourceDeptId)
            .append("lastSourceDeptName", lastSourceDeptName)
            .append("mappedDeptId", mappedDeptId)
            .append("mappedDeptName", mappedDeptName)
            .append("duplicateMatchType", duplicateMatchType)
            .append("duplicateMatchReason", duplicateMatchReason)
            .append("duplicatePriority", duplicatePriority)
            .append("remark", getRemark())
            .toString();
    }
}
