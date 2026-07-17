-- 设备预警 V3 处置记录第一阶段（开发/保底库）
-- 本站只记录处置过程和等待状态，不修改 SDK 数据源中的设备绑定关系。
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS fe_device_warning_task_record (
    record_id bigint NOT NULL AUTO_INCREMENT COMMENT '处置记录ID',
    task_id bigint NOT NULL COMMENT '预警任务ID',
    warning_id bigint NOT NULL COMMENT '设备预警ID',
    action_type varchar(64) NOT NULL COMMENT '处置方式',
    action_channel varchar(32) NOT NULL COMMENT '处置渠道: onsite/external_platform/web_record/other',
    action_description varchar(1000) NOT NULL COMMENT '处置说明',
    next_task_status varchar(32) NOT NULL COMMENT '记录后的任务状态: waiting_external/verifying',
    operator_user_id bigint NOT NULL COMMENT '操作人用户ID',
    operator_user_name varchar(64) NOT NULL COMMENT '操作人账号快照',
    operator_nick_name varchar(64) DEFAULT NULL COMMENT '操作人名称快照',
    action_time datetime NOT NULL COMMENT '处置记录时间',
    create_by varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (record_id),
    KEY idx_warning_task_record_task (task_id, action_time),
    KEY idx_warning_task_record_warning (warning_id, action_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备预警任务处置记录表';

ALTER TABLE fe_device_warning_task
    MODIFY COLUMN task_status varchar(32) NOT NULL DEFAULT 'manual_pending'
    COMMENT '任务状态: manual_pending/assigned/processing/waiting_external/verifying/recovered/completed/cancelled';

SET @task_menu_id := (
  SELECT menu_id FROM sys_menu
  WHERE component = 'manage/deviceWarningTask/index'
  ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '记录处置', @task_menu_id, 5, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'manage:deviceWarningTask:treat', '#',
  'admin', NOW(), 'admin', NOW(), '记录现场、外部平台或本站处置过程，并等待SDK恢复验证'
FROM dual
WHERE @task_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE parent_id = @task_menu_id AND perms = 'manage:deviceWarningTask:treat'
  );
SET @treat_menu_id := (
  SELECT menu_id FROM sys_menu
  WHERE parent_id = @task_menu_id AND perms = 'manage:deviceWarningTask:treat'
  ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, @treat_menu_id
FROM sys_role r
WHERE @treat_menu_id IS NOT NULL
  AND (r.role_id = 1 OR r.role_key IN ('device_warning_handler', 'device_warning_coordinator'))
  AND r.del_flag = '0'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = r.role_id AND rm.menu_id = @treat_menu_id
  );
