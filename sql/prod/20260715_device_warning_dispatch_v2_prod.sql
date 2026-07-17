-- 设备预警 V2 分发闭环（正式库）
-- 1. 预警事实保留在 fe_device_warning；处理工作进入独立任务表。
-- 2. 自动派发只选择预警所属单位本级、启用且拥有统一处理角色的用户。
-- 3. 无可用处理员时任务进入 manual_pending，不自动上浮到上级单位。

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS fe_device_warning_task (
    task_id bigint NOT NULL AUTO_INCREMENT COMMENT '预警任务ID',
    warning_id bigint NOT NULL COMMENT '设备预警ID',
    dept_id bigint DEFAULT NULL COMMENT '预警所属单位ID',
    source_dept_id bigint DEFAULT NULL COMMENT 'SDK凭证来源单位ID',
    fire_point_id bigint DEFAULT NULL COMMENT '消防点ID',
    handler_role_id bigint DEFAULT NULL COMMENT '派发时处理角色ID快照',
    handler_role_key varchar(100) NOT NULL DEFAULT 'device_warning_handler' COMMENT '处理角色标识快照',
    handler_role_name varchar(100) NOT NULL DEFAULT '设备预警处理员' COMMENT '处理角色名称快照',
    assignee_user_id bigint DEFAULT NULL COMMENT '当前处理人用户ID',
    assignee_user_name varchar(64) DEFAULT NULL COMMENT '当前处理人账号快照',
    assignee_nick_name varchar(64) DEFAULT NULL COMMENT '当前处理人名称快照',
    task_status varchar(32) NOT NULL DEFAULT 'manual_pending' COMMENT '任务状态: manual_pending/assigned/processing/recovered/completed/cancelled',
    dispatch_source varchar(16) DEFAULT NULL COMMENT '派发来源: auto/manual',
    dispatch_time datetime DEFAULT NULL COMMENT '最近派发时间',
    dispatch_by varchar(64) DEFAULT NULL COMMENT '最近派发人或系统标识',
    first_view_time datetime DEFAULT NULL COMMENT '处理人首次查看时间',
    start_time datetime DEFAULT NULL COMMENT '开始处理时间',
    reassign_count int NOT NULL DEFAULT 0 COMMENT '改派次数',
    last_reassign_time datetime DEFAULT NULL COMMENT '最近改派时间',
    last_reassign_by varchar(64) DEFAULT NULL COMMENT '最近改派人',
    reassign_reason varchar(500) DEFAULT NULL COMMENT '最近改派原因',
    end_time datetime DEFAULT NULL COMMENT '任务终止时间',
    end_reason varchar(500) DEFAULT NULL COMMENT '任务终止原因',
    remark varchar(500) DEFAULT NULL COMMENT '备注',
    create_by varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime DEFAULT NULL COMMENT '创建时间',
    update_by varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    del_flag char(1) NOT NULL DEFAULT '0' COMMENT '删除标志: 0存在, 2删除',
    PRIMARY KEY (task_id),
    UNIQUE KEY uk_warning_task (warning_id),
    KEY idx_warning_task_assignee (assignee_user_id, task_status, dispatch_time),
    KEY idx_warning_task_dept (dept_id, task_status, dispatch_time),
    KEY idx_warning_task_status (task_status, update_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备预警处理任务表';

ALTER TABLE fe_device_warning
    MODIFY COLUMN warning_status varchar(32) NOT NULL DEFAULT 'pending'
    COMMENT '工作流状态: pending待分发/dispatched已派发/processing处理中/resolved已解除/false_alarm误报';

SET @handler_role_sort := IFNULL((SELECT MAX(role_sort) + 1 FROM sys_role), 10);

INSERT INTO sys_role (
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT
  '设备预警处理员', 'device_warning_handler', @handler_role_sort, '4', 1, 1,
  '0', '0', 'admin', NOW(), '处理所属单位本级自动派发的设备预警任务'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'device_warning_handler');

UPDATE sys_role
SET role_name = '设备预警处理员',
    data_scope = '4',
    status = '0',
    remark = '处理所属单位本级自动派发的设备预警任务',
    update_by = 'admin',
    update_time = NOW()
WHERE role_key = 'device_warning_handler'
  AND del_flag = '0';

SET @device_menu_id := (
  SELECT menu_id FROM sys_menu
  WHERE path = 'manage' AND menu_type = 'M'
  ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '预警处理任务', @device_menu_id, 5, 'deviceWarningTask', 'manage/deviceWarningTask/index', NULL, '',
  1, 0, 'C', '0', '0', 'manage:deviceWarningTask:list', 'list',
  'admin', NOW(), 'admin', NOW(), '设备预警分发与处理工作清单'
FROM dual
WHERE @device_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'manage/deviceWarningTask/index');

SET @task_menu_id := (
  SELECT menu_id FROM sys_menu
  WHERE component = 'manage/deviceWarningTask/index'
  ORDER BY menu_id LIMIT 1
);

UPDATE sys_menu
SET menu_name = '预警处理任务', parent_id = @device_menu_id, order_num = 5,
    path = 'deviceWarningTask', component = 'manage/deviceWarningTask/index',
    menu_type = 'C', visible = '0', status = '0',
    perms = 'manage:deviceWarningTask:list', icon = 'list',
    remark = '设备预警分发与处理工作清单', update_by = 'admin', update_time = NOW()
WHERE @task_menu_id IS NOT NULL AND menu_id = @task_menu_id;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT p.menu_name, @task_menu_id, p.order_num, '', NULL, NULL, '',
       1, 0, 'F', '0', '0', p.perms, '#',
       'admin', NOW(), 'admin', NOW(), p.remark
FROM (
  SELECT '任务查询' AS menu_name, 1 AS order_num, 'manage:deviceWarningTask:query' AS perms, '查看预警处理任务详情' AS remark
  UNION ALL SELECT '开始处理', 2, 'manage:deviceWarningTask:start', '将本人待处理任务转为处理中'
  UNION ALL SELECT '人工分发', 3, 'manage:deviceWarningTask:dispatch', '人工触发预警任务分发'
  UNION ALL SELECT '任务改派', 4, 'manage:deviceWarningTask:reassign', '改派待人工分发或待处理任务'
) p
WHERE @task_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu m
    WHERE m.parent_id = @task_menu_id AND m.perms = p.perms
  );

SET @handler_role_id := (
  SELECT role_id FROM sys_role
  WHERE role_key = 'device_warning_handler' AND del_flag = '0'
  ORDER BY role_id LIMIT 1
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (
    m.menu_id IN (@device_menu_id, @task_menu_id)
    OR m.parent_id = @task_menu_id
  )
  AND NOT EXISTS (
  SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @handler_role_id, m.menu_id
FROM sys_menu m
WHERE @handler_role_id IS NOT NULL
  AND (
    m.menu_id IN (@device_menu_id, @task_menu_id)
    OR (m.parent_id = @task_menu_id AND m.perms IN (
      'manage:deviceWarningTask:query', 'manage:deviceWarningTask:start'
    ))
  )
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = @handler_role_id AND rm.menu_id = m.menu_id
  );

-- 首页卡片统计活动且未结束的预警，权限字符保持不变，仅更新管理端中文名称。
UPDATE sys_menu
SET menu_name = '待处理预警',
    update_by = 'admin',
    update_time = NOW()
WHERE perms = 'dashboard:card:alarm';
