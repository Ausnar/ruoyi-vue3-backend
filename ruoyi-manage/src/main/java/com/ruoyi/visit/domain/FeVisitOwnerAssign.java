package com.ruoyi.visit.domain;

import java.util.List;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeVisitOwnerAssign extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long assignId;
    private String targetType;
    private Long targetId;
    private String targetName;
    private Long ownerUserId;
    private String ownerUserName;
    private Long ownerDeptId;
    private String ownerDeptName;
    private String status;
    private List<Long> scopeDeptIds;

    public Long getAssignId()
    {
        return assignId;
    }

    public void setAssignId(Long assignId)
    {
        this.assignId = assignId;
    }

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
    }

    public Long getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Long targetId)
    {
        this.targetId = targetId;
    }

    public String getTargetName()
    {
        return targetName;
    }

    public void setTargetName(String targetName)
    {
        this.targetName = targetName;
    }

    public Long getOwnerUserId()
    {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId)
    {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerUserName()
    {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName)
    {
        this.ownerUserName = ownerUserName;
    }

    public Long getOwnerDeptId()
    {
        return ownerDeptId;
    }

    public void setOwnerDeptId(Long ownerDeptId)
    {
        this.ownerDeptId = ownerDeptId;
    }

    public String getOwnerDeptName()
    {
        return ownerDeptName;
    }

    public void setOwnerDeptName(String ownerDeptName)
    {
        this.ownerDeptName = ownerDeptName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public List<Long> getScopeDeptIds()
    {
        return scopeDeptIds;
    }

    public void setScopeDeptIds(List<Long> scopeDeptIds)
    {
        this.scopeDeptIds = scopeDeptIds;
    }
}
