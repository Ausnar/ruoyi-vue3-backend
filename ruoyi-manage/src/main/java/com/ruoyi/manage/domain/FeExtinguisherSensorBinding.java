package com.ruoyi.manage.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeExtinguisherSensorBinding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long bindingId;
    private Long extinguisherId;
    private Long sensorId;
    private Long externalExtinguisherId;
    private Long externalSensorId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bindTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date unbindTime;
    private String isActive;
    private String sourceType;

    public Long getBindingId() { return bindingId; }
    public void setBindingId(Long bindingId) { this.bindingId = bindingId; }
    public Long getExtinguisherId() { return extinguisherId; }
    public void setExtinguisherId(Long extinguisherId) { this.extinguisherId = extinguisherId; }
    public Long getSensorId() { return sensorId; }
    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
    public Long getExternalExtinguisherId() { return externalExtinguisherId; }
    public void setExternalExtinguisherId(Long externalExtinguisherId) { this.externalExtinguisherId = externalExtinguisherId; }
    public Long getExternalSensorId() { return externalSensorId; }
    public void setExternalSensorId(Long externalSensorId) { this.externalSensorId = externalSensorId; }
    public Date getBindTime() { return bindTime; }
    public void setBindTime(Date bindTime) { this.bindTime = bindTime; }
    public Date getUnbindTime() { return unbindTime; }
    public void setUnbindTime(Date unbindTime) { this.unbindTime = unbindTime; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("bindingId", bindingId)
            .append("extinguisherId", extinguisherId)
            .append("sensorId", sensorId)
            .append("externalExtinguisherId", externalExtinguisherId)
            .append("externalSensorId", externalSensorId)
            .append("bindTime", bindTime)
            .append("unbindTime", unbindTime)
            .append("isActive", isActive)
            .append("sourceType", sourceType)
            .toString();
    }
}
