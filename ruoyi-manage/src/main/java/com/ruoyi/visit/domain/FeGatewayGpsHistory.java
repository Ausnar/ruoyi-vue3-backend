package com.ruoyi.visit.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeGatewayGpsHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long historyId;
    private Long gatewayId;
    private Long externalTboxId;
    private Long deptId;
    private BigDecimal gpsLongitude;
    private BigDecimal gpsLatitude;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gpsTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    public Long getHistoryId()
    {
        return historyId;
    }

    public void setHistoryId(Long historyId)
    {
        this.historyId = historyId;
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

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public BigDecimal getGpsLongitude()
    {
        return gpsLongitude;
    }

    public void setGpsLongitude(BigDecimal gpsLongitude)
    {
        this.gpsLongitude = gpsLongitude;
    }

    public BigDecimal getGpsLatitude()
    {
        return gpsLatitude;
    }

    public void setGpsLatitude(BigDecimal gpsLatitude)
    {
        this.gpsLatitude = gpsLatitude;
    }

    public Date getGpsTime()
    {
        return gpsTime;
    }

    public void setGpsTime(Date gpsTime)
    {
        this.gpsTime = gpsTime;
    }

    public Date getSyncTime()
    {
        return syncTime;
    }

    public void setSyncTime(Date syncTime)
    {
        this.syncTime = syncTime;
    }
}
