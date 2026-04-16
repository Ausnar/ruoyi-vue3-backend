package com.ruoyi.visit.domain.dto;

import java.math.BigDecimal;

public class VisitPassiveCandidate
{
    private String targetType;
    private Long targetId;
    private String targetName;
    private Long externalCompanyId;
    private String externalCompanyName;
    private BigDecimal distanceM;
    private Long firePointId;
    private String firePointName;

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

    public Long getExternalCompanyId()
    {
        return externalCompanyId;
    }

    public void setExternalCompanyId(Long externalCompanyId)
    {
        this.externalCompanyId = externalCompanyId;
    }

    public String getExternalCompanyName()
    {
        return externalCompanyName;
    }

    public void setExternalCompanyName(String externalCompanyName)
    {
        this.externalCompanyName = externalCompanyName;
    }

    public BigDecimal getDistanceM()
    {
        return distanceM;
    }

    public void setDistanceM(BigDecimal distanceM)
    {
        this.distanceM = distanceM;
    }

    public Long getFirePointId()
    {
        return firePointId;
    }

    public void setFirePointId(Long firePointId)
    {
        this.firePointId = firePointId;
    }

    public String getFirePointName()
    {
        return firePointName;
    }

    public void setFirePointName(String firePointName)
    {
        this.firePointName = firePointName;
    }
}
