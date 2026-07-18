package com.ruoyi.manage.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.FeDeviceWarningTask;
import com.ruoyi.manage.domain.FeDeviceWarningTaskAttachment;
import com.ruoyi.manage.domain.FeDeviceWarningTaskRecord;
import com.ruoyi.manage.service.IFeDeviceWarningTaskService;

@RestController
@RequestMapping("/manage/device-warning-task")
public class FeDeviceWarningTaskController extends BaseController
{
    @Autowired
    private IFeDeviceWarningTaskService taskService;

    @PreAuthorize("@ss.hasPermi('manage:deviceWarningTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeDeviceWarningTask task)
    {
        startPage();
        return getDataTable(taskService.selectTaskList(task));
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceWarningTask:query')")
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId)
    {
        return success(taskService.selectTaskByTaskId(taskId));
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceWarningTask:dispatch')")
    @Log(title = "设备预警任务分发", businessType = BusinessType.UPDATE)
    @PostMapping("/dispatch/{warningId}")
    public AjaxResult dispatch(@PathVariable Long warningId)
    {
        return success(taskService.dispatchWarning(warningId, SecurityUtils.getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceWarningTask:reassign')")
    @GetMapping("/{taskId}/candidates")
    public AjaxResult candidates(@PathVariable Long taskId)
    {
        List<SysUser> users = taskService.selectTaskAssigneeCandidates(taskId);
        return success(users);
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceWarningTask:reassign')")
    @Log(title = "设备预警任务改派", businessType = BusinessType.UPDATE)
    @PutMapping("/{taskId}/assignee")
    public AjaxResult reassign(@PathVariable Long taskId, @RequestBody FeDeviceWarningTask task)
    {
        return toAjax(taskService.reassignTask(taskId, task.getAssigneeUserId(), task.getReassignReason(),
            SecurityUtils.getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceWarningTask:start')")
    @Log(title = "开始处理设备预警", businessType = BusinessType.UPDATE)
    @PutMapping("/{taskId}/start")
    public AjaxResult start(@PathVariable Long taskId)
    {
        return toAjax(taskService.startTask(taskId, SecurityUtils.getUserId(), SecurityUtils.getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceWarningTask:treat')")
    @Log(title = "记录设备预警处置", businessType = BusinessType.INSERT)
    @PostMapping("/{taskId}/treatment")
    public AjaxResult treatment(@PathVariable Long taskId, @RequestBody FeDeviceWarningTaskRecord record)
    {
        return success(taskService.submitTreatment(taskId, record, SecurityUtils.getUserId(),
            SecurityUtils.getUsername(), SecurityUtils.getLoginUser().getUser().getNickName()));
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceWarningTask:treat')")
    @Log(title = "记录设备预警处置及附件", businessType = BusinessType.INSERT)
    @PostMapping(value = "/{taskId}/treatment-with-attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult treatmentWithAttachments(@PathVariable Long taskId, FeDeviceWarningTaskRecord record,
        @RequestParam(value = "files", required = false) MultipartFile[] files)
    {
        return success(taskService.submitTreatmentWithAttachments(taskId, record, files, SecurityUtils.getUserId(),
            SecurityUtils.getUsername(), SecurityUtils.getLoginUser().getUser().getNickName()));
    }

    @PreAuthorize("@ss.hasPermi('manage:deviceWarningTask:query')")
    @GetMapping("/attachment/{attachmentId}")
    public void downloadAttachment(@PathVariable Long attachmentId, HttpServletResponse response) throws IOException
    {
        FeDeviceWarningTaskAttachment attachment = taskService.selectTreatmentAttachment(attachmentId);
        File file = taskService.resolveTreatmentAttachment(attachment);
        response.setContentType(attachment.getFileType());
        response.setContentLengthLong(file.length());
        FileUtils.setAttachmentResponseHeader(response, attachment.getOriginalName());
        FileUtils.writeBytes(file.getAbsolutePath(), response.getOutputStream());
    }
}
