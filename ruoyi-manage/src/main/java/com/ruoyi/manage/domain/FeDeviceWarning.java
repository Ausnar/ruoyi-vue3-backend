package com.ruoyi.manage.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备预警入库对象 fe_device_warning
 */
public class FeDeviceWarning extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long warningId;
    private String warningType;
    private String objectType;
    private Long objectId;
    private Long deptId;
    private String deptName;
    private String deptProvince;
    private String deptCity;
    private String deptArea;
    private Long sourceDeptId;
    private String sourceDeptName;
    private Long firePointId;
    private String firePointName;
    private Long gatewayId;
    private String gatewayImei;
    private Long sensorId;
    private String sensorCode;
    private Long extinguisherId;
    private String extinguisherLabelCode;
    private String extinguisherProductName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date triggerTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastTriggerTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date windowStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date windowEndTime;
    private Integer sampleCount;
    private String thresholdSnapshot;
    private String evidenceSummary;
    private String warningStatus;
    private String confirmBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;
    private String handleBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;
    private String resolveBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date resolveTime;
    private String falseAlarmReason;
    private String delFlag;
    private String keyword;

    public Long getWarningId() { return warningId; }
    public void setWarningId(Long warningId) { this.warningId = warningId; }
    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }
    public String getObjectType() { return objectType; }
    public void setObjectType(String objectType) { this.objectType = objectType; }
    public Long getObjectId() { return objectId; }
    public void setObjectId(Long objectId) { this.objectId = objectId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptProvince() { return deptProvince; }
    public void setDeptProvince(String deptProvince) { this.deptProvince = deptProvince; }
    public String getDeptCity() { return deptCity; }
    public void setDeptCity(String deptCity) { this.deptCity = deptCity; }
    public String getDeptArea() { return deptArea; }
    public void setDeptArea(String deptArea) { this.deptArea = deptArea; }
    public Long getSourceDeptId() { return sourceDeptId; }
    public void setSourceDeptId(Long sourceDeptId) { this.sourceDeptId = sourceDeptId; }
    public String getSourceDeptName() { return sourceDeptName; }
    public void setSourceDeptName(String sourceDeptName) { this.sourceDeptName = sourceDeptName; }
    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public Long getGatewayId() { return gatewayId; }
    public void setGatewayId(Long gatewayId) { this.gatewayId = gatewayId; }
    public String getGatewayImei() { return gatewayImei; }
    public void setGatewayImei(String gatewayImei) { this.gatewayImei = gatewayImei; }
    public Long getSensorId() { return sensorId; }
    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
    public String getSensorCode() { return sensorCode; }
    public void setSensorCode(String sensorCode) { this.sensorCode = sensorCode; }
    public Long getExtinguisherId() { return extinguisherId; }
    public void setExtinguisherId(Long extinguisherId) { this.extinguisherId = extinguisherId; }
    public String getExtinguisherLabelCode() { return extinguisherLabelCode; }
    public void setExtinguisherLabelCode(String extinguisherLabelCode) { this.extinguisherLabelCode = extinguisherLabelCode; }
    public String getExtinguisherProductName() { return extinguisherProductName; }
    public void setExtinguisherProductName(String extinguisherProductName) { this.extinguisherProductName = extinguisherProductName; }
    public Date getTriggerTime() { return triggerTime; }
    public void setTriggerTime(Date triggerTime) { this.triggerTime = triggerTime; }
    public Date getLastTriggerTime() { return lastTriggerTime; }
    public void setLastTriggerTime(Date lastTriggerTime) { this.lastTriggerTime = lastTriggerTime; }
    public Date getWindowStartTime() { return windowStartTime; }
    public void setWindowStartTime(Date windowStartTime) { this.windowStartTime = windowStartTime; }
    public Date getWindowEndTime() { return windowEndTime; }
    public void setWindowEndTime(Date windowEndTime) { this.windowEndTime = windowEndTime; }
    public Integer getSampleCount() { return sampleCount; }
    public void setSampleCount(Integer sampleCount) { this.sampleCount = sampleCount; }
    public String getThresholdSnapshot() { return thresholdSnapshot; }
    public void setThresholdSnapshot(String thresholdSnapshot) { this.thresholdSnapshot = thresholdSnapshot; }
    public String getEvidenceSummary() { return evidenceSummary; }
    public void setEvidenceSummary(String evidenceSummary) { this.evidenceSummary = evidenceSummary; }
    public String getWarningStatus() { return warningStatus; }
    public void setWarningStatus(String warningStatus) { this.warningStatus = warningStatus; }
    public String getConfirmBy() { return confirmBy; }
    public void setConfirmBy(String confirmBy) { this.confirmBy = confirmBy; }
    public Date getConfirmTime() { return confirmTime; }
    public void setConfirmTime(Date confirmTime) { this.confirmTime = confirmTime; }
    public String getHandleBy() { return handleBy; }
    public void setHandleBy(String handleBy) { this.handleBy = handleBy; }
    public Date getHandleTime() { return handleTime; }
    public void setHandleTime(Date handleTime) { this.handleTime = handleTime; }
    public String getResolveBy() { return resolveBy; }
    public void setResolveBy(String resolveBy) { this.resolveBy = resolveBy; }
    public Date getResolveTime() { return resolveTime; }
    public void setResolveTime(Date resolveTime) { this.resolveTime = resolveTime; }
    public String getFalseAlarmReason() { return falseAlarmReason; }
    public void setFalseAlarmReason(String falseAlarmReason) { this.falseAlarmReason = falseAlarmReason; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("warningId", getWarningId())
            .append("warningType", getWarningType())
            .append("objectType", getObjectType())
            .append("objectId", getObjectId())
            .append("deptId", getDeptId())
            .append("deptName", getDeptName())
            .append("deptProvince", getDeptProvince())
            .append("deptCity", getDeptCity())
            .append("deptArea", getDeptArea())
            .append("sourceDeptId", getSourceDeptId())
            .append("sourceDeptName", getSourceDeptName())
            .append("firePointId", getFirePointId())
            .append("firePointName", getFirePointName())
            .append("gatewayId", getGatewayId())
            .append("gatewayImei", getGatewayImei())
            .append("sensorId", getSensorId())
            .append("sensorCode", getSensorCode())
            .append("extinguisherId", getExtinguisherId())
            .append("extinguisherLabelCode", getExtinguisherLabelCode())
            .append("extinguisherProductName", getExtinguisherProductName())
            .append("triggerTime", getTriggerTime())
            .append("lastTriggerTime", getLastTriggerTime())
            .append("windowStartTime", getWindowStartTime())
            .append("windowEndTime", getWindowEndTime())
            .append("sampleCount", getSampleCount())
            .append("thresholdSnapshot", getThresholdSnapshot())
            .append("evidenceSummary", getEvidenceSummary())
            .append("warningStatus", getWarningStatus())
            .append("confirmBy", getConfirmBy())
            .append("confirmTime", getConfirmTime())
            .append("handleBy", getHandleBy())
            .append("handleTime", getHandleTime())
            .append("resolveBy", getResolveBy())
            .append("resolveTime", getResolveTime())
            .append("falseAlarmReason", getFalseAlarmReason())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("keyword", getKeyword())
            .toString();
    }
}
