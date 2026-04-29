package com.ruoyi.manage.domain.report;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备报告查询条件。
 */
public class DeviceReportQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String reportType;
    private String startDate;
    private String endDate;
    private Long deptId;
    private Date startTime;
    private Date endTimeExclusive;
    private Date expireSoonEndTime;
    private String reportTypeName;
    private String periodText;
    private String scopeName;

    public String getReportType()
    {
        return reportType;
    }

    public void setReportType(String reportType)
    {
        this.reportType = reportType;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTimeExclusive()
    {
        return endTimeExclusive;
    }

    public void setEndTimeExclusive(Date endTimeExclusive)
    {
        this.endTimeExclusive = endTimeExclusive;
    }

    public Date getExpireSoonEndTime()
    {
        return expireSoonEndTime;
    }

    public void setExpireSoonEndTime(Date expireSoonEndTime)
    {
        this.expireSoonEndTime = expireSoonEndTime;
    }

    public String getReportTypeName()
    {
        return reportTypeName;
    }

    public void setReportTypeName(String reportTypeName)
    {
        this.reportTypeName = reportTypeName;
    }

    public String getPeriodText()
    {
        return periodText;
    }

    public void setPeriodText(String periodText)
    {
        this.periodText = periodText;
    }

    public String getScopeName()
    {
        return scopeName;
    }

    public void setScopeName(String scopeName)
    {
        this.scopeName = scopeName;
    }
}
