package com.ruoyi.issue.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

public class FeIssueItemLog
{
    @Excel(name = "日志ID")
    private Long logId;

    private Long issueId;

    @Excel(name = "原状态")
    private String fromStatus;

    @Excel(name = "新状态")
    private String toStatus;

    @Excel(name = "原责任侧")
    private String fromOwnerSide;

    @Excel(name = "新责任侧")
    private String toOwnerSide;

    @Excel(name = "变更说明")
    private String changeSummary;

    @Excel(name = "操作人")
    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getIssueId()
    {
        return issueId;
    }

    public void setIssueId(Long issueId)
    {
        this.issueId = issueId;
    }

    public String getFromStatus()
    {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus)
    {
        this.fromStatus = fromStatus;
    }

    public String getToStatus()
    {
        return toStatus;
    }

    public void setToStatus(String toStatus)
    {
        this.toStatus = toStatus;
    }

    public String getFromOwnerSide()
    {
        return fromOwnerSide;
    }

    public void setFromOwnerSide(String fromOwnerSide)
    {
        this.fromOwnerSide = fromOwnerSide;
    }

    public String getToOwnerSide()
    {
        return toOwnerSide;
    }

    public void setToOwnerSide(String toOwnerSide)
    {
        this.toOwnerSide = toOwnerSide;
    }

    public String getChangeSummary()
    {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary)
    {
        this.changeSummary = changeSummary;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
