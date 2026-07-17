-- 设备预警 V2 协调员角色（历史联调/样例库）
-- 自动派发仍只匹配预警所属单位本级处理员。
-- 所属单位没有处理员时，任务保持 manual_pending，由来源责任树协调员人工分发。
SET NAMES utf8mb4;

SET @coordinator_role_sort := IFNULL((SELECT MAX(role_sort) + 1 FROM sys_role), 10);

INSERT INTO sys_role (
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT
  '设备预警协调员', 'device_warning_coordinator', @coordinator_role_sort, '4', 1, 1,
  '0', '0', 'admin', NOW(), '查看本单位及下级单位待协调任务，并进行人工分发或改派'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role
  WHERE role_key = 'device_warning_coordinator' AND del_flag = '0'
);

UPDATE sys_role
SET role_name = '设备预警协调员',
    data_scope = '4',
    status = '0',
    remark = '查看本单位及下级单位待协调任务，并进行人工分发或改派',
    update_by = 'admin',
    update_time = NOW()
WHERE role_key = 'device_warning_coordinator'
  AND del_flag = '0';

SET @coordinator_role_id := (
  SELECT role_id FROM sys_role
  WHERE role_key = 'device_warning_coordinator' AND del_flag = '0'
  ORDER BY role_id LIMIT 1
);

SET @task_menu_id := (
  SELECT menu_id FROM sys_menu
  WHERE component = 'manage/deviceWarningTask/index'
  ORDER BY menu_id LIMIT 1
);

SET @device_menu_id := (
  SELECT parent_id FROM sys_menu
  WHERE menu_id = @task_menu_id
  LIMIT 1
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @coordinator_role_id, m.menu_id
FROM sys_menu m
WHERE @coordinator_role_id IS NOT NULL
  AND (
    m.menu_id IN (@device_menu_id, @task_menu_id)
    OR (
      m.parent_id = @task_menu_id
      AND m.perms IN (
        'manage:deviceWarningTask:query',
        'manage:deviceWarningTask:start',
        'manage:deviceWarningTask:dispatch',
        'manage:deviceWarningTask:reassign'
      )
    )
  )
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = @coordinator_role_id AND rm.menu_id = m.menu_id
  );

-- 三家公交公司测试账号：协调员负责责任树兜底，同时保留处理员身份以便必要时自行处理。
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u
JOIN sys_role r
  ON r.role_key IN ('device_warning_handler', 'device_warning_coordinator')
 AND r.status = '0'
 AND r.del_flag = '0'
WHERE u.status = '0'
  AND u.del_flag = '0'
  AND (
    (u.user_id = 101 AND u.user_name = 'changzhou' AND u.dept_id = 276)
    OR (u.user_id = 102 AND u.user_name = 'wuxi' AND u.dept_id = 272)
    OR (u.user_id = 103 AND u.user_name = 'suzhou' AND u.dept_id = 265)
  )
  AND NOT EXISTS (
    SELECT 1 FROM sys_user_role ur
    WHERE ur.user_id = u.user_id AND ur.role_id = r.role_id
  );

SELECT role_id, role_name, role_key, data_scope, status
FROM sys_role
WHERE role_key IN ('device_warning_handler', 'device_warning_coordinator')
  AND del_flag = '0'
ORDER BY role_key;

SELECT u.user_id, u.user_name, u.nick_name, u.dept_id, r.role_name, r.role_key
FROM sys_user u
JOIN sys_user_role ur ON ur.user_id = u.user_id
JOIN sys_role r ON r.role_id = ur.role_id
WHERE u.user_id IN (101, 102, 103)
  AND r.role_key IN ('device_warning_handler', 'device_warning_coordinator')
ORDER BY u.user_id, r.role_key;
