package com.ruoyi.record.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报警记录对象 fe_alarm_record
 * 
 * @author ruoyi
 * @date 2026-02-09
 */
public class FeAlarmRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报警ID */
    private Long alarmId;

    /** 报警编号 */
    @Excel(name = "报警编号")
    private String alarmNo;

    /** 传感器ID */
    @Excel(name = "传感器ID")
    private Long sensorId;

    /** 灭火器ID */
    @Excel(name = "灭火器ID")
    private Long extinguisherId;

    /** 所属部门ID */
    @Excel(name = "所属部门ID")
    private Long deptId;

    /** 报警类型(压力异常/温度异常/低电量/离线/过期预警) */
    @Excel(name = "报警类型(压力异常/温度异常/低电量/离线/过期预警)")
    private String alarmType;

    /** 报警级别(1提醒 2警告 3严重) */
    @Excel(name = "报警级别(1提醒 2警告 3严重)")
    private String alarmLevel;

    /** 报警内容 */
    @Excel(name = "报警内容")
    private String alarmContent;

    /** 报警值 */
    @Excel(name = "报警值")
    private String alarmValue;

    /** 报警时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报警时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date alarmTime;

    /** 处理状态(0未处理 1处理中 2已处理 3已忽略) */
    @Excel(name = "处理状态(0未处理 1处理中 2已处理 3已忽略)")
    private String handleStatus;

    /** 处理人 */
    @Excel(name = "处理人")
    private String handlePerson;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date handleTime;

    /** 处理结果 */
    @Excel(name = "处理结果")
    private String handleResult;

    /** 是否已通知(0否 1是) */
    @Excel(name = "是否已通知(0否 1是)")
    private String isNotified;

    /** 通知时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "通知时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date notifyTime;

    public void setAlarmId(Long alarmId) 
    {
        this.alarmId = alarmId;
    }

    public Long getAlarmId() 
    {
        return alarmId;
    }

    public void setAlarmNo(String alarmNo) 
    {
        this.alarmNo = alarmNo;
    }

    public String getAlarmNo() 
    {
        return alarmNo;
    }

    public void setSensorId(Long sensorId) 
    {
        this.sensorId = sensorId;
    }

    public Long getSensorId() 
    {
        return sensorId;
    }

    public void setExtinguisherId(Long extinguisherId) 
    {
        this.extinguisherId = extinguisherId;
    }

    public Long getExtinguisherId() 
    {
        return extinguisherId;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    public void setAlarmType(String alarmType) 
    {
        this.alarmType = alarmType;
    }

    public String getAlarmType() 
    {
        return alarmType;
    }

    public void setAlarmLevel(String alarmLevel) 
    {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmLevel() 
    {
        return alarmLevel;
    }

    public void setAlarmContent(String alarmContent) 
    {
        this.alarmContent = alarmContent;
    }

    public String getAlarmContent() 
    {
        return alarmContent;
    }

    public void setAlarmValue(String alarmValue) 
    {
        this.alarmValue = alarmValue;
    }

    public String getAlarmValue() 
    {
        return alarmValue;
    }

    public void setAlarmTime(Date alarmTime) 
    {
        this.alarmTime = alarmTime;
    }

    public Date getAlarmTime() 
    {
        return alarmTime;
    }

    public void setHandleStatus(String handleStatus) 
    {
        this.handleStatus = handleStatus;
    }

    public String getHandleStatus() 
    {
        return handleStatus;
    }

    public void setHandlePerson(String handlePerson) 
    {
        this.handlePerson = handlePerson;
    }

    public String getHandlePerson() 
    {
        return handlePerson;
    }

    public void setHandleTime(Date handleTime) 
    {
        this.handleTime = handleTime;
    }

    public Date getHandleTime() 
    {
        return handleTime;
    }

    public void setHandleResult(String handleResult) 
    {
        this.handleResult = handleResult;
    }

    public String getHandleResult() 
    {
        return handleResult;
    }

    public void setIsNotified(String isNotified) 
    {
        this.isNotified = isNotified;
    }

    public String getIsNotified() 
    {
        return isNotified;
    }

    public void setNotifyTime(Date notifyTime) 
    {
        this.notifyTime = notifyTime;
    }

    public Date getNotifyTime() 
    {
        return notifyTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("alarmId", getAlarmId())
            .append("alarmNo", getAlarmNo())
            .append("sensorId", getSensorId())
            .append("extinguisherId", getExtinguisherId())
            .append("deptId", getDeptId())
            .append("alarmType", getAlarmType())
            .append("alarmLevel", getAlarmLevel())
            .append("alarmContent", getAlarmContent())
            .append("alarmValue", getAlarmValue())
            .append("alarmTime", getAlarmTime())
            .append("handleStatus", getHandleStatus())
            .append("handlePerson", getHandlePerson())
            .append("handleTime", getHandleTime())
            .append("handleResult", getHandleResult())
            .append("isNotified", getIsNotified())
            .append("notifyTime", getNotifyTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
