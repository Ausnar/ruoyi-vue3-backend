package com.ruoyi.manage.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 传感器管理对象 fe_sensor
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
public class FeSensor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 传感器ID */
    private Long sensorId;

    /** 传感器编号 */
    @Excel(name = "传感器编号")
    private String sensorCode;

    /** 所属部门ID(关联sys_dept) */
    @Excel(name = "所属部门ID(关联sys_dept)")
    private Long deptId;

    /** 所属部门名称 */
    @Excel(name = "所属部门名称")
    private String deptName;

    /** 网关编号 */
    @Excel(name = "网关编号")
    private String gatewayCode;

    /** 压力值(MPa) */
    @Excel(name = "压力值(MPa)")
    private BigDecimal pressure;

    /** 温度值(℃) */
    @Excel(name = "温度值(℃)")
    private BigDecimal temperature;

    /** 电量(%) */
    @Excel(name = "电量(%)")
    private Integer batteryLevel;

    /** 最后在线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后在线时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastOnlineTime;

    /** 状态(0正常 1异常 2离线) */
    @Excel(name = "状态(0正常 1异常 2离线)")
    private String status;

    /** 删除标志(0存在 2删除) */
    private String delFlag;

    public void setSensorId(Long sensorId) 
    {
        this.sensorId = sensorId;
    }

    public Long getSensorId() 
    {
        return sensorId;
    }

    public void setSensorCode(String sensorCode) 
    {
        this.sensorCode = sensorCode;
    }

    public String getSensorCode() 
    {
        return sensorCode;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setGatewayCode(String gatewayCode) 
    {
        this.gatewayCode = gatewayCode;
    }

    public String getGatewayCode() 
    {
        return gatewayCode;
    }

    public void setPressure(BigDecimal pressure) 
    {
        this.pressure = pressure;
    }

    public BigDecimal getPressure() 
    {
        return pressure;
    }

    public void setTemperature(BigDecimal temperature) 
    {
        this.temperature = temperature;
    }

    public BigDecimal getTemperature() 
    {
        return temperature;
    }

    public void setBatteryLevel(Integer batteryLevel) 
    {
        this.batteryLevel = batteryLevel;
    }

    public Integer getBatteryLevel() 
    {
        return batteryLevel;
    }

    public void setLastOnlineTime(Date lastOnlineTime) 
    {
        this.lastOnlineTime = lastOnlineTime;
    }

    public Date getLastOnlineTime() 
    {
        return lastOnlineTime;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("sensorId", getSensorId())
            .append("sensorCode", getSensorCode())
            .append("deptId", getDeptId())
            .append("gatewayCode", getGatewayCode())
            .append("pressure", getPressure())
            .append("temperature", getTemperature())
            .append("batteryLevel", getBatteryLevel())
            .append("lastOnlineTime", getLastOnlineTime())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
