package com.ruoyi.manage.service.impl;

import java.util.Collections;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeDeviceWarning;
import com.ruoyi.manage.domain.FeDeviceWarningTask;
import com.ruoyi.manage.domain.FeDeviceWarningTaskRecord;
import com.ruoyi.manage.mapper.FeDeviceWarningMapper;
import com.ruoyi.manage.mapper.FeDeviceWarningTaskMapper;
import com.ruoyi.manage.service.IFeDeviceWarningService;
import com.ruoyi.manage.service.IFeDeviceWarningTaskService;
import com.ruoyi.system.service.ISysDeptService;

@Service
public class FeDeviceWarningTaskServiceImpl implements IFeDeviceWarningTaskService
{
    private static final String REASSIGN_PERMISSION = "manage:deviceWarningTask:reassign";
    private static final String DISPATCH_PERMISSION = "manage:deviceWarningTask:dispatch";
    private static final String DISPATCH_SYSTEM = "quartz:warningDispatch";
    private static final String RECOVERY_END_REASON = "设备最新数据已恢复正常";
    private static final Set<String> TREATMENT_TASK_STATUSES = new HashSet<String>(Arrays.asList(
        TASK_PROCESSING, TASK_WAITING_EXTERNAL, TASK_VERIFYING));
    private static final Set<String> TREATMENT_NEXT_STATUSES = new HashSet<String>(Arrays.asList(
        TASK_WAITING_EXTERNAL, TASK_VERIFYING));
    private static final Set<String> TREATMENT_CHANNELS = new HashSet<String>(Arrays.asList(
        "onsite", "external_platform", "web_record", "other"));
    private static final Set<String> TREATMENT_ACTION_TYPES = new HashSet<String>(Arrays.asList(
        "onsite_inspection", "fire_disposal", "battery_replacement", "sensor_replacement",
        "pressure_inspection", "extinguisher_maintenance", "extinguisher_replacement",
        "external_binding", "extinguisher_replenishment", "expected_count_correction",
        "environment_adjustment", "device_relocation", "other"));

    @Autowired
    private FeDeviceWarningTaskMapper taskMapper;

    @Autowired
    private FeDeviceWarningMapper warningMapper;

    @Autowired
    private IFeDeviceWarningService warningService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    @DataScope(deptAlias = "t")
    public List<FeDeviceWarningTask> selectTaskList(FeDeviceWarningTask task)
    {
        if (!isTaskManager())
        {
            task.setAssigneeUserId(SecurityUtils.getUserId());
        }
        return taskMapper.selectFeDeviceWarningTaskList(task);
    }

    @Override
    public FeDeviceWarningTask selectTaskByTaskId(Long taskId)
    {
        FeDeviceWarningTask task = requireTask(taskId);
        checkTaskAccess(task, false);
        Long userId = SecurityUtils.getUserId();
        if (userId.equals(task.getAssigneeUserId()) && task.getFirstViewTime() == null)
        {
            Date now = DateUtils.getNowDate();
            taskMapper.markTaskViewed(taskId, now, SecurityUtils.getUsername(), now);
            task.setFirstViewTime(now);
        }
        task.setTreatmentRecords(taskMapper.selectTreatmentRecords(taskId));
        return task;
    }

    @Override
    @Transactional
    public FeDeviceWarningTask dispatchWarning(Long warningId, String operator)
    {
        requireTaskManager();
        FeDeviceWarning warning = warningMapper.selectFeDeviceWarningByWarningId(warningId);
        if (warning == null)
        {
            throw new ServiceException("设备预警不存在");
        }
        checkDeptDataScope(warning.getDeptId());
        return dispatchWarningInternal(warning, operator);
    }

    @Override
    @Transactional
    public int dispatchActiveWarnings(Long sourceDeptId, String operator)
    {
        int count = 0;
        for (FeDeviceWarning warning : taskMapper.selectActiveWarningsWithoutTask(sourceDeptId))
        {
            if (dispatchWarningInternal(warning, operator) != null)
            {
                count++;
            }
        }
        return count;
    }

    @Override
    @Transactional
    public int closeRecoveredTasks(Long sourceDeptId, String operator)
    {
        Date now = DateUtils.getNowDate();
        return taskMapper.closeRecoveredTasks(sourceDeptId, now, RECOVERY_END_REASON,
            StringUtils.defaultIfBlank(operator, DISPATCH_SYSTEM), now);
    }

    @Override
    @Transactional
    public int reassignTask(Long taskId, Long assigneeUserId, String reason, String operator)
    {
        requireTaskManager();
        FeDeviceWarningTask task = requireTask(taskId);
        checkDeptDataScope(task.getDeptId());
        if (!TASK_MANUAL_PENDING.equals(task.getTaskStatus()) && !TASK_ASSIGNED.equals(task.getTaskStatus()))
        {
            throw new ServiceException("只有待协调或待处理任务可以改派");
        }

        SysUser assignee = taskMapper.selectEligibleAssignee(task.getSourceDeptId(), task.getDeptId(),
            HANDLER_ROLE_KEY, COORDINATOR_ROLE_KEY, assigneeUserId);
        if (assignee == null)
        {
            throw new ServiceException("所选人员不在预警来源责任树内，或未配置处理员/协调员角色");
        }

        SysRole role = taskMapper.selectAssigneeRole(assigneeUserId, HANDLER_ROLE_KEY, COORDINATOR_ROLE_KEY);
        Date now = DateUtils.getNowDate();
        task.setHandlerRoleId(role == null ? null : role.getRoleId());
        task.setHandlerRoleKey(role == null ? HANDLER_ROLE_KEY : role.getRoleKey());
        task.setHandlerRoleName(role == null ? HANDLER_ROLE_NAME : role.getRoleName());
        task.setAssigneeUserId(assignee.getUserId());
        task.setAssigneeUserName(assignee.getUserName());
        task.setAssigneeNickName(assignee.getNickName());
        task.setTaskStatus(TASK_ASSIGNED);
        task.setDispatchSource(DISPATCH_MANUAL);
        task.setDispatchTime(now);
        task.setDispatchBy(operator);
        task.setReassignCount((task.getReassignCount() == null ? 0 : task.getReassignCount()) + 1);
        task.setLastReassignTime(now);
        task.setLastReassignBy(operator);
        task.setReassignReason(StringUtils.trimToNull(reason));
        task.setUpdateBy(operator);
        task.setUpdateTime(now);
        int rows = taskMapper.updateTaskAssignment(task);
        if (rows == 0)
        {
            throw new ServiceException("任务状态已经变化，请刷新后重试");
        }
        updateWarningStatus(task.getWarningId(), IFeDeviceWarningService.STATUS_DISPATCHED, operator);
        return rows;
    }

    @Override
    @Transactional
    public int startTask(Long taskId, Long userId, String operator)
    {
        FeDeviceWarningTask current = requireTask(taskId);
        if (!userId.equals(current.getAssigneeUserId()))
        {
            throw new ServiceException("该任务未派发给当前用户");
        }
        if (!TASK_ASSIGNED.equals(current.getTaskStatus()))
        {
            throw new ServiceException("只有待处理任务可以开始处理");
        }
        if (!IFeDeviceWarningService.ALARM_STATE_ACTIVE.equals(current.getAlarmState()))
        {
            throw new ServiceException("设备状态已恢复，无需开始处理");
        }

        Date now = DateUtils.getNowDate();
        FeDeviceWarningTask update = new FeDeviceWarningTask();
        update.setTaskId(taskId);
        update.setTaskStatus(TASK_PROCESSING);
        update.setStartTime(now);
        update.setUpdateBy(operator);
        update.setUpdateTime(now);
        int rows = taskMapper.updateTaskStatus(update);
        if (rows == 0)
        {
            throw new ServiceException("任务状态已经变化，请刷新后重试");
        }
        updateWarningStatus(current.getWarningId(), IFeDeviceWarningService.STATUS_PROCESSING, operator);
        return rows;
    }

    @Override
    @Transactional
    public int submitTreatment(Long taskId, FeDeviceWarningTaskRecord record, Long userId, String operator,
        String operatorNickName)
    {
        FeDeviceWarningTask current = requireTask(taskId);
        if (!userId.equals(current.getAssigneeUserId()))
        {
            throw new ServiceException("只有当前处理人可以记录处置");
        }
        if (!TREATMENT_TASK_STATUSES.contains(current.getTaskStatus()))
        {
            throw new ServiceException("任务尚未开始处理或已经结束");
        }
        if (!IFeDeviceWarningService.ALARM_STATE_ACTIVE.equals(current.getAlarmState()))
        {
            throw new ServiceException("设备状态已恢复，无需继续记录处置");
        }
        validateTreatment(record);

        Date now = DateUtils.getNowDate();
        int rows = taskMapper.updateTaskTreatmentStatus(taskId, record.getNextTaskStatus(), operator, now);
        if (rows == 0)
        {
            throw new ServiceException("任务状态已经变化，请刷新后重试");
        }

        record.setTaskId(taskId);
        record.setWarningId(current.getWarningId());
        record.setActionDescription(StringUtils.trim(record.getActionDescription()));
        record.setOperatorUserId(userId);
        record.setOperatorUserName(operator);
        record.setOperatorNickName(StringUtils.trimToNull(operatorNickName));
        record.setActionTime(now);
        record.setCreateBy(operator);
        record.setCreateTime(now);
        return taskMapper.insertFeDeviceWarningTaskRecord(record);
    }

    @Override
    public List<SysUser> selectTaskAssigneeCandidates(Long taskId)
    {
        requireTaskManager();
        FeDeviceWarningTask task = requireTask(taskId);
        checkDeptDataScope(task.getDeptId());
        if (!TASK_MANUAL_PENDING.equals(task.getTaskStatus()) && !TASK_ASSIGNED.equals(task.getTaskStatus()))
        {
            return Collections.emptyList();
        }
        return taskMapper.selectManualDispatchCandidates(task.getSourceDeptId(), task.getDeptId(),
            HANDLER_ROLE_KEY, COORDINATOR_ROLE_KEY);
    }

    private FeDeviceWarningTask dispatchWarningInternal(FeDeviceWarning warning, String operator)
    {
        if (!IFeDeviceWarningService.ALARM_STATE_ACTIVE.equals(warning.getAlarmState()))
        {
            return null;
        }
        FeDeviceWarningTask existed = taskMapper.selectFeDeviceWarningTaskByWarningId(warning.getWarningId());
        if (existed != null)
        {
            return existed;
        }

        SysRole role = taskMapper.selectHandlerRoleByRoleKey(HANDLER_ROLE_KEY);
        List<SysUser> candidates = warning.getDeptId() == null || role == null
            ? Collections.emptyList()
            : taskMapper.selectDispatchCandidates(warning.getDeptId(), HANDLER_ROLE_KEY);
        SysUser assignee = candidates.isEmpty() ? null : candidates.get(0);
        Date now = DateUtils.getNowDate();
        String actualOperator = StringUtils.defaultIfBlank(operator, DISPATCH_SYSTEM);

        FeDeviceWarningTask task = new FeDeviceWarningTask();
        task.setWarningId(warning.getWarningId());
        task.setDeptId(warning.getDeptId());
        task.setSourceDeptId(warning.getSourceDeptId());
        task.setFirePointId(warning.getFirePointId());
        task.setHandlerRoleId(role == null ? null : role.getRoleId());
        task.setHandlerRoleKey(HANDLER_ROLE_KEY);
        task.setHandlerRoleName(role == null ? HANDLER_ROLE_NAME : role.getRoleName());
        task.setAssigneeUserId(assignee == null ? null : assignee.getUserId());
        task.setAssigneeUserName(assignee == null ? null : assignee.getUserName());
        task.setAssigneeNickName(assignee == null ? null : assignee.getNickName());
        task.setTaskStatus(assignee == null ? TASK_MANUAL_PENDING : TASK_ASSIGNED);
        task.setDispatchSource(assignee == null ? null : DISPATCH_AUTO);
        task.setDispatchTime(assignee == null ? null : now);
        task.setDispatchBy(assignee == null ? null : actualOperator);
        task.setReassignCount(0);
        task.setCreateBy(actualOperator);
        task.setCreateTime(now);
        task.setUpdateBy(actualOperator);
        task.setUpdateTime(now);
        try
        {
            taskMapper.insertFeDeviceWarningTask(task);
        }
        catch (DuplicateKeyException ex)
        {
            return taskMapper.selectFeDeviceWarningTaskByWarningId(warning.getWarningId());
        }

        updateWarningStatus(warning.getWarningId(), assignee == null
            ? IFeDeviceWarningService.STATUS_PENDING : IFeDeviceWarningService.STATUS_DISPATCHED, actualOperator);
        return task;
    }

    private void updateWarningStatus(Long warningId, String status, String operator)
    {
        FeDeviceWarning warning = new FeDeviceWarning();
        warning.setWarningId(warningId);
        warning.setWarningStatus(status);
        warning.setUpdateBy(operator);
        warningService.updateFeDeviceWarning(warning);
    }

    private void validateTreatment(FeDeviceWarningTaskRecord record)
    {
        if (record == null)
        {
            throw new ServiceException("处置记录不能为空");
        }
        if (!TREATMENT_ACTION_TYPES.contains(record.getActionType()))
        {
            throw new ServiceException("请选择有效的处置方式");
        }
        if (!TREATMENT_CHANNELS.contains(record.getActionChannel()))
        {
            throw new ServiceException("请选择有效的处置渠道");
        }
        if (!TREATMENT_NEXT_STATUSES.contains(record.getNextTaskStatus()))
        {
            throw new ServiceException("请选择有效的下一步状态");
        }
        String description = StringUtils.trimToNull(record.getActionDescription());
        if (description == null)
        {
            throw new ServiceException("请填写处置说明");
        }
        if (description.length() > 1000)
        {
            throw new ServiceException("处置说明不能超过1000个字符");
        }
    }

    private FeDeviceWarningTask requireTask(Long taskId)
    {
        FeDeviceWarningTask task = taskMapper.selectFeDeviceWarningTaskByTaskId(taskId);
        if (task == null)
        {
            throw new ServiceException("预警处理任务不存在");
        }
        return task;
    }

    private void checkTaskAccess(FeDeviceWarningTask task, boolean managerOnly)
    {
        if (isTaskManager())
        {
            checkDeptDataScope(task.getDeptId());
            return;
        }
        if (managerOnly || !SecurityUtils.getUserId().equals(task.getAssigneeUserId()))
        {
            throw new ServiceException("没有权限访问该预警处理任务");
        }
    }

    private void requireTaskManager()
    {
        if (!isTaskManager())
        {
            throw new ServiceException("没有预警任务分发或改派权限");
        }
    }

    private boolean isTaskManager()
    {
        if (SecurityUtils.isAdmin())
        {
            return true;
        }
        Set<String> permissions = SecurityUtils.getLoginUser().getPermissions();
        return permissions != null && (permissions.contains("*:*:*")
            || permissions.contains(REASSIGN_PERMISSION) || permissions.contains(DISPATCH_PERMISSION));
    }

    private void checkDeptDataScope(Long deptId)
    {
        if (!SecurityUtils.isAdmin())
        {
            if (deptId == null)
            {
                throw new ServiceException("任务没有所属单位，无法校验数据权限");
            }
            sysDeptService.checkDeptDataScope(deptId);
        }
    }
}
