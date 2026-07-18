package com.ruoyi.manage.service;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.manage.domain.FeDeviceWarningTask;
import com.ruoyi.manage.domain.FeDeviceWarningTaskAttachment;
import com.ruoyi.manage.domain.FeDeviceWarningTaskRecord;

public interface IFeDeviceWarningTaskService
{
    String HANDLER_ROLE_KEY = "device_warning_handler";
    String HANDLER_ROLE_NAME = "设备预警处理员";
    String COORDINATOR_ROLE_KEY = "device_warning_coordinator";
    String COORDINATOR_ROLE_NAME = "设备预警协调员";
    String TASK_MANUAL_PENDING = "manual_pending";
    String TASK_ASSIGNED = "assigned";
    String TASK_PROCESSING = "processing";
    String TASK_WAITING_EXTERNAL = "waiting_external";
    String TASK_VERIFYING = "verifying";
    String TASK_RECOVERED = "recovered";
    String TASK_COMPLETED = "completed";
    String TASK_CANCELLED = "cancelled";
    String DISPATCH_AUTO = "auto";
    String DISPATCH_MANUAL = "manual";

    List<FeDeviceWarningTask> selectTaskList(FeDeviceWarningTask task);

    FeDeviceWarningTask selectTaskByTaskId(Long taskId);

    FeDeviceWarningTask dispatchWarning(Long warningId, String operator);

    int dispatchActiveWarnings(Long sourceDeptId, String operator);

    int closeRecoveredTasks(Long sourceDeptId, String operator);

    int reassignTask(Long taskId, Long assigneeUserId, String reason, String operator);

    int startTask(Long taskId, Long userId, String operator);

    Long submitTreatment(Long taskId, FeDeviceWarningTaskRecord record, Long userId, String operator,
        String operatorNickName);

    Long submitTreatmentWithAttachments(Long taskId, FeDeviceWarningTaskRecord record, MultipartFile[] files,
        Long userId, String operator, String operatorNickName);

    FeDeviceWarningTaskAttachment selectTreatmentAttachment(Long attachmentId);

    File resolveTreatmentAttachment(FeDeviceWarningTaskAttachment attachment);

    List<SysUser> selectTaskAssigneeCandidates(Long taskId);
}
