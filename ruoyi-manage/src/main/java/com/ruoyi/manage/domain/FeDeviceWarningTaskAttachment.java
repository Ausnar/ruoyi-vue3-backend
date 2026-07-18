package com.ruoyi.manage.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备预警任务处置附件对象 fe_device_warning_task_attachment
 */
public class FeDeviceWarningTaskAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long attachmentId;
    private Long taskId;
    private Long warningId;
    private Long recordId;
    private String originalName;
    @JsonIgnore
    private String storedPath;
    private String fileType;
    private Long fileSize;
    private Long uploaderUserId;
    private String uploaderUserName;
    private String uploaderNickName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;
    private String delFlag;

    public Long getAttachmentId() { return attachmentId; }
    public void setAttachmentId(Long attachmentId) { this.attachmentId = attachmentId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getWarningId() { return warningId; }
    public void setWarningId(Long warningId) { this.warningId = warningId; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getStoredPath() { return storedPath; }
    public void setStoredPath(String storedPath) { this.storedPath = storedPath; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public Long getUploaderUserId() { return uploaderUserId; }
    public void setUploaderUserId(Long uploaderUserId) { this.uploaderUserId = uploaderUserId; }
    public String getUploaderUserName() { return uploaderUserName; }
    public void setUploaderUserName(String uploaderUserName) { this.uploaderUserName = uploaderUserName; }
    public String getUploaderNickName() { return uploaderNickName; }
    public void setUploaderNickName(String uploaderNickName) { this.uploaderNickName = uploaderNickName; }
    public Date getUploadTime() { return uploadTime; }
    public void setUploadTime(Date uploadTime) { this.uploadTime = uploadTime; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
