package com.ruoyi.issue.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeIssueProjectModule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "模块ID")
    private Long moduleId;

    private Long projectId;

    @Excel(name = "项目名称")
    private String projectName;

    @Excel(name = "模块名称")
    private String moduleName;

    @Excel(name = "状态")
    private String status;

    @Excel(name = "排序")
    private Integer sortOrder;

    private String delFlag;

    public Long getModuleId()
    {
        return moduleId;
    }

    public void setModuleId(Long moduleId)
    {
        this.moduleId = moduleId;
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

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
