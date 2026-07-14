package com.ruoyi.manage.domain.report;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

public class DeviceOperationReportQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String periodType;
    private String periodDate;
    private Long deptId;
    private Date beginTime;
    private Date endTime;
    private String periodTypeName;
    private String periodLabel;
    private String scopeName;

    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }
    public String getPeriodDate() { return periodDate; }
    public void setPeriodDate(String periodDate) { this.periodDate = periodDate; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Date getBeginTime() { return beginTime; }
    public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getPeriodTypeName() { return periodTypeName; }
    public void setPeriodTypeName(String periodTypeName) { this.periodTypeName = periodTypeName; }
    public String getPeriodLabel() { return periodLabel; }
    public void setPeriodLabel(String periodLabel) { this.periodLabel = periodLabel; }
    public String getScopeName() { return scopeName; }
    public void setScopeName(String scopeName) { this.scopeName = scopeName; }
}
