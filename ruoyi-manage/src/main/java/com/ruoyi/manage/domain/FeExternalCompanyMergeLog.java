package com.ruoyi.manage.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeExternalCompanyMergeLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long mergeLogId;
    private Long sourceCompanyRecordId;
    private Long sourceExternalCompanyId;
    private String sourceExternalCompanyName;
    private Long targetCompanyRecordId;
    private Long targetExternalCompanyId;
    private String targetExternalCompanyName;
    private String mergeStrategy;
    private String mergeReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mergeTime;

    public Long getMergeLogId() { return mergeLogId; }
    public void setMergeLogId(Long mergeLogId) { this.mergeLogId = mergeLogId; }
    public Long getSourceCompanyRecordId() { return sourceCompanyRecordId; }
    public void setSourceCompanyRecordId(Long sourceCompanyRecordId) { this.sourceCompanyRecordId = sourceCompanyRecordId; }
    public Long getSourceExternalCompanyId() { return sourceExternalCompanyId; }
    public void setSourceExternalCompanyId(Long sourceExternalCompanyId) { this.sourceExternalCompanyId = sourceExternalCompanyId; }
    public String getSourceExternalCompanyName() { return sourceExternalCompanyName; }
    public void setSourceExternalCompanyName(String sourceExternalCompanyName) { this.sourceExternalCompanyName = sourceExternalCompanyName; }
    public Long getTargetCompanyRecordId() { return targetCompanyRecordId; }
    public void setTargetCompanyRecordId(Long targetCompanyRecordId) { this.targetCompanyRecordId = targetCompanyRecordId; }
    public Long getTargetExternalCompanyId() { return targetExternalCompanyId; }
    public void setTargetExternalCompanyId(Long targetExternalCompanyId) { this.targetExternalCompanyId = targetExternalCompanyId; }
    public String getTargetExternalCompanyName() { return targetExternalCompanyName; }
    public void setTargetExternalCompanyName(String targetExternalCompanyName) { this.targetExternalCompanyName = targetExternalCompanyName; }
    public String getMergeStrategy() { return mergeStrategy; }
    public void setMergeStrategy(String mergeStrategy) { this.mergeStrategy = mergeStrategy; }
    public String getMergeReason() { return mergeReason; }
    public void setMergeReason(String mergeReason) { this.mergeReason = mergeReason; }
    public Date getMergeTime() { return mergeTime; }
    public void setMergeTime(Date mergeTime) { this.mergeTime = mergeTime; }
}
