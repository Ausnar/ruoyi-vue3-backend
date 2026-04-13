package com.ruoyi.manage.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeSdkSyncLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long syncLogId;
    private Long deptId;
    private Long externalCompanyId;
    private String syncScope;
    private String syncStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private Integer successCount;
    private Integer failCount;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date tokenExpireTime;

    public Long getSyncLogId() { return syncLogId; }
    public void setSyncLogId(Long syncLogId) { this.syncLogId = syncLogId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public String getSyncScope() { return syncScope; }
    public void setSyncScope(String syncScope) { this.syncScope = syncScope; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }
    public Integer getFailCount() { return failCount; }
    public void setFailCount(Integer failCount) { this.failCount = failCount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getTokenExpireTime() { return tokenExpireTime; }
    public void setTokenExpireTime(Date tokenExpireTime) { this.tokenExpireTime = tokenExpireTime; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("syncLogId", syncLogId)
            .append("deptId", deptId)
            .append("externalCompanyId", externalCompanyId)
            .append("syncScope", syncScope)
            .append("syncStatus", syncStatus)
            .append("startTime", startTime)
            .append("endTime", endTime)
            .append("successCount", successCount)
            .append("failCount", failCount)
            .append("message", message)
            .append("tokenExpireTime", tokenExpireTime)
            .toString();
    }
}
