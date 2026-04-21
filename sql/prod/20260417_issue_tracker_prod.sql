SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `fe_issue_project` (
  `project_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(128) NOT NULL COMMENT '项目名称',
  `project_code` varchar(64) NOT NULL COMMENT '项目编码',
  `status` varchar(32) NOT NULL DEFAULT 'active' COMMENT '状态(active/inactive)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `uk_issue_project_code` (`project_code`, `del_flag`),
  UNIQUE KEY `uk_issue_project_name` (`project_name`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题事项项目表';

CREATE TABLE IF NOT EXISTS `fe_issue_project_module` (
  `module_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目模块ID',
  `project_id` bigint(20) NOT NULL COMMENT '所属项目ID',
  `module_name` varchar(128) NOT NULL COMMENT '模块名称',
  `status` varchar(32) NOT NULL DEFAULT 'active' COMMENT '状态(active/inactive)',
  `sort_order` int(11) NOT NULL DEFAULT '1' COMMENT '排序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`module_id`),
  UNIQUE KEY `uk_issue_project_module_name` (`project_id`, `module_name`, `del_flag`),
  KEY `idx_issue_project_module_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题事项项目模块表';

CREATE TABLE IF NOT EXISTS `fe_issue_item` (
  `issue_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '问题事项ID',
  `project_id` bigint(20) NOT NULL COMMENT '所属项目ID',
  `module_id` bigint(20) NOT NULL COMMENT '所属模块ID',
  `module_name_snapshot` varchar(128) NOT NULL COMMENT '模块名称快照',
  `issue_title` varchar(500) NOT NULL COMMENT '问题标题',
  `priority` varchar(16) NOT NULL COMMENT '优先级(P1/P2/P3)',
  `status` varchar(32) NOT NULL COMMENT '状态(pending/in_progress/resolved/closed)',
  `owner_side` varchar(32) NOT NULL COMMENT '责任侧(frontend/backend/fullstack/other)',
  `current_summary` varchar(1000) NOT NULL COMMENT '当前说明',
  `status_detail` varchar(1000) DEFAULT NULL COMMENT '状态说明',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`issue_id`),
  KEY `idx_issue_item_project` (`project_id`),
  KEY `idx_issue_item_module` (`module_id`),
  KEY `idx_issue_item_status` (`status`),
  KEY `idx_issue_item_priority` (`priority`),
  KEY `idx_issue_item_owner_side` (`owner_side`),
  KEY `idx_issue_item_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题事项主表';

CREATE TABLE IF NOT EXISTS `fe_issue_item_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `issue_id` bigint(20) NOT NULL COMMENT '问题事项ID',
  `from_status` varchar(32) DEFAULT NULL COMMENT '原状态',
  `to_status` varchar(32) DEFAULT NULL COMMENT '新状态',
  `from_owner_side` varchar(32) DEFAULT NULL COMMENT '原责任侧',
  `to_owner_side` varchar(32) DEFAULT NULL COMMENT '新责任侧',
  `change_summary` varchar(1000) DEFAULT NULL COMMENT '变更说明',
  `operator` varchar(64) DEFAULT '' COMMENT '操作人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_issue_item_log_issue` (`issue_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题事项状态日志表';

DELIMITER $$

DROP PROCEDURE IF EXISTS `proc_issue_tracker_upgrade`$$
CREATE PROCEDURE `proc_issue_tracker_upgrade`()
BEGIN
  DECLARE v_default_project_id BIGINT DEFAULT NULL;

  INSERT INTO `fe_issue_project` (
    `project_name`, `project_code`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`
  )
  SELECT '安众道后台系统', 'azd-admin', 'active', '问题事项模块默认项目', 'admin', NOW(), 'admin', NOW(), '0'
  FROM dual
  WHERE NOT EXISTS (
    SELECT 1 FROM `fe_issue_project` WHERE `project_code` = 'azd-admin' AND `del_flag` = '0'
  );

  IF EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'fe_issue_item'
      AND COLUMN_NAME = 'module_id'
  ) = 0 THEN
    ALTER TABLE `fe_issue_item`
      ADD COLUMN `module_id` bigint(20) NULL COMMENT '所属模块ID' AFTER `project_id`;
  END IF;

  IF EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'fe_issue_item'
      AND COLUMN_NAME = 'module_name_snapshot'
  ) = 0 THEN
    ALTER TABLE `fe_issue_item`
      ADD COLUMN `module_name_snapshot` varchar(128) NULL COMMENT '模块名称快照' AFTER `module_id`;
  END IF;

  IF EXISTS (
    SELECT 1
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'fe_issue_item'
      AND INDEX_NAME = 'idx_issue_item_module'
  ) = 0 THEN
    ALTER TABLE `fe_issue_item`
      ADD KEY `idx_issue_item_module` (`module_id`);
  END IF;

  IF EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'fe_issue_item'
      AND COLUMN_NAME = 'module_name'
  ) THEN
    SET @issue_module_seed_sql = '
      INSERT INTO fe_issue_project_module (
        project_id, module_name, status, sort_order, remark,
        create_by, create_time, update_by, update_time, del_flag
      )
      SELECT DISTINCT
        i.project_id,
        i.module_name,
        ''active'',
        100,
        ''由历史问题事项迁移生成'',
        ''admin'',
        NOW(),
        ''admin'',
        NOW(),
        ''0''
      FROM fe_issue_item i
      LEFT JOIN fe_issue_project_module m
        ON m.project_id = i.project_id
       AND m.module_name = i.module_name
       AND m.del_flag = ''0''
      WHERE i.del_flag = ''0''
        AND IFNULL(i.module_name, '''') <> ''''
        AND m.module_id IS NULL
    ';
    PREPARE stmt FROM @issue_module_seed_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @issue_module_fill_sql = '
      UPDATE fe_issue_item i
      JOIN fe_issue_project_module m
        ON m.project_id = i.project_id
       AND m.module_name = i.module_name
       AND m.del_flag = ''0''
      SET i.module_id = IFNULL(i.module_id, m.module_id),
          i.module_name_snapshot = IF(
            IFNULL(i.module_name_snapshot, '''') = '''',
            m.module_name,
            i.module_name_snapshot
          )
      WHERE i.del_flag = ''0''
        AND (
          i.module_id IS NULL
          OR IFNULL(i.module_name_snapshot, '''') = ''''
        )
    ';
    PREPARE stmt FROM @issue_module_fill_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  INSERT INTO `fe_issue_project_module` (
    `project_id`, `module_name`, `status`, `sort_order`, `remark`,
    `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`
  )
  SELECT DISTINCT
    i.project_id,
    i.module_name_snapshot,
    'active',
    100,
    '由历史模块快照回补生成',
    'admin',
    NOW(),
    'admin',
    NOW(),
    '0'
  FROM `fe_issue_item` i
  LEFT JOIN `fe_issue_project_module` m
    ON m.project_id = i.project_id
   AND m.module_name = i.module_name_snapshot
   AND m.del_flag = '0'
  WHERE i.del_flag = '0'
    AND IFNULL(i.module_name_snapshot, '') <> ''
    AND m.module_id IS NULL;

  UPDATE `fe_issue_item` i
  JOIN `fe_issue_project_module` m
    ON m.project_id = i.project_id
   AND m.module_name = i.module_name_snapshot
   AND m.del_flag = '0'
  SET i.module_id = m.module_id
  WHERE i.del_flag = '0'
    AND i.module_id IS NULL
    AND IFNULL(i.module_name_snapshot, '') <> '';

  UPDATE `fe_issue_item` i
  JOIN `fe_issue_project_module` m
    ON m.module_id = i.module_id
   AND m.del_flag = '0'
  SET i.module_name_snapshot = m.module_name
  WHERE i.del_flag = '0'
    AND (
      IFNULL(i.module_name_snapshot, '') = ''
      OR i.module_name_snapshot <> m.module_name
    );

  SET v_default_project_id = (
    SELECT `project_id`
    FROM `fe_issue_project`
    WHERE `project_code` = 'azd-admin' AND `del_flag` = '0'
    ORDER BY `project_id`
    LIMIT 1
  );

  IF v_default_project_id IS NOT NULL THEN
    INSERT INTO `fe_issue_project_module` (
      `project_id`, `module_name`, `status`, `sort_order`, `remark`,
      `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`
    )
    SELECT v_default_project_id, t.module_name, 'active', t.sort_order, '默认项目模块', 'admin', NOW(), 'admin', NOW(), '0'
    FROM (
      SELECT '登录授权' AS module_name, 1 AS sort_order
      UNION ALL SELECT '单位管理', 2
      UNION ALL SELECT '邀请分配', 3
      UNION ALL SELECT '设备绑定', 4
      UNION ALL SELECT '地图与统计', 5
      UNION ALL SELECT '传感器详情', 6
      UNION ALL SELECT '智能问答', 7
      UNION ALL SELECT '其他', 99
    ) t
    WHERE NOT EXISTS (
      SELECT 1
      FROM `fe_issue_project_module` m
      WHERE m.`project_id` = v_default_project_id
        AND m.`module_name` = t.module_name
        AND m.`del_flag` = '0'
    );
  END IF;

  INSERT INTO `fe_issue_project_module` (
    `project_id`, `module_name`, `status`, `sort_order`, `remark`,
    `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`
  )
  SELECT DISTINCT
    i.`project_id`, '其他', 'active', 999, '问题事项迁移兜底模块', 'admin', NOW(), 'admin', NOW(), '0'
  FROM `fe_issue_item` i
  LEFT JOIN `fe_issue_project_module` m
    ON m.`project_id` = i.`project_id`
   AND m.`module_name` = '其他'
   AND m.`del_flag` = '0'
  WHERE i.`del_flag` = '0'
    AND i.`module_id` IS NULL
    AND m.`module_id` IS NULL;

  ALTER TABLE `fe_issue_project`
    MODIFY COLUMN `project_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    MODIFY COLUMN `project_name` varchar(128) NOT NULL COMMENT '项目名称',
    MODIFY COLUMN `project_code` varchar(64) NOT NULL COMMENT '项目编码',
    MODIFY COLUMN `status` varchar(32) NOT NULL DEFAULT 'active' COMMENT '状态(active/inactive)',
    MODIFY COLUMN `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    MODIFY COLUMN `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    MODIFY COLUMN `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 2删除)',
    COMMENT = '问题事项项目表';

  ALTER TABLE `fe_issue_project_module`
    MODIFY COLUMN `module_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目模块ID',
    MODIFY COLUMN `project_id` bigint(20) NOT NULL COMMENT '所属项目ID',
    MODIFY COLUMN `module_name` varchar(128) NOT NULL COMMENT '模块名称',
    MODIFY COLUMN `status` varchar(32) NOT NULL DEFAULT 'active' COMMENT '状态(active/inactive)',
    MODIFY COLUMN `sort_order` int(11) NOT NULL DEFAULT '1' COMMENT '排序',
    MODIFY COLUMN `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    MODIFY COLUMN `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    MODIFY COLUMN `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 2删除)',
    COMMENT = '问题事项项目模块表';

  UPDATE `fe_issue_item`
  SET `module_name_snapshot` = '其他'
  WHERE IFNULL(`module_name_snapshot`, '') = '';

  IF EXISTS (
    SELECT 1
    FROM `fe_issue_project_module` m
    JOIN `fe_issue_item` i
      ON i.`project_id` = m.`project_id`
     AND i.`module_name_snapshot` = m.`module_name`
     AND i.`del_flag` = '0'
    WHERE i.`module_id` IS NULL
      AND m.`del_flag` = '0'
  ) THEN
    UPDATE `fe_issue_item` i
    JOIN `fe_issue_project_module` m
      ON m.`project_id` = i.`project_id`
     AND m.`module_name` = i.`module_name_snapshot`
     AND m.`del_flag` = '0'
    SET i.`module_id` = m.`module_id`
    WHERE i.`del_flag` = '0'
      AND i.`module_id` IS NULL;
  END IF;

  UPDATE `fe_issue_item` i
  JOIN `fe_issue_project_module` m
    ON m.`project_id` = i.`project_id`
   AND m.`module_name` = '其他'
   AND m.`del_flag` = '0'
  SET i.`module_id` = m.`module_id`,
      i.`module_name_snapshot` = '其他'
  WHERE i.`del_flag` = '0'
    AND i.`module_id` IS NULL;

  ALTER TABLE `fe_issue_item`
    MODIFY COLUMN `issue_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '问题事项ID',
    MODIFY COLUMN `project_id` bigint(20) NOT NULL COMMENT '所属项目ID',
    MODIFY COLUMN `module_id` bigint(20) NOT NULL COMMENT '所属模块ID',
    MODIFY COLUMN `module_name_snapshot` varchar(128) NOT NULL COMMENT '模块名称快照',
    MODIFY COLUMN `issue_title` varchar(500) NOT NULL COMMENT '问题标题',
    MODIFY COLUMN `priority` varchar(16) NOT NULL COMMENT '优先级(P1/P2/P3)',
    MODIFY COLUMN `status` varchar(32) NOT NULL COMMENT '状态(pending/in_progress/resolved/closed)',
    MODIFY COLUMN `owner_side` varchar(32) NOT NULL COMMENT '责任侧(frontend/backend/fullstack/other)',
    MODIFY COLUMN `current_summary` varchar(1000) NOT NULL COMMENT '当前说明',
    MODIFY COLUMN `status_detail` varchar(1000) DEFAULT NULL COMMENT '状态说明',
    MODIFY COLUMN `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    MODIFY COLUMN `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    MODIFY COLUMN `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 2删除)',
    COMMENT = '问题事项主表';

  ALTER TABLE `fe_issue_item_log`
    MODIFY COLUMN `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    MODIFY COLUMN `issue_id` bigint(20) NOT NULL COMMENT '问题事项ID',
    MODIFY COLUMN `from_status` varchar(32) DEFAULT NULL COMMENT '原状态',
    MODIFY COLUMN `to_status` varchar(32) DEFAULT NULL COMMENT '新状态',
    MODIFY COLUMN `from_owner_side` varchar(32) DEFAULT NULL COMMENT '原责任侧',
    MODIFY COLUMN `to_owner_side` varchar(32) DEFAULT NULL COMMENT '新责任侧',
    MODIFY COLUMN `change_summary` varchar(1000) DEFAULT NULL COMMENT '变更说明',
    MODIFY COLUMN `operator` varchar(64) DEFAULT '' COMMENT '操作人',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    COMMENT = '问题事项状态日志表';

  IF EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'fe_issue_item'
      AND COLUMN_NAME = 'module_name'
  ) THEN
    ALTER TABLE `fe_issue_item`
      DROP COLUMN `module_name`;
  END IF;
END$$

CALL `proc_issue_tracker_upgrade`()$$
DROP PROCEDURE IF EXISTS `proc_issue_tracker_upgrade`$$

DELIMITER ;

SET @issue_parent_order := IFNULL((SELECT MAX(order_num) + 1 FROM sys_menu WHERE parent_id = 0), 10);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '问题事项', 0, @issue_parent_order, 'issueCenter', NULL, NULL, '',
  1, 0, 'M', '0', '0', '', 'tickets',
  'admin', NOW(), 'admin', NOW(), '多项目问题事项管理入口'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'issueCenter' AND menu_type = 'M');

SET @issue_parent_menu_id := (
  SELECT menu_id FROM sys_menu WHERE path = 'issueCenter' AND menu_type = 'M' ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '项目管理', @issue_parent_menu_id, 1, 'project', 'issue/project/index', NULL, '',
  1, 0, 'C', '0', '0', 'issue:project:list', 'folder-opened',
  'admin', NOW(), 'admin', NOW(), '问题事项-项目管理'
FROM dual
WHERE @issue_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'issue/project/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '项目模块管理', @issue_parent_menu_id, 2, 'module', 'issue/module/index', NULL, '',
  1, 0, 'C', '0', '0', 'issue:module:list', 'grid',
  'admin', NOW(), 'admin', NOW(), '问题事项-项目模块管理'
FROM dual
WHERE @issue_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'issue/module/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '问题台账', @issue_parent_menu_id, 3, 'item', 'issue/item/index', NULL, '',
  1, 0, 'C', '0', '0', 'issue:item:list', 'memo',
  'admin', NOW(), 'admin', NOW(), '问题事项-问题台账'
FROM dual
WHERE @issue_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'issue/item/index');

SET @issue_project_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'issue/project/index' ORDER BY menu_id LIMIT 1);
SET @issue_module_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'issue/module/index' ORDER BY menu_id LIMIT 1);
SET @issue_item_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'issue/item/index' ORDER BY menu_id LIMIT 1);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @issue_project_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '项目查询' AS menu_name, 1 AS order_num, 'issue:project:query' AS perms, '问题项目查询' AS remark
  UNION ALL SELECT '项目新增', 2, 'issue:project:add', '问题项目新增'
  UNION ALL SELECT '项目修改', 3, 'issue:project:edit', '问题项目修改'
  UNION ALL SELECT '项目导出', 4, 'issue:project:export', '问题项目导出'
) t
WHERE @issue_project_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @issue_project_menu_id AND s.perms = t.perms);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @issue_module_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '模块查询' AS menu_name, 1 AS order_num, 'issue:module:query' AS perms, '项目模块查询' AS remark
  UNION ALL SELECT '模块新增', 2, 'issue:module:add', '项目模块新增'
  UNION ALL SELECT '模块修改', 3, 'issue:module:edit', '项目模块修改'
  UNION ALL SELECT '模块导出', 4, 'issue:module:export', '项目模块导出'
) t
WHERE @issue_module_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @issue_module_menu_id AND s.perms = t.perms);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @issue_item_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '问题查询' AS menu_name, 1 AS order_num, 'issue:item:query' AS perms, '问题事项查询' AS remark
  UNION ALL SELECT '问题新增', 2, 'issue:item:add', '问题事项新增'
  UNION ALL SELECT '问题修改', 3, 'issue:item:edit', '问题事项修改'
  UNION ALL SELECT '问题删除', 4, 'issue:item:remove', '问题事项删除'
  UNION ALL SELECT '问题导出', 5, 'issue:item:export', '问题事项导出'
  UNION ALL SELECT '查看日志', 6, 'issue:item:viewLog', '问题事项日志查看'
) t
WHERE @issue_item_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @issue_item_menu_id AND s.perms = t.perms);

UPDATE `fe_issue_project`
SET `project_name` = '安众道后台系统',
    `remark` = '问题事项模块默认项目'
WHERE `project_code` = 'azd-admin' AND `del_flag` = '0';

UPDATE `sys_menu`
SET `menu_name` = '问题事项',
    `remark` = '多项目问题事项管理入口'
WHERE `path` = 'issueCenter' AND `menu_type` = 'M';

UPDATE `sys_menu`
SET `parent_id` = @issue_parent_menu_id,
    `order_num` = 1,
    `path` = 'project',
    `component` = 'issue/project/index',
    `menu_name` = '项目管理',
    `perms` = 'issue:project:list',
    `icon` = 'folder-opened',
    `remark` = '问题事项-项目管理'
WHERE `component` = 'issue/project/index';

UPDATE `sys_menu`
SET `parent_id` = @issue_parent_menu_id,
    `order_num` = 2,
    `path` = 'module',
    `component` = 'issue/module/index',
    `menu_name` = '项目模块管理',
    `perms` = 'issue:module:list',
    `icon` = 'grid',
    `remark` = '问题事项-项目模块管理'
WHERE `component` = 'issue/module/index';

UPDATE `sys_menu`
SET `parent_id` = @issue_parent_menu_id,
    `order_num` = 3,
    `path` = 'item',
    `component` = 'issue/item/index',
    `menu_name` = '问题台账',
    `perms` = 'issue:item:list',
    `icon` = 'memo',
    `remark` = '问题事项-问题台账'
WHERE `component` = 'issue/item/index';

UPDATE `sys_menu` SET `menu_name` = '项目查询', `remark` = '问题项目查询' WHERE `perms` = 'issue:project:query';
UPDATE `sys_menu` SET `menu_name` = '项目新增', `remark` = '问题项目新增' WHERE `perms` = 'issue:project:add';
UPDATE `sys_menu` SET `menu_name` = '项目修改', `remark` = '问题项目修改' WHERE `perms` = 'issue:project:edit';
UPDATE `sys_menu` SET `menu_name` = '项目导出', `remark` = '问题项目导出' WHERE `perms` = 'issue:project:export';

UPDATE `sys_menu` SET `menu_name` = '模块查询', `remark` = '项目模块查询' WHERE `perms` = 'issue:module:query';
UPDATE `sys_menu` SET `menu_name` = '模块新增', `remark` = '项目模块新增' WHERE `perms` = 'issue:module:add';
UPDATE `sys_menu` SET `menu_name` = '模块修改', `remark` = '项目模块修改' WHERE `perms` = 'issue:module:edit';
UPDATE `sys_menu` SET `menu_name` = '模块导出', `remark` = '项目模块导出' WHERE `perms` = 'issue:module:export';

UPDATE `sys_menu` SET `menu_name` = '问题查询', `remark` = '问题事项查询' WHERE `perms` = 'issue:item:query';
UPDATE `sys_menu` SET `menu_name` = '问题新增', `remark` = '问题事项新增' WHERE `perms` = 'issue:item:add';
UPDATE `sys_menu` SET `menu_name` = '问题修改', `remark` = '问题事项修改' WHERE `perms` = 'issue:item:edit';
UPDATE `sys_menu` SET `menu_name` = '问题删除', `remark` = '问题事项删除' WHERE `perms` = 'issue:item:remove';
UPDATE `sys_menu` SET `menu_name` = '问题导出', `remark` = '问题事项导出' WHERE `perms` = 'issue:item:export';
UPDATE `sys_menu` SET `menu_name` = '查看日志', `remark` = '问题事项日志查看' WHERE `perms` = 'issue:item:viewLog';

SET @issue_role_sort := IFNULL((SELECT MAX(role_sort) + 1 FROM sys_role), 10);

INSERT INTO sys_role (
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT
  '问题事项维护员', 'issue_item_manager', @issue_role_sort, '1', 1, 1,
  '0', '0', 'admin', NOW(), '问题事项模块维护角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'issue_item_manager');

UPDATE `sys_role`
SET `role_name` = '问题事项维护员',
    `remark` = '问题事项模块维护角色'
WHERE `role_key` = 'issue_item_manager';

SET @issue_role_id := (
  SELECT role_id FROM sys_role WHERE role_key = 'issue_item_manager' ORDER BY role_id LIMIT 1
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (
    m.path = 'issueCenter'
    OR m.component IN ('issue/project/index', 'issue/module/index', 'issue/item/index')
    OR m.perms IN (
      'issue:project:list', 'issue:project:query', 'issue:project:add', 'issue:project:edit', 'issue:project:export',
      'issue:module:list', 'issue:module:query', 'issue:module:add', 'issue:module:edit', 'issue:module:export',
      'issue:item:list', 'issue:item:query', 'issue:item:add', 'issue:item:edit', 'issue:item:remove', 'issue:item:export', 'issue:item:viewLog'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @issue_role_id, m.menu_id
FROM sys_menu m
WHERE @issue_role_id IS NOT NULL
  AND (
    m.path = 'issueCenter'
    OR m.component IN ('issue/project/index', 'issue/module/index', 'issue/item/index')
    OR m.perms IN (
      'issue:project:list', 'issue:project:query', 'issue:project:add', 'issue:project:edit', 'issue:project:export',
      'issue:module:list', 'issue:module:query', 'issue:module:add', 'issue:module:edit', 'issue:module:export',
      'issue:item:list', 'issue:item:query', 'issue:item:add', 'issue:item:edit', 'issue:item:remove', 'issue:item:export', 'issue:item:viewLog'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @issue_role_id AND rm.menu_id = m.menu_id);
