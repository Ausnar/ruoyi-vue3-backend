SET NAMES utf8mb4;

SET @schema_name := DATABASE();

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'visit_mode'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `visit_mode` varchar(32) NOT NULL DEFAULT 'active' COMMENT '拜访模式(active/passive)' AFTER `applicant_dept_id`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'source_type'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `source_type` varchar(64) DEFAULT 'manual' COMMENT '来源类型(manual/gateway_displacement)' AFTER `visit_mode`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'source_event_id'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `source_event_id` bigint(20) DEFAULT NULL COMMENT '来源事件ID' AFTER `source_type`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'trigger_gateway_id'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `trigger_gateway_id` bigint(20) DEFAULT NULL COMMENT '触发网关ID' AFTER `source_event_id`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'trigger_fire_point_id'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `trigger_fire_point_id` bigint(20) DEFAULT NULL COMMENT '触发消防点ID' AFTER `trigger_gateway_id`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'trigger_from_longitude'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `trigger_from_longitude` decimal(12,7) DEFAULT NULL COMMENT '触发起点经度' AFTER `trigger_fire_point_id`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'trigger_from_latitude'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `trigger_from_latitude` decimal(12,7) DEFAULT NULL COMMENT '触发起点纬度' AFTER `trigger_from_longitude`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'trigger_to_longitude'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `trigger_to_longitude` decimal(12,7) DEFAULT NULL COMMENT '触发终点经度' AFTER `trigger_from_latitude`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'trigger_to_latitude'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `trigger_to_latitude` decimal(12,7) DEFAULT NULL COMMENT '触发终点纬度' AFTER `trigger_to_longitude`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND COLUMN_NAME = 'trigger_distance_m'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply` ADD COLUMN `trigger_distance_m` decimal(10,2) DEFAULT NULL COMMENT '触发位移距离(米)' AFTER `trigger_to_latitude`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_apply' AND INDEX_NAME = 'idx_visit_apply_mode_status'),
  'SELECT 1',
  'ALTER TABLE `fe_visit_apply` ADD KEY `idx_visit_apply_mode_status` (`visit_mode`, `status`, `del_flag`)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_customer' AND COLUMN_NAME = 'longitude'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_customer` ADD COLUMN `longitude` decimal(12,7) DEFAULT NULL COMMENT '经度' AFTER `address`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_visit_customer' AND COLUMN_NAME = 'latitude'),
  'SELECT 1',
  "ALTER TABLE `fe_visit_customer` ADD COLUMN `latitude` decimal(12,7) DEFAULT NULL COMMENT '纬度' AFTER `longitude`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS `fe_gateway_gps_history` (
  `history_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '历史ID',
  `gateway_id` bigint(20) NOT NULL COMMENT '网关ID',
  `external_tbox_id` bigint(20) DEFAULT NULL COMMENT '外部TBox ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '归属部门ID',
  `gps_longitude` decimal(12,7) NOT NULL COMMENT '经度',
  `gps_latitude` decimal(12,7) NOT NULL COMMENT '纬度',
  `gps_time` datetime NOT NULL COMMENT 'GPS时间',
  `sync_time` datetime NOT NULL COMMENT '同步时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`history_id`),
  KEY `idx_gateway_gps_history_gateway_time` (`gateway_id`, `gps_time`),
  KEY `idx_gateway_gps_history_dept_time` (`dept_id`, `gps_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网关GPS历史记录';

CREATE TABLE IF NOT EXISTS `fe_visit_passive_event` (
  `event_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '事件ID',
  `gateway_id` bigint(20) NOT NULL COMMENT '网关ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '归属部门ID',
  `fire_point_id` bigint(20) DEFAULT NULL COMMENT '消防点ID',
  `from_longitude` decimal(12,7) NOT NULL COMMENT '起点经度',
  `from_latitude` decimal(12,7) NOT NULL COMMENT '起点纬度',
  `to_longitude` decimal(12,7) NOT NULL COMMENT '终点经度',
  `to_latitude` decimal(12,7) NOT NULL COMMENT '终点纬度',
  `distance_m` decimal(10,2) NOT NULL COMMENT '位移距离(米)',
  `trigger_time` datetime NOT NULL COMMENT '触发时间',
  `candidate_summary` text COMMENT '候选客户快照(JSON)',
  `selected_target_type` varchar(32) DEFAULT NULL COMMENT '已选目标类型',
  `selected_target_id` bigint(20) DEFAULT NULL COMMENT '已选目标ID',
  `status` varchar(32) NOT NULL COMMENT '状态(pending_confirm/converted/ignored)',
  `confirm_user_id` bigint(20) DEFAULT NULL COMMENT '确认人用户ID',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `visit_id` bigint(20) DEFAULT NULL COMMENT '关联拜访单ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`event_id`),
  KEY `idx_visit_passive_event_gateway_status` (`gateway_id`, `status`),
  KEY `idx_visit_passive_event_dept_status` (`dept_id`, `status`),
  KEY `idx_visit_passive_event_trigger_time` (`trigger_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外出拜访被动事件';

CREATE TABLE IF NOT EXISTS `fe_visit_owner_assign` (
  `assign_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分配ID',
  `target_type` varchar(32) NOT NULL COMMENT '目标类型(contract_dept/independent_customer)',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `owner_user_id` bigint(20) NOT NULL COMMENT '负责人用户ID',
  `owner_dept_id` bigint(20) NOT NULL COMMENT '负责人部门ID',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`assign_id`),
  UNIQUE KEY `uk_visit_owner_assign_target` (`target_type`, `target_id`),
  KEY `idx_visit_owner_assign_owner` (`owner_user_id`, `status`),
  KEY `idx_visit_owner_assign_dept` (`owner_dept_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外出拜访负责人分配';

INSERT INTO sys_dict_type (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT '外出拜访模式', 'fe_visit_mode', '0', 'admin', NOW(), '外出拜访模式'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'fe_visit_mode');

INSERT INTO sys_dict_type (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT '外出拜访来源类型', 'fe_visit_source_type', '0', 'admin', NOW(), '外出拜访来源类型'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'fe_visit_source_type');

INSERT INTO sys_dict_type (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT '被动事件状态', 'fe_visit_passive_event_status', '0', 'admin', NOW(), '被动事件状态'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'fe_visit_passive_event_status');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 1, '主动', 'active', 'fe_visit_mode', '', 'primary', 'Y', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_mode' AND dict_value = 'active');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 2, '被动', 'passive', 'fe_visit_mode', '', 'warning', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_mode' AND dict_value = 'passive');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 1, '手工申请', 'manual', 'fe_visit_source_type', '', 'primary', 'Y', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_source_type' AND dict_value = 'manual');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 2, '网关位移推断', 'gateway_displacement', 'fe_visit_source_type', '', 'warning', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_source_type' AND dict_value = 'gateway_displacement');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 1, '待确认', 'pending_confirm', 'fe_visit_passive_event_status', '', 'warning', 'Y', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_passive_event_status' AND dict_value = 'pending_confirm');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 2, '已转单', 'converted', 'fe_visit_passive_event_status', '', 'success', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_passive_event_status' AND dict_value = 'converted');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 3, '已忽略', 'ignored', 'fe_visit_passive_event_status', '', 'info', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_passive_event_status' AND dict_value = 'ignored');

SET @visit_parent_menu_id := (SELECT menu_id FROM sys_menu WHERE path = 'visit' AND menu_type = 'M' ORDER BY menu_id LIMIT 1);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '被动事件确认', @visit_parent_menu_id, 5, 'passiveEvent', 'visit/passiveEvent/index', NULL, '',
  1, 0, 'C', '0', '0', 'visit:passiveEvent:query', 'map-location',
  'admin', NOW(), 'admin', NOW(), '被动事件确认菜单'
FROM dual
WHERE @visit_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'visit/passiveEvent/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '责任分配', @visit_parent_menu_id, 6, 'ownerAssign', 'visit/ownerAssign/index', NULL, '',
  1, 0, 'C', '0', '0', 'visit:ownerAssign:query', 'userFilled',
  'admin', NOW(), 'admin', NOW(), '责任分配菜单'
FROM dual
WHERE @visit_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'visit/ownerAssign/index');

SET @visit_passive_event_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'visit/passiveEvent/index' ORDER BY menu_id LIMIT 1);
SET @visit_owner_assign_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'visit/ownerAssign/index' ORDER BY menu_id LIMIT 1);
SET @visit_apply_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'visit/apply/index' ORDER BY menu_id LIMIT 1);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @visit_passive_event_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '被动事件查询' AS menu_name, 1 AS order_num, 'visit:passiveEvent:query' AS perms, '被动事件查询按钮' AS remark
  UNION ALL SELECT '被动事件确认转单', 2, 'visit:passiveEvent:confirm', '被动事件确认按钮'
  UNION ALL SELECT '被动事件忽略', 3, 'visit:passiveEvent:ignore', '被动事件忽略按钮'
) t
WHERE @visit_passive_event_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @visit_passive_event_menu_id AND s.perms = t.perms);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @visit_owner_assign_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '责任分配查询' AS menu_name, 1 AS order_num, 'visit:ownerAssign:query' AS perms, '责任分配查询按钮' AS remark
  UNION ALL SELECT '责任分配新增', 2, 'visit:ownerAssign:add', '责任分配新增按钮'
  UNION ALL SELECT '责任分配修改', 3, 'visit:ownerAssign:edit', '责任分配修改按钮'
  UNION ALL SELECT '责任分配删除', 4, 'visit:ownerAssign:remove', '责任分配删除按钮'
) t
WHERE @visit_owner_assign_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @visit_owner_assign_menu_id AND s.perms = t.perms);

SET @visit_passive_role_sort := IFNULL((SELECT MAX(role_sort) + 1 FROM sys_role), 10);
INSERT INTO sys_role (`role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
SELECT '被动事件处理', 'visit_passive_handler', @visit_passive_role_sort, '4', 1, 1, '0', '0', 'admin', NOW(), '被动事件处理角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'visit_passive_handler');

SET @visit_passive_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'visit_passive_handler' ORDER BY role_id LIMIT 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (
    m.component IN ('visit/passiveEvent/index', 'visit/ownerAssign/index')
    OR m.perms IN (
      'visit:passiveEvent:query', 'visit:passiveEvent:confirm', 'visit:passiveEvent:ignore',
      'visit:ownerAssign:query', 'visit:ownerAssign:add', 'visit:ownerAssign:edit', 'visit:ownerAssign:remove'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @visit_passive_role_id, m.menu_id
FROM sys_menu m
WHERE @visit_passive_role_id IS NOT NULL
  AND (
    m.path = 'visit'
    OR m.component IN ('visit/apply/index', 'visit/passiveEvent/index')
    OR m.perms IN (
      'visit:apply:query', 'visit:apply:feedback',
      'visit:passiveEvent:query', 'visit:passiveEvent:confirm', 'visit:passiveEvent:ignore'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @visit_passive_role_id AND rm.menu_id = m.menu_id);