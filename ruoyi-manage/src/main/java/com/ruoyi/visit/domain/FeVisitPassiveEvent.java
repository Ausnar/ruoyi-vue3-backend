package com.ruoyi.visit.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeVisitPassiveEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long eventId;
    private Long gatewayId;
    private Long externalTboxId;
    private String gatewayImei;
    private Long deptId;
    private String deptName;
    private Long firePointId;
    private String firePointName;
    private BigDecimal fromLongitude;
    private BigDecimal fromLatitude;
    private BigDecimal toLongitude;
    private BigDecimal toLatitude;
    private BigDecimal distanceM;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date triggerTime;

    private String candidateSummary;
    private String selectedTargetType;
    private Long selectedTargetId;
    private String selectedTargetName;
    private Long selectedExternalCompanyId;
    private String selectedExternalCompanyName;
    private String status;
    private Long confirmUserId;
    private String confirmUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    private Long visitId;
    private List<Long> scopeDeptIds;

    public Long getEventId()
    {
        return eventId;
    }

    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }

    public Long getGatewayId()
    {
        return gatewayId;
    }

    public void setGatewayId(Long gatewayId)
    {
        this.gatewayId = gatewayId;
    }

    public Long getExternalTboxId()
    {
        return externalTboxId;
    }

    public void setExternalTboxId(Long externalTboxId)
    {
        this.externalTboxId = externalTboxId;
    }

    public String getGatewayImei()
    {
        return gatewayImei;
    }

    public void setGatewayImei(String gatewayImei)
    {
        this.gatewayImei = gatewayImei;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
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

    public BigDecimal getFromLongitude()
    {
        return fromLongitude;
    }

    public void setFromLongitude(BigDecimal fromLongitude)
    {
        this.fromLongitude = fromLongitude;
    }

    public BigDecimal getFromLatitude()
    {
        return fromLatitude;
    }

    public void setFromLatitude(BigDecimal fromLatitude)
    {
        this.fromLatitude = fromLatitude;
    }

    public BigDecimal getToLongitude()
    {
        return toLongitude;
    }

    public void setToLongitude(BigDecimal toLongitude)
    {
        this.toLongitude = toLongitude;
    }

    public BigDecimal getToLatitude()
    {
        return toLatitude;
    }

    public void setToLatitude(BigDecimal toLatitude)
    {
        this.toLatitude = toLatitude;
    }

    public BigDecimal getDistanceM()
    {
        return distanceM;
    }

    public void setDistanceM(BigDecimal distanceM)
    {
        this.distanceM = distanceM;
    }

    public Date getTriggerTime()
    {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime)
    {
        this.triggerTime = triggerTime;
    }

    public String getCandidateSummary()
    {
        return candidateSummary;
    }

    public void setCandidateSummary(String candidateSummary)
    {
        this.candidateSummary = candidateSummary;
    }

    public String getSelectedTargetType()
    {
        return selectedTargetType;
    }

    public void setSelectedTargetType(String selectedTargetType)
    {
        this.selectedTargetType = selectedTargetType;
    }

    public Long getSelectedTargetId()
    {
        return selectedTargetId;
    }

    public void setSelectedTargetId(Long selectedTargetId)
    {
        this.selectedTargetId = selectedTargetId;
    }

    public String getSelectedTargetName()
    {
        return selectedTargetName;
    }

    public void setSelectedTargetName(String selectedTargetName)
    {
        this.selectedTargetName = selectedTargetName;
    }

    public Long getSelectedExternalCompanyId()
    {
        return selectedExternalCompanyId;
    }

    public void setSelectedExternalCompanyId(Long selectedExternalCompanyId)
    {
        this.selectedExternalCompanyId = selectedExternalCompanyId;
    }

    public String getSelectedExternalCompanyName()
    {
        return selectedExternalCompanyName;
    }

    public void setSelectedExternalCompanyName(String selectedExternalCompanyName)
    {
        this.selectedExternalCompanyName = selectedExternalCompanyName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getConfirmUserId()
    {
        return confirmUserId;
    }

    public void setConfirmUserId(Long confirmUserId)
    {
        this.confirmUserId = confirmUserId;
    }

    public String getConfirmUserName()
    {
        return confirmUserName;
    }

    public void setConfirmUserName(String confirmUserName)
    {
        this.confirmUserName = confirmUserName;
    }

    public Date getConfirmTime()
    {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime)
    {
        this.confirmTime = confirmTime;
    }

    public Long getVisitId()
    {
        return visitId;
    }

    public void setVisitId(Long visitId)
    {
        this.visitId = visitId;
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
