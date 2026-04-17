package com.ruoyi.manage.domain.dto;

public class FeExternalCompanyMergeRequest
{
    private Long sourceCompanyRecordId;
    private Long targetCompanyRecordId;
    private String mergeReason;

    public Long getSourceCompanyRecordId() { return sourceCompanyRecordId; }
    public void setSourceCompanyRecordId(Long sourceCompanyRecordId) { this.sourceCompanyRecordId = sourceCompanyRecordId; }
    public Long getTargetCompanyRecordId() { return targetCompanyRecordId; }
    public void setTargetCompanyRecordId(Long targetCompanyRecordId) { this.targetCompanyRecordId = targetCompanyRecordId; }
    public String getMergeReason() { return mergeReason; }
    public void setMergeReason(String mergeReason) { this.mergeReason = mergeReason; }
}
