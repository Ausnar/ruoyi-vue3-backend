package com.ruoyi.visit.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class VisitFeedbackRequest
{
    private Long visitId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualEndTime;

    private String resultDesc;
    private String visitConclusion;
    private String intentionLevel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextFollowTime;

    private String resultAttachments;
    private String remark;

    public Long getVisitId()
    {
        return visitId;
    }

    public void setVisitId(Long visitId)
    {
        this.visitId = visitId;
    }

    public Date getActualStartTime()
    {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime)
    {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualEndTime()
    {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime)
    {
        this.actualEndTime = actualEndTime;
    }

    public String getResultDesc()
    {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc)
    {
        this.resultDesc = resultDesc;
    }

    public String getVisitConclusion()
    {
        return visitConclusion;
    }

    public void setVisitConclusion(String visitConclusion)
    {
        this.visitConclusion = visitConclusion;
    }

    public String getIntentionLevel()
    {
        return intentionLevel;
    }

    public void setIntentionLevel(String intentionLevel)
    {
        this.intentionLevel = intentionLevel;
    }

    public Date getNextFollowTime()
    {
        return nextFollowTime;
    }

    public void setNextFollowTime(Date nextFollowTime)
    {
        this.nextFollowTime = nextFollowTime;
    }

    public String getResultAttachments()
    {
        return resultAttachments;
    }

    public void setResultAttachments(String resultAttachments)
    {
        this.resultAttachments = resultAttachments;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
