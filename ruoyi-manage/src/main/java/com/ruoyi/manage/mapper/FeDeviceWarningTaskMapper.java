package com.ruoyi.manage.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.manage.domain.FeDeviceWarning;
import com.ruoyi.manage.domain.FeDeviceWarningTask;
import com.ruoyi.manage.domain.FeDeviceWarningTaskRecord;

public interface FeDeviceWarningTaskMapper
{
    List<FeDeviceWarningTask> selectFeDeviceWarningTaskList(FeDeviceWarningTask task);

    FeDeviceWarningTask selectFeDeviceWarningTaskByTaskId(Long taskId);

    FeDeviceWarningTask selectFeDeviceWarningTaskByWarningId(Long warningId);

    List<FeDeviceWarning> selectActiveWarningsWithoutTask(@Param("sourceDeptId") Long sourceDeptId);

    SysRole selectHandlerRoleByRoleKey(String roleKey);

    List<SysUser> selectDispatchCandidates(@Param("deptId") Long deptId, @Param("roleKey") String roleKey);

    List<SysUser> selectManualDispatchCandidates(@Param("sourceDeptId") Long sourceDeptId,
        @Param("deptId") Long deptId, @Param("handlerRoleKey") String handlerRoleKey,
        @Param("coordinatorRoleKey") String coordinatorRoleKey);

    SysUser selectEligibleAssignee(@Param("sourceDeptId") Long sourceDeptId, @Param("deptId") Long deptId,
        @Param("handlerRoleKey") String handlerRoleKey, @Param("coordinatorRoleKey") String coordinatorRoleKey,
        @Param("userId") Long userId);

    SysRole selectAssigneeRole(@Param("userId") Long userId, @Param("handlerRoleKey") String handlerRoleKey,
        @Param("coordinatorRoleKey") String coordinatorRoleKey);

    int insertFeDeviceWarningTask(FeDeviceWarningTask task);

    int updateTaskAssignment(FeDeviceWarningTask task);

    int updateTaskStatus(FeDeviceWarningTask task);

    int updateTaskTreatmentStatus(@Param("taskId") Long taskId, @Param("taskStatus") String taskStatus,
        @Param("updateBy") String updateBy, @Param("updateTime") Date updateTime);

    int insertFeDeviceWarningTaskRecord(FeDeviceWarningTaskRecord record);

    List<FeDeviceWarningTaskRecord> selectTreatmentRecords(Long taskId);

    int markTaskViewed(@Param("taskId") Long taskId, @Param("viewTime") Date viewTime,
        @Param("updateBy") String updateBy, @Param("updateTime") Date updateTime);

    int closeRecoveredTasks(@Param("sourceDeptId") Long sourceDeptId, @Param("endTime") Date endTime,
        @Param("endReason") String endReason, @Param("updateBy") String updateBy,
        @Param("updateTime") Date updateTime);
}
