SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `fe_gateway_manual_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '网关记录ID',
  `external_company_id` bigint(20) DEFAULT NULL COMMENT '外部单位ID',
  `external_company_name_snapshot` varchar(255) DEFAULT NULL COMMENT '外部单位名称快照',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '录入归口单位ID',
  `source_dept_id` bigint(20) DEFAULT NULL COMMENT '来源单位ID',
  `gateway_id` bigint(20) DEFAULT NULL COMMENT '已匹配网关ID',
  `gateway_config_code` varchar(100) DEFAULT NULL COMMENT 'TBOX配置号',
  `gateway_qr_code` varchar(100) DEFAULT NULL COMMENT '二维码号',
  `gateway_imei` varchar(64) DEFAULT NULL COMMENT '4G号/IMEI',
  `sim_no` varchar(64) DEFAULT NULL COMMENT 'SIM卡号',
  `sim_open_month` varchar(32) DEFAULT NULL COMMENT 'SIM开卡月',
  `placement_location` varchar(500) DEFAULT NULL COMMENT '放置地点',
  `status` varchar(32) NOT NULL DEFAULT 'active' COMMENT '状态(draft/active/void)',
  `match_status` varchar(32) NOT NULL DEFAULT 'unmatched' COMMENT '匹配状态(unmatched/matched)',
  `installer_name` varchar(64) DEFAULT NULL COMMENT '施工人员',
  `install_time` datetime DEFAULT NULL COMMENT '施工时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_gateway_manual_company` (`external_company_id`),
  KEY `idx_gateway_manual_dept` (`dept_id`),
  KEY `idx_gateway_manual_imei` (`gateway_imei`),
  KEY `idx_gateway_manual_status` (`status`, `match_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备人工施工台账-网关记录';

CREATE TABLE IF NOT EXISTS `fe_sensor_manual_record` (
  `sensor_record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '传感器记录ID',
  `gateway_record_id` bigint(20) NOT NULL COMMENT '所属网关记录ID',
  `external_company_id` bigint(20) DEFAULT NULL COMMENT '外部单位ID',
  `external_company_name_snapshot` varchar(255) DEFAULT NULL COMMENT '外部单位名称快照',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '录入归口单位ID',
  `sensor_id` bigint(20) DEFAULT NULL COMMENT '已匹配传感器ID',
  `extinguisher_id` bigint(20) DEFAULT NULL COMMENT '已匹配灭火器ID',
  `sensor_code` varchar(100) DEFAULT NULL COMMENT '传感器号',
  `mac_address` varchar(100) DEFAULT NULL COMMENT 'MAC地址',
  `assembly_address` varchar(500) DEFAULT NULL COMMENT '装配地址',
  `extinguisher_body_serial_no` varchar(100) DEFAULT NULL COMMENT '灭火器瓶体序列号',
  `extinguisher_production_date` date DEFAULT NULL COMMENT '灭火器出产日期',
  `sensor_vendor_type` varchar(32) DEFAULT NULL COMMENT '传感器类型(self_research/third_party)',
  `status` varchar(32) NOT NULL DEFAULT 'active' COMMENT '状态(draft/active/void)',
  `match_status` varchar(32) NOT NULL DEFAULT 'unmatched' COMMENT '匹配状态(unmatched/partial_matched/full_matched)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`sensor_record_id`),
  KEY `idx_sensor_manual_gateway` (`gateway_record_id`),
  KEY `idx_sensor_manual_company` (`external_company_id`),
  KEY `idx_sensor_manual_dept` (`dept_id`),
  KEY `idx_sensor_manual_code` (`sensor_code`),
  KEY `idx_sensor_manual_mac` (`mac_address`),
  KEY `idx_sensor_manual_status` (`status`, `match_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备人工施工台账-传感器记录';

SET @manual_parent_order := IFNULL((SELECT MAX(order_num) + 1 FROM sys_menu WHERE parent_id = 0), 8);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '人工施工台账', 0, @manual_parent_order, 'manualLedger', NULL, NULL, '',
  1, 0, 'M', '0', '0', '', 'clipboard',
  'admin', NOW(), 'admin', NOW(), '设备人工施工台账一级菜单'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'manualLedger' AND menu_type = 'M');

SET @manual_parent_menu_id := IFNULL(
  (SELECT menu_id FROM sys_menu WHERE path = 'manualLedger' AND menu_type = 'M' ORDER BY menu_id LIMIT 1),
  (SELECT menu_id FROM sys_menu WHERE menu_name = '人工施工台账' AND menu_type = 'M' ORDER BY menu_id LIMIT 1)
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '网关记录', @manual_parent_menu_id, 1, 'manualGateway', 'manage/manualGateway/index', NULL, '',
  1, 0, 'C', '0', '0', 'manage:manualGateway:list', 'connection',
  'admin', NOW(), 'admin', NOW(), '设备人工施工台账-网关记录'
FROM dual
WHERE @manual_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'manage/manualGateway/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '传感器记录', @manual_parent_menu_id, 2, 'manualSensor', 'manage/manualSensor/index', NULL, '',
  1, 0, 'C', '0', '0', 'manage:manualSensor:list', 'cpu',
  'admin', NOW(), 'admin', NOW(), '设备人工施工台账-传感器记录'
FROM dual
WHERE @manual_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'manage/manualSensor/index');

UPDATE sys_menu
SET parent_id = @manual_parent_menu_id, order_num = 1, menu_name = '网关记录', remark = '设备人工施工台账-网关记录'
WHERE @manual_parent_menu_id IS NOT NULL
  AND component = 'manage/manualGateway/index';

UPDATE sys_menu
SET parent_id = @manual_parent_menu_id, order_num = 2, menu_name = '传感器记录', remark = '设备人工施工台账-传感器记录'
WHERE @manual_parent_menu_id IS NOT NULL
  AND component = 'manage/manualSensor/index';

SET @manual_gateway_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'manage/manualGateway/index' ORDER BY menu_id LIMIT 1);
SET @manual_sensor_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'manage/manualSensor/index' ORDER BY menu_id LIMIT 1);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @manual_gateway_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '网关记录查询' AS menu_name, 1 AS order_num, 'manage:manualGateway:query' AS perms, '人工台账网关查询' AS remark
  UNION ALL SELECT '网关记录新增', 2, 'manage:manualGateway:add', '人工台账网关新增'
  UNION ALL SELECT '网关记录修改', 3, 'manage:manualGateway:edit', '人工台账网关修改'
  UNION ALL SELECT '网关记录作废', 4, 'manage:manualGateway:void', '人工台账网关作废'
  UNION ALL SELECT '网关记录导出', 5, 'manage:manualGateway:export', '人工台账网关导出'
  UNION ALL SELECT '外部单位快速创建', 6, 'manage:manualExternalCompany:add', '人工台账外部单位快速创建'
) t
WHERE @manual_gateway_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @manual_gateway_menu_id AND s.perms = t.perms);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @manual_sensor_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '传感器记录查询' AS menu_name, 1 AS order_num, 'manage:manualSensor:query' AS perms, '人工台账传感器查询' AS remark
  UNION ALL SELECT '传感器记录新增', 2, 'manage:manualSensor:add', '人工台账传感器新增'
  UNION ALL SELECT '传感器记录修改', 3, 'manage:manualSensor:edit', '人工台账传感器修改'
  UNION ALL SELECT '传感器记录作废', 4, 'manage:manualSensor:void', '人工台账传感器作废'
  UNION ALL SELECT '传感器记录导出', 5, 'manage:manualSensor:export', '人工台账传感器导出'
) t
WHERE @manual_sensor_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @manual_sensor_menu_id AND s.perms = t.perms);

SET @governance_role_sort := IFNULL((SELECT MAX(role_sort) + 1 FROM sys_role), 10);
INSERT INTO sys_role (`role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
SELECT '设备治理专员', 'device_governance_specialist', @governance_role_sort, '4', 1, 1, '0', '0', 'admin', NOW(), '设备人工施工台账录入角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'device_governance_specialist');

SET @governance_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'device_governance_specialist' ORDER BY role_id LIMIT 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (
    m.component IN ('manage/manualGateway/index', 'manage/manualSensor/index')
    OR m.perms IN (
      'manage:manualGateway:list', 'manage:manualGateway:query', 'manage:manualGateway:add', 'manage:manualGateway:edit', 'manage:manualGateway:void', 'manage:manualGateway:export',
      'manage:manualSensor:list', 'manage:manualSensor:query', 'manage:manualSensor:add', 'manage:manualSensor:edit', 'manage:manualSensor:void', 'manage:manualSensor:export',
      'manage:manualExternalCompany:add'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @governance_role_id, m.menu_id
FROM sys_menu m
WHERE @governance_role_id IS NOT NULL
  AND (
    m.path = 'manualLedger'
    OR m.component IN ('manage/manualGateway/index', 'manage/manualSensor/index')
    OR m.perms IN (
      'manage:manualGateway:list', 'manage:manualGateway:query', 'manage:manualGateway:add', 'manage:manualGateway:edit', 'manage:manualGateway:void', 'manage:manualGateway:export',
      'manage:manualSensor:list', 'manage:manualSensor:query', 'manage:manualSensor:add', 'manage:manualSensor:edit', 'manage:manualSensor:void', 'manage:manualSensor:export',
      'manage:manualExternalCompany:add'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @governance_role_id AND rm.menu_id = m.menu_id);

UPDATE sys_menu SET menu_name = '网关记录', remark = '设备人工施工台账-网关记录'
WHERE component = 'manage/manualGateway/index';

UPDATE sys_menu SET menu_name = '传感器记录', remark = '设备人工施工台账-传感器记录'
WHERE component = 'manage/manualSensor/index';

UPDATE sys_menu SET menu_name = '人工施工台账', remark = '设备人工施工台账一级菜单'
WHERE path = 'manualLedger' AND menu_type = 'M';

UPDATE sys_menu SET menu_name = '网关记录查询', remark = '人工台账网关查询'
WHERE perms = 'manage:manualGateway:query';

UPDATE sys_menu SET menu_name = '网关记录新增', remark = '人工台账网关新增'
WHERE perms = 'manage:manualGateway:add';

UPDATE sys_menu SET menu_name = '网关记录修改', remark = '人工台账网关修改'
WHERE perms = 'manage:manualGateway:edit';

UPDATE sys_menu SET menu_name = '网关记录作废', remark = '人工台账网关作废'
WHERE perms = 'manage:manualGateway:void';

UPDATE sys_menu SET menu_name = '网关记录导出', remark = '人工台账网关导出'
WHERE perms = 'manage:manualGateway:export';

UPDATE sys_menu SET menu_name = '外部单位快速创建', remark = '人工台账外部单位快速创建'
WHERE perms = 'manage:manualExternalCompany:add';

UPDATE sys_menu SET menu_name = '传感器记录查询', remark = '人工台账传感器查询'
WHERE perms = 'manage:manualSensor:query';

UPDATE sys_menu SET menu_name = '传感器记录新增', remark = '人工台账传感器新增'
WHERE perms = 'manage:manualSensor:add';

UPDATE sys_menu SET menu_name = '传感器记录修改', remark = '人工台账传感器修改'
WHERE perms = 'manage:manualSensor:edit';

UPDATE sys_menu SET menu_name = '传感器记录作废', remark = '人工台账传感器作废'
WHERE perms = 'manage:manualSensor:void';

UPDATE sys_menu SET menu_name = '传感器记录导出', remark = '人工台账传感器导出'
WHERE perms = 'manage:manualSensor:export';

UPDATE sys_role
SET role_name = '设备治理专员', remark = '设备人工施工台账录入角色'
WHERE role_key = 'device_governance_specialist';
