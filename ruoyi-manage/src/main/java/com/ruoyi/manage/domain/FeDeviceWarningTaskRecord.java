package com.ruoyi.manage.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备预警任务处置记录对象 fe_device_warning_task_record
 */
public class FeDeviceWarningTaskRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long recordId;
    private Long taskId;
    private Long warningId;
    private String actionType;
    private String actionChannel;
    private String actionDescription;
    private String nextTaskStatus;
    private Long operatorUserId;
    private String operatorUserName;
    private String operatorNickName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actionTime;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getWarningId() { return warningId; }
    public void setWarningId(Long warningId) { this.warningId = warningId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getActionChannel() { return actionChannel; }
    public void setActionChannel(String actionChannel) { this.actionChannel = actionChannel; }
    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }
    public String getNextTaskStatus() { return nextTaskStatus; }
    public void setNextTaskStatus(String nextTaskStatus) { this.nextTaskStatus = nextTaskStatus; }
    public Long getOperatorUserId() { return operatorUserId; }
    public void setOperatorUserId(Long operatorUserId) { this.operatorUserId = operatorUserId; }
    public String getOperatorUserName() { return operatorUserName; }
    public void setOperatorUserName(String operatorUserName) { this.operatorUserName = operatorUserName; }
    public String getOperatorNickName() { return operatorNickName; }
    public void setOperatorNickName(String operatorNickName) { this.operatorNickName = operatorNickName; }
    public Date getActionTime() { return actionTime; }
    public void setActionTime(Date actionTime) { this.actionTime = actionTime; }
}
