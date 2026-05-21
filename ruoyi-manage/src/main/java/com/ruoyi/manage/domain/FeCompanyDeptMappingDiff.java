package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class FeCompanyDeptMappingDiff extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long externalCompanyId;
    private String externalCompanyName;
    private Long oldMappingDeptId;
    private String oldMappingDeptName;
    private Long sdkDeptId;
    private String sdkDeptName;
    private Long sdkParentDeptId;
    private String sdkParentDeptName;
    private String diffStatus;
    private Integer firePointCount;
    private Integer gatewayCount;
    private Integer sensorCount;
    private Integer extinguisherCount;

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

    public Long getOldMappingDeptId()
    {
        return oldMappingDeptId;
    }

    public void setOldMappingDeptId(Long oldMappingDeptId)
    {
        this.oldMappingDeptId = oldMappingDeptId;
    }

    public String getOldMappingDeptName()
    {
        return oldMappingDeptName;
    }

    public void setOldMappingDeptName(String oldMappingDeptName)
    {
        this.oldMappingDeptName = oldMappingDeptName;
    }

    public Long getSdkDeptId()
    {
        return sdkDeptId;
    }

    public void setSdkDeptId(Long sdkDeptId)
    {
        this.sdkDeptId = sdkDeptId;
    }

    public String getSdkDeptName()
    {
        return sdkDeptName;
    }

    public void setSdkDeptName(String sdkDeptName)
    {
        this.sdkDeptName = sdkDeptName;
    }

    public Long getSdkParentDeptId()
    {
        return sdkParentDeptId;
    }

    public void setSdkParentDeptId(Long sdkParentDeptId)
    {
        this.sdkParentDeptId = sdkParentDeptId;
    }

    public String getSdkParentDeptName()
    {
        return sdkParentDeptName;
    }

    public void setSdkParentDeptName(String sdkParentDeptName)
    {
        this.sdkParentDeptName = sdkParentDeptName;
    }

    public String getDiffStatus()
    {
        return diffStatus;
    }

    public void setDiffStatus(String diffStatus)
    {
        this.diffStatus = diffStatus;
    }

    public Integer getFirePointCount()
    {
        return firePointCount;
    }

    public void setFirePointCount(Integer firePointCount)
    {
        this.firePointCount = firePointCount;
    }

    public Integer getGatewayCount()
    {
        return gatewayCount;
    }

    public void setGatewayCount(Integer gatewayCount)
    {
        this.gatewayCount = gatewayCount;
    }

    public Integer getSensorCount()
    {
        return sensorCount;
    }

    public void setSensorCount(Integer sensorCount)
    {
        this.sensorCount = sensorCount;
    }

    public Integer getExtinguisherCount()
    {
        return extinguisherCount;
    }

    public void setExtinguisherCount(Integer extinguisherCount)
    {
        this.extinguisherCount = extinguisherCount;
    }
}
