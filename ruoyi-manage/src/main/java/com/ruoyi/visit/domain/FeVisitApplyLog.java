package com.ruoyi.visit.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeVisitApplyLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long logId;
    private Long visitId;
    private String actionType;
    private String fromStatus;
    private String toStatus;
    private Long operatorUserId;
    private String operatorUserName;
    private Long operatorDeptId;
    private String operatorDeptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actionTime;

    private String actionComment;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getVisitId()
    {
        return visitId;
    }

    public void setVisitId(Long visitId)
    {
        this.visitId = visitId;
    }

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
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

    public Long getOperatorUserId()
    {
        return operatorUserId;
    }

    public void setOperatorUserId(Long operatorUserId)
    {
        this.operatorUserId = operatorUserId;
    }

    public String getOperatorUserName()
    {
        return operatorUserName;
    }

    public void setOperatorUserName(String operatorUserName)
    {
        this.operatorUserName = operatorUserName;
    }

    public Long getOperatorDeptId()
    {
        return operatorDeptId;
    }

    public void setOperatorDeptId(Long operatorDeptId)
    {
        this.operatorDeptId = operatorDeptId;
    }

    public String getOperatorDeptName()
    {
        return operatorDeptName;
    }

    public void setOperatorDeptName(String operatorDeptName)
    {
        this.operatorDeptName = operatorDeptName;
    }

    public Date getActionTime()
    {
        return actionTime;
    }

    public void setActionTime(Date actionTime)
    {
        this.actionTime = actionTime;
    }

    public String getActionComment()
    {
        return actionComment;
    }

    public void setActionComment(String actionComment)
    {
        this.actionComment = actionComment;
    }
}
