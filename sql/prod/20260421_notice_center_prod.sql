-- 公告中心与发布范围：管理页 + 阅读页分离
-- 基线库：ruoyi
-- 说明：
-- 1. system/notice/index 菜单重命名为“公告管理”
-- 2. 新增面向全体登录用户的“公告中心”入口
-- 3. 为 sys_notice 增加“发布范围类型”
-- 4. 新增 sys_notice_dept 维护公告与本地单位范围关系
-- 5. 指定单位发布的生效口径为“所选本地单位 + 下级单位”

SET @notice_publish_scope_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_notice'
    AND COLUMN_NAME = 'publish_scope_type'
);

SET @notice_publish_scope_sql := IF(
  @notice_publish_scope_exists = 0,
  'ALTER TABLE `sys_notice` ADD COLUMN `publish_scope_type` char(1) NOT NULL DEFAULT ''1'' COMMENT ''发布范围类型（1全体 2指定部门）'' AFTER `status`',
  'SELECT 1'
);
PREPARE stmt_notice_publish_scope FROM @notice_publish_scope_sql;
EXECUTE stmt_notice_publish_scope;
DEALLOCATE PREPARE stmt_notice_publish_scope;

UPDATE `sys_notice`
SET `publish_scope_type` = '1'
WHERE `publish_scope_type` IS NULL OR `publish_scope_type` = '';

CREATE TABLE IF NOT EXISTS `sys_notice_dept` (
  `notice_id` int(4) NOT NULL COMMENT '公告ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`notice_id`, `dept_id`),
  KEY `idx_notice_dept_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告发布范围部门表';

SET @notice_manage_name := CONVERT(UNHEX('E585ACE5918AE7AEA1E79086') USING utf8mb4);
SET @notice_manage_remark := CONVERT(UNHEX('E7B3BBE7BB9FE585ACE5918AE7BBB4E68AA4E585A5E58FA3') USING utf8mb4);
SET @notice_center_name := CONVERT(UNHEX('E585ACE5918AE4B8ADE5BF83') USING utf8mb4);
SET @notice_center_remark := CONVERT(UNHEX('E585A8E4BD93E799BBE5BD95E794A8E688B7E585ACE5918AE99885E8AFBBE585A5E58FA3') USING utf8mb4);
SET @notice_publish_scope_comment := CONVERT(UNHEX('E58F91E5B883E88C83E59BB4E7B1BBE59E8BEFBC8831E585A8E4BD932032E68C87E5AE9AE983A8E997A8EFBC89') USING utf8mb4);
SET @notice_dept_table_comment := CONVERT(UNHEX('E585ACE5918AE58F91E5B883E88C83E59BB4E983A8E997A8E8A1A8') USING utf8mb4);
SET @notice_id_comment := CONVERT(UNHEX('E585ACE5918A4944') USING utf8mb4);
SET @dept_id_comment := CONVERT(UNHEX('E983A8E997A84944') USING utf8mb4);

SET @notice_publish_scope_comment_sql := CONCAT(
  'ALTER TABLE `sys_notice` MODIFY COLUMN `publish_scope_type` char(1) NOT NULL DEFAULT ''1'' COMMENT ''',
  @notice_publish_scope_comment,
  ''''
);
PREPARE stmt_notice_publish_scope_comment FROM @notice_publish_scope_comment_sql;
EXECUTE stmt_notice_publish_scope_comment;
DEALLOCATE PREPARE stmt_notice_publish_scope_comment;

SET @notice_dept_table_comment_sql := CONCAT(
  'ALTER TABLE `sys_notice_dept` COMMENT = ''',
  @notice_dept_table_comment,
  ''''
);
PREPARE stmt_notice_dept_table_comment FROM @notice_dept_table_comment_sql;
EXECUTE stmt_notice_dept_table_comment;
DEALLOCATE PREPARE stmt_notice_dept_table_comment;

SET @notice_dept_notice_id_comment_sql := CONCAT(
  'ALTER TABLE `sys_notice_dept` MODIFY COLUMN `notice_id` int(4) NOT NULL COMMENT ''',
  @notice_id_comment,
  ''''
);
PREPARE stmt_notice_dept_notice_id_comment FROM @notice_dept_notice_id_comment_sql;
EXECUTE stmt_notice_dept_notice_id_comment;
DEALLOCATE PREPARE stmt_notice_dept_notice_id_comment;

SET @notice_dept_dept_id_comment_sql := CONCAT(
  'ALTER TABLE `sys_notice_dept` MODIFY COLUMN `dept_id` bigint(20) NOT NULL COMMENT ''',
  @dept_id_comment,
  ''''
);
PREPARE stmt_notice_dept_dept_id_comment FROM @notice_dept_dept_id_comment_sql;
EXECUTE stmt_notice_dept_dept_id_comment;
DEALLOCATE PREPARE stmt_notice_dept_dept_id_comment;

UPDATE `sys_menu`
SET `menu_name` = @notice_manage_name,
    `remark` = @notice_manage_remark
WHERE `component` = 'system/notice/index';

SET @notice_center_menu_id := (
  SELECT `menu_id`
  FROM `sys_menu`
  WHERE `component` = 'notice/center/index'
  ORDER BY `menu_id`
  LIMIT 1
);

SET @notice_center_order := IFNULL((SELECT MAX(CAST(`order_num` AS UNSIGNED)) + 1 FROM `sys_menu` WHERE `parent_id` = 0), 20);

INSERT INTO `sys_menu` (
  `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`,
  `create_by`, `create_time`, `update_by`, `update_time`, `remark`
)
SELECT
  @notice_center_name, 0, @notice_center_order, 'noticeCenter', 'notice/center/index', NULL, 'NoticeCenter',
  1, 0, 'C', '0', '0', '', 'message',
  'admin', NOW(), 'admin', NOW(), @notice_center_remark
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `component` = 'notice/center/index'
);

SET @notice_center_menu_id := (
  SELECT `menu_id`
  FROM `sys_menu`
  WHERE `component` = 'notice/center/index'
  ORDER BY `menu_id`
  LIMIT 1
);

UPDATE `sys_menu`
SET `menu_name` = @notice_center_name,
    `parent_id` = 0,
    `path` = 'noticeCenter',
    `component` = 'notice/center/index',
    `route_name` = 'NoticeCenter',
    `is_frame` = 1,
    `is_cache` = 0,
    `menu_type` = 'C',
    `visible` = '0',
    `status` = '0',
    `perms` = '',
    `icon` = 'message',
    `remark` = @notice_center_remark,
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `menu_id` = @notice_center_menu_id;

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.`role_id`, @notice_center_menu_id
FROM `sys_role` r
WHERE @notice_center_menu_id IS NOT NULL
  AND r.`del_flag` = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu` rm
    WHERE rm.`role_id` = r.`role_id`
      AND rm.`menu_id` = @notice_center_menu_id
  );
