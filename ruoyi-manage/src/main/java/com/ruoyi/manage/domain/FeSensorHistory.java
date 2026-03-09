package com.ruoyi.manage.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 传感器历史数据对象 fe_sensor_history
 *
 * @author ruoyi
 * @date 2026-03-09
 */
public class FeSensorHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 历史记录ID */
    private Long historyId;

    /** 传感器ID */
    private Long sensorId;

    /** 传感器编号 */
    @Excel(name = "传感器编号")
    private String sensorCode;

    /** 压力值(MPa) */
    @Excel(name = "压力值(MPa)")
    private BigDecimal pressure;

    /** 温度值(℃) */
    @Excel(name = "温度值(℃)")
    private BigDecimal temperature;

    /** 电量(%) */
    @Excel(name = "电量(%)")
    private Integer batteryLevel;

    /** 信号强度 */
    @Excel(name = "信号强度")
    private Integer signalStrength;

    /** 状态(0正常 1异常 2离线) */
    @Excel(name = "状态(0正常 1异常 2离线)")
    private String status;

    /** 查询开始时间 */
    private String startTime;

    /** 查询结束时间 */
    private String endTime;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getHistoryId()
    {
        return historyId;
    }

    public void setHistoryId(Long historyId)
    {
        this.historyId = historyId;
    }

    public Long getSensorId()
    {
        return sensorId;
    }

    public void setSensorId(Long sensorId)
    {
        this.sensorId = sensorId;
    }

    public String getSensorCode()
    {
        return sensorCode;
    }

    public void setSensorCode(String sensorCode)
    {
        this.sensorCode = sensorCode;
    }

    public BigDecimal getPressure()
    {
        return pressure;
    }

    public void setPressure(BigDecimal pressure)
    {
        this.pressure = pressure;
    }

    public BigDecimal getTemperature()
    {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature)
    {
        this.temperature = temperature;
    }

    public Integer getBatteryLevel()
    {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel)
    {
        this.batteryLevel = batteryLevel;
    }

    public Integer getSignalStrength()
    {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength)
    {
        this.signalStrength = signalStrength;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("historyId", getHistoryId())
            .append("sensorId", getSensorId())
            .append("sensorCode", getSensorCode())
            .append("pressure", getPressure())
            .append("temperature", getTemperature())
            .append("batteryLevel", getBatteryLevel())
            .append("signalStrength", getSignalStrength())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .toString();
    }
}
