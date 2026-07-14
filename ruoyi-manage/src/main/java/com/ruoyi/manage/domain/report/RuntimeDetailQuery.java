package com.ruoyi.manage.domain.report;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备运行明细查询条件。
 */
public class RuntimeDetailQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String detailType;
    private String periodType;
    private String periodDate;
    private Long deptId;
    private Long firePointId;
    private Long deviceId;
    private Boolean allDevices;
    private String deviceKeyword;
    private Date beginTime;
    private Date endTime;
    private String periodLabel;
    private String scopeName;
    private Integer offset;
    private Integer limit;

    public String getDetailType() { return detailType; }
    public void setDetailType(String detailType) { this.detailType = detailType; }
    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }
    public String getPeriodDate() { return periodDate; }
    public void setPeriodDate(String periodDate) { this.periodDate = periodDate; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public Boolean getAllDevices() { return allDevices; }
    public void setAllDevices(Boolean allDevices) { this.allDevices = allDevices; }
    public String getDeviceKeyword() { return deviceKeyword; }
    public void setDeviceKeyword(String deviceKeyword) { this.deviceKeyword = deviceKeyword; }
    public Date getBeginTime() { return beginTime; }
    public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getPeriodLabel() { return periodLabel; }
    public void setPeriodLabel(String periodLabel) { this.periodLabel = periodLabel; }
    public String getScopeName() { return scopeName; }
    public void setScopeName(String scopeName) { this.scopeName = scopeName; }
    public Integer getOffset() { return offset; }
    public void setOffset(Integer offset) { this.offset = offset; }
    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
}
