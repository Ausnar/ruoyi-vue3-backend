SET NAMES utf8mb4;

SET @db_name := DATABASE();

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND COLUMN_NAME = 'manual_confirmed_name') = 0,
  'ALTER TABLE `fe_external_company` ADD COLUMN `manual_confirmed_name` varchar(255) DEFAULT NULL COMMENT ''人工治理名'' AFTER `external_company_name`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND COLUMN_NAME = 'first_source_type') = 0,
  'ALTER TABLE `fe_external_company` ADD COLUMN `first_source_type` varchar(32) DEFAULT NULL COMMENT ''首次来源类型(sdk/manual)'' AFTER `sync_status`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND COLUMN_NAME = 'sdk_observed') = 0,
  'ALTER TABLE `fe_external_company` ADD COLUMN `sdk_observed` char(1) NOT NULL DEFAULT ''0'' COMMENT ''是否已被SDK观测(1是 0否)'' AFTER `first_source_type`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND COLUMN_NAME = 'manual_created') = 0,
  'ALTER TABLE `fe_external_company` ADD COLUMN `manual_created` char(1) NOT NULL DEFAULT ''0'' COMMENT ''是否有人工作为主体创建(1是 0否)'' AFTER `sdk_observed`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND COLUMN_NAME = 'manual_created_by') = 0,
  'ALTER TABLE `fe_external_company` ADD COLUMN `manual_created_by` varchar(64) DEFAULT NULL COMMENT ''人工创建人'' AFTER `manual_created`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND COLUMN_NAME = 'manual_created_time') = 0,
  'ALTER TABLE `fe_external_company` ADD COLUMN `manual_created_time` datetime DEFAULT NULL COMMENT ''人工创建时间'' AFTER `manual_created_by`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND COLUMN_NAME = 'record_status') = 0,
  'ALTER TABLE `fe_external_company` ADD COLUMN `record_status` varchar(32) NOT NULL DEFAULT ''active'' COMMENT ''记录状态(active/disabled/merged)'' AFTER `manual_created_time`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND COLUMN_NAME = 'merged_to_company_record_id') = 0,
  'ALTER TABLE `fe_external_company` ADD COLUMN `merged_to_company_record_id` bigint(20) DEFAULT NULL COMMENT ''并入目标记录ID'' AFTER `record_status`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND COLUMN_NAME = 'merged_to_external_company_id') = 0,
  'ALTER TABLE `fe_external_company` ADD COLUMN `merged_to_external_company_id` bigint(20) DEFAULT NULL COMMENT ''并入目标外部单位ID'' AFTER `merged_to_company_record_id`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS `fe_external_company_merge_log` (
  `merge_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '并档日志ID',
  `source_company_record_id` bigint(20) NOT NULL COMMENT '源记录ID',
  `source_external_company_id` bigint(20) NOT NULL COMMENT '源外部单位ID',
  `source_external_company_name` varchar(255) DEFAULT NULL COMMENT '源外部单位名称快照',
  `target_company_record_id` bigint(20) NOT NULL COMMENT '目标记录ID',
  `target_external_company_id` bigint(20) NOT NULL COMMENT '目标外部单位ID',
  `target_external_company_name` varchar(255) DEFAULT NULL COMMENT '目标外部单位名称快照',
  `merge_strategy` varchar(64) NOT NULL DEFAULT 'manual_confirmed' COMMENT '并档策略',
  `merge_reason` varchar(500) DEFAULT NULL COMMENT '并档备注',
  `merge_time` datetime NOT NULL COMMENT '并档时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`merge_log_id`),
  KEY `idx_external_company_merge_source` (`source_company_record_id`),
  KEY `idx_external_company_merge_target` (`target_company_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外部单位并档日志';

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND INDEX_NAME = 'idx_external_company_record_status') = 0,
  'ALTER TABLE `fe_external_company` ADD INDEX `idx_external_company_record_status` (`record_status`, `sdk_observed`, `manual_created`)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS
   WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'fe_external_company' AND INDEX_NAME = 'idx_external_company_merged_to') = 0,
  'ALTER TABLE `fe_external_company` ADD INDEX `idx_external_company_merged_to` (`merged_to_company_record_id`)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `fe_external_company`
SET
  `manual_confirmed_name` = NULLIF(TRIM(`manual_confirmed_name`), ''),
  `first_source_type` = CASE
    WHEN `first_source_type` IS NOT NULL AND `first_source_type` <> '' THEN `first_source_type`
    WHEN `external_company_id` < 0 THEN 'manual'
    ELSE 'sdk'
  END,
  `sdk_observed` = CASE
    WHEN `sdk_observed` IN ('0', '1') THEN `sdk_observed`
    WHEN `external_company_id` < 0 THEN '0'
    ELSE '1'
  END,
  `manual_created` = CASE
    WHEN `manual_created` IN ('0', '1') THEN `manual_created`
    WHEN `external_company_id` < 0 THEN '1'
    ELSE '0'
  END,
  `manual_created_by` = CASE
    WHEN `manual_created_by` IS NOT NULL AND `manual_created_by` <> '' THEN `manual_created_by`
    WHEN `external_company_id` < 0 THEN COALESCE(NULLIF(`create_by`, ''), 'admin')
    ELSE `manual_created_by`
  END,
  `manual_created_time` = CASE
    WHEN `manual_created_time` IS NOT NULL THEN `manual_created_time`
    WHEN `external_company_id` < 0 THEN COALESCE(`create_time`, `update_time`)
    ELSE `manual_created_time`
  END,
  `record_status` = CASE
    WHEN `record_status` IS NOT NULL AND `record_status` <> '' THEN `record_status`
    ELSE 'active'
  END;

SET @unit_center_order := IFNULL((SELECT MAX(order_num) + 1 FROM sys_menu WHERE parent_id = 0), 9);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '单位中心', 0, @unit_center_order, 'unitCenter', NULL, NULL, '',
  1, 0, 'M', '0', '0', '', 'office-building',
  'admin', NOW(), 'admin', NOW(), '单位治理统一入口'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'unitCenter' AND menu_type = 'M');

SET @unit_center_menu_id := (
  SELECT menu_id FROM sys_menu WHERE path = 'unitCenter' AND menu_type = 'M' ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '本地单位管理', @unit_center_menu_id, 1, 'dept', 'system/dept/index', NULL, '',
  1, 0, 'C', '0', '0', 'system:dept:list', 'tree-table',
  'admin', NOW(), 'admin', NOW(), '单位中心-本地单位管理'
FROM dual
WHERE @unit_center_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'system/dept/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '合同管理', @unit_center_menu_id, 2, 'contract', 'system/contract/index', NULL, '',
  1, 0, 'C', '0', '0', 'system:contract:list', 'tickets',
  'admin', NOW(), 'admin', NOW(), '单位中心-合同管理'
FROM dual
WHERE @unit_center_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'system/contract/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '外部单位管理', @unit_center_menu_id, 3, 'externalCompany', 'manage/externalCompany/index', NULL, '',
  1, 0, 'C', '0', '0', 'manage:externalCompany:list', 'office-building',
  'admin', NOW(), 'admin', NOW(), '单位中心-外部单位管理'
FROM dual
WHERE @unit_center_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'manage/externalCompany/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '外部单位映射', @unit_center_menu_id, 4, 'companyDeptMapping', 'manage/companyDeptMapping/index', NULL, '',
  1, 0, 'C', '0', '0', 'manage:companyDeptMapping:list', 'share',
  'admin', NOW(), 'admin', NOW(), '单位中心-外部单位映射'
FROM dual
WHERE @unit_center_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'manage/companyDeptMapping/index');

UPDATE sys_menu
SET parent_id = @unit_center_menu_id, order_num = 1, menu_name = '本地单位管理', remark = '单位中心-本地单位管理'
WHERE @unit_center_menu_id IS NOT NULL
  AND component = 'system/dept/index';

UPDATE sys_menu
SET parent_id = @unit_center_menu_id, order_num = 2, menu_name = '合同管理', remark = '单位中心-合同管理'
WHERE @unit_center_menu_id IS NOT NULL
  AND component = 'system/contract/index';

UPDATE sys_menu
SET parent_id = @unit_center_menu_id, order_num = 4, menu_name = '外部单位映射', remark = '单位中心-外部单位映射'
WHERE @unit_center_menu_id IS NOT NULL
  AND component = 'manage/companyDeptMapping/index';

UPDATE sys_menu
SET parent_id = @unit_center_menu_id, order_num = 3, menu_name = '外部单位管理', remark = '单位中心-外部单位管理'
WHERE @unit_center_menu_id IS NOT NULL
  AND component = 'manage/externalCompany/index';

SET @external_company_menu_id := (
  SELECT menu_id FROM sys_menu WHERE component = 'manage/externalCompany/index' ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @external_company_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '外部单位查询' AS menu_name, 1 AS order_num, 'manage:externalCompany:query' AS perms, '外部单位管理-查询' AS remark
  UNION ALL SELECT '外部单位新增', 2, 'manage:externalCompany:add', '外部单位管理-新增'
  UNION ALL SELECT '外部单位修改', 3, 'manage:externalCompany:edit', '外部单位管理-修改'
  UNION ALL SELECT '外部单位导出', 4, 'manage:externalCompany:export', '外部单位管理-导出'
  UNION ALL SELECT '外部单位并档', 5, 'manage:externalCompany:merge', '外部单位管理-并档'
) t
WHERE @external_company_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @external_company_menu_id AND s.perms = t.perms);

SET @governance_role_sort := IFNULL((SELECT MAX(role_sort) + 1 FROM sys_role), 10);
INSERT INTO sys_role (`role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
SELECT '设备治理专员', 'device_governance_specialist', @governance_role_sort, '4', 1, 1, '0', '0', 'admin', NOW(), '设备人工施工台账和外部单位治理角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'device_governance_specialist');

SET @governance_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'device_governance_specialist' ORDER BY role_id LIMIT 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (
    m.path = 'unitCenter'
    OR m.component IN ('manage/externalCompany/index')
    OR m.perms IN (
      'manage:externalCompany:list',
      'manage:externalCompany:query',
      'manage:externalCompany:add',
      'manage:externalCompany:edit',
      'manage:externalCompany:export',
      'manage:externalCompany:merge'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @governance_role_id, m.menu_id
FROM sys_menu m
WHERE @governance_role_id IS NOT NULL
  AND (
    m.path = 'unitCenter'
    OR m.component IN ('manage/externalCompany/index', 'manage/companyDeptMapping/index')
    OR m.perms IN (
      'manage:externalCompany:list',
      'manage:externalCompany:query',
      'manage:externalCompany:add',
      'manage:externalCompany:edit',
      'manage:externalCompany:export',
      'manage:externalCompany:merge',
      'manage:companyDeptMapping:list',
      'manage:companyDeptMapping:query'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @governance_role_id AND rm.menu_id = m.menu_id);

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE58D95E4BD8DE4B8ADE5BF83 USING utf8mb4),
  remark = CONVERT(0xE58D95E4BD8DE6B2BBE79086E7BB9FE4B880E585A5E58FA3 USING utf8mb4)
WHERE path = 'unitCenter' AND menu_type = 'M';

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE69CACE59CB0E58D95E4BD8DE7AEA1E79086 USING utf8mb4),
  remark = CONVERT(0xE58D95E4BD8DE4B8ADE5BF832DE69CACE59CB0E58D95E4BD8DE7AEA1E79086 USING utf8mb4)
WHERE component = 'system/dept/index';

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE59088E5908CE7AEA1E79086 USING utf8mb4),
  remark = CONVERT(0xE58D95E4BD8DE4B8ADE5BF832DE59088E5908CE7AEA1E79086 USING utf8mb4)
WHERE component = 'system/contract/index';

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE5A496E983A8E58D95E4BD8DE7AEA1E79086 USING utf8mb4),
  remark = CONVERT(0xE58D95E4BD8DE4B8ADE5BF832DE5A496E983A8E58D95E4BD8DE7AEA1E79086 USING utf8mb4)
WHERE component = 'manage/externalCompany/index';

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE5A496E983A8E58D95E4BD8DE698A0E5B084 USING utf8mb4),
  remark = CONVERT(0xE58D95E4BD8DE4B8ADE5BF832DE5A496E983A8E58D95E4BD8DE698A0E5B084 USING utf8mb4)
WHERE component = 'manage/companyDeptMapping/index';

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE5A496E983A8E58D95E4BD8DE69FA5E8AFA2 USING utf8mb4),
  remark = CONVERT(0xE5A496E983A8E58D95E4BD8DE7AEA1E790862DE69FA5E8AFA2 USING utf8mb4)
WHERE perms = 'manage:externalCompany:query';

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE5A496E983A8E58D95E4BD8DE696B0E5A29E USING utf8mb4),
  remark = CONVERT(0xE5A496E983A8E58D95E4BD8DE7AEA1E790862DE696B0E5A29E USING utf8mb4)
WHERE perms = 'manage:externalCompany:add';

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE5A496E983A8E58D95E4BD8DE4BFAEE694B9 USING utf8mb4),
  remark = CONVERT(0xE5A496E983A8E58D95E4BD8DE7AEA1E790862DE4BFAEE694B9 USING utf8mb4)
WHERE perms = 'manage:externalCompany:edit';

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE5A496E983A8E58D95E4BD8DE5AFBCE587BA USING utf8mb4),
  remark = CONVERT(0xE5A496E983A8E58D95E4BD8DE7AEA1E790862DE5AFBCE587BA USING utf8mb4)
WHERE perms = 'manage:externalCompany:export';

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE5A496E983A8E58D95E4BD8DE5B9B6E6A1A3 USING utf8mb4),
  remark = CONVERT(0xE5A496E983A8E58D95E4BD8DE7AEA1E790862DE5B9B6E6A1A3 USING utf8mb4)
WHERE perms = 'manage:externalCompany:merge';
