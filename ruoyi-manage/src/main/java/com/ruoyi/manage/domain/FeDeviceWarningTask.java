package com.ruoyi.manage.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备预警处理任务对象 fe_device_warning_task
 */
public class FeDeviceWarningTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long taskId;
    private Long warningId;
    private Long deptId;
    private Long sourceDeptId;
    private Long firePointId;
    private Long handlerRoleId;
    private String handlerRoleKey;
    private String handlerRoleName;
    private Long assigneeUserId;
    private String assigneeUserName;
    private String assigneeNickName;
    private String taskStatus;
    private String dispatchSource;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dispatchTime;
    private String dispatchBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstViewTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    private Integer reassignCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastReassignTime;
    private String lastReassignBy;
    private String reassignReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private String endReason;
    private String delFlag;

    // 任务列表只读展示字段
    private String deptName;
    private String sourceDeptName;
    private String firePointName;
    private String warningType;
    private String alarmState;
    private String warningStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date triggerTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastTriggerTime;
    private String thresholdSnapshot;
    private String evidenceSummary;
    private String sensorCode;
    private String gatewayImei;
    private String extinguisherLabelCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recoveryTime;
    private String recoverySource;
    private String recoveryEvidence;
    private List<FeDeviceWarningTaskRecord> treatmentRecords;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getWarningId() { return warningId; }
    public void setWarningId(Long warningId) { this.warningId = warningId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getSourceDeptId() { return sourceDeptId; }
    public void setSourceDeptId(Long sourceDeptId) { this.sourceDeptId = sourceDeptId; }
    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public Long getHandlerRoleId() { return handlerRoleId; }
    public void setHandlerRoleId(Long handlerRoleId) { this.handlerRoleId = handlerRoleId; }
    public String getHandlerRoleKey() { return handlerRoleKey; }
    public void setHandlerRoleKey(String handlerRoleKey) { this.handlerRoleKey = handlerRoleKey; }
    public String getHandlerRoleName() { return handlerRoleName; }
    public void setHandlerRoleName(String handlerRoleName) { this.handlerRoleName = handlerRoleName; }
    public Long getAssigneeUserId() { return assigneeUserId; }
    public void setAssigneeUserId(Long assigneeUserId) { this.assigneeUserId = assigneeUserId; }
    public String getAssigneeUserName() { return assigneeUserName; }
    public void setAssigneeUserName(String assigneeUserName) { this.assigneeUserName = assigneeUserName; }
    public String getAssigneeNickName() { return assigneeNickName; }
    public void setAssigneeNickName(String assigneeNickName) { this.assigneeNickName = assigneeNickName; }
    public String getTaskStatus() { return taskStatus; }
    public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }
    public String getDispatchSource() { return dispatchSource; }
    public void setDispatchSource(String dispatchSource) { this.dispatchSource = dispatchSource; }
    public Date getDispatchTime() { return dispatchTime; }
    public void setDispatchTime(Date dispatchTime) { this.dispatchTime = dispatchTime; }
    public String getDispatchBy() { return dispatchBy; }
    public void setDispatchBy(String dispatchBy) { this.dispatchBy = dispatchBy; }
    public Date getFirstViewTime() { return firstViewTime; }
    public void setFirstViewTime(Date firstViewTime) { this.firstViewTime = firstViewTime; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Integer getReassignCount() { return reassignCount; }
    public void setReassignCount(Integer reassignCount) { this.reassignCount = reassignCount; }
    public Date getLastReassignTime() { return lastReassignTime; }
    public void setLastReassignTime(Date lastReassignTime) { this.lastReassignTime = lastReassignTime; }
    public String getLastReassignBy() { return lastReassignBy; }
    public void setLastReassignBy(String lastReassignBy) { this.lastReassignBy = lastReassignBy; }
    public String getReassignReason() { return reassignReason; }
    public void setReassignReason(String reassignReason) { this.reassignReason = reassignReason; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getEndReason() { return endReason; }
    public void setEndReason(String endReason) { this.endReason = endReason; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getSourceDeptName() { return sourceDeptName; }
    public void setSourceDeptName(String sourceDeptName) { this.sourceDeptName = sourceDeptName; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }
    public String getAlarmState() { return alarmState; }
    public void setAlarmState(String alarmState) { this.alarmState = alarmState; }
    public String getWarningStatus() { return warningStatus; }
    public void setWarningStatus(String warningStatus) { this.warningStatus = warningStatus; }
    public Date getTriggerTime() { return triggerTime; }
    public void setTriggerTime(Date triggerTime) { this.triggerTime = triggerTime; }
    public Date getLastTriggerTime() { return lastTriggerTime; }
    public void setLastTriggerTime(Date lastTriggerTime) { this.lastTriggerTime = lastTriggerTime; }
    public String getThresholdSnapshot() { return thresholdSnapshot; }
    public void setThresholdSnapshot(String thresholdSnapshot) { this.thresholdSnapshot = thresholdSnapshot; }
    public String getEvidenceSummary() { return evidenceSummary; }
    public void setEvidenceSummary(String evidenceSummary) { this.evidenceSummary = evidenceSummary; }
    public String getSensorCode() { return sensorCode; }
    public void setSensorCode(String sensorCode) { this.sensorCode = sensorCode; }
    public String getGatewayImei() { return gatewayImei; }
    public void setGatewayImei(String gatewayImei) { this.gatewayImei = gatewayImei; }
    public String getExtinguisherLabelCode() { return extinguisherLabelCode; }
    public void setExtinguisherLabelCode(String extinguisherLabelCode) { this.extinguisherLabelCode = extinguisherLabelCode; }
    public Date getRecoveryTime() { return recoveryTime; }
    public void setRecoveryTime(Date recoveryTime) { this.recoveryTime = recoveryTime; }
    public String getRecoverySource() { return recoverySource; }
    public void setRecoverySource(String recoverySource) { this.recoverySource = recoverySource; }
    public String getRecoveryEvidence() { return recoveryEvidence; }
    public void setRecoveryEvidence(String recoveryEvidence) { this.recoveryEvidence = recoveryEvidence; }
    public List<FeDeviceWarningTaskRecord> getTreatmentRecords() { return treatmentRecords; }
    public void setTreatmentRecords(List<FeDeviceWarningTaskRecord> treatmentRecords) { this.treatmentRecords = treatmentRecords; }
}
