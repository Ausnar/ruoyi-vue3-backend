package com.ruoyi.issue.domain;

import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeIssueItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "编号")
    private Long issueId;

    private Long projectId;

    @Excel(name = "项目")
    private String projectName;

    private Long moduleId;

    @Excel(name = "模块")
    private String moduleNameSnapshot;

    @Excel(name = "问题")
    private String issueTitle;

    @Excel(name = "优先级")
    private String priority;

    @Excel(name = "当前状态")
    private String status;

    @Excel(name = "责任侧")
    private String ownerSide;

    @Excel(name = "说明")
    private String currentSummary;

    @Excel(name = "状态说明")
    private String statusDetail;

    private String delFlag;

    private String changeSummary;

    private List<FeIssueItemLog> logs;

    private String keyword;

    public Long getIssueId()
    {
        return issueId;
    }

    public void setIssueId(Long issueId)
    {
        this.issueId = issueId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public Long getModuleId()
    {
        return moduleId;
    }

    public void setModuleId(Long moduleId)
    {
        this.moduleId = moduleId;
    }

    public String getModuleNameSnapshot()
    {
        return moduleNameSnapshot;
    }

    public void setModuleNameSnapshot(String moduleNameSnapshot)
    {
        this.moduleNameSnapshot = moduleNameSnapshot;
    }

    public String getIssueTitle()
    {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle)
    {
        this.issueTitle = issueTitle;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getOwnerSide()
    {
        return ownerSide;
    }

    public void setOwnerSide(String ownerSide)
    {
        this.ownerSide = ownerSide;
    }

    public String getCurrentSummary()
    {
        return currentSummary;
    }

    public void setCurrentSummary(String currentSummary)
    {
        this.currentSummary = currentSummary;
    }

    public String getStatusDetail()
    {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail)
    {
        this.statusDetail = statusDetail;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getChangeSummary()
    {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary)
    {
        this.changeSummary = changeSummary;
    }

    public List<FeIssueItemLog> getLogs()
    {
        return logs;
    }

    public void setLogs(List<FeIssueItemLog> logs)
    {
        this.logs = logs;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
}
