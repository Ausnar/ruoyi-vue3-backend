SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `fe_visit_customer` (
  `customer_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '独立客户ID',
  `customer_name` varchar(100) NOT NULL COMMENT '客户名称',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(30) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '客户地址',
  `dept_id` bigint(20) NOT NULL COMMENT '归属部门ID',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`customer_id`),
  KEY `idx_visit_customer_dept_status` (`dept_id`, `status`, `del_flag`),
  KEY `idx_visit_customer_name` (`customer_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外出拜访独立客户档案';

CREATE TABLE IF NOT EXISTS `fe_visit_approve_config` (
  `config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  `role_id` bigint(20) NOT NULL COMMENT '审批角色ID',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_visit_approve_config_dept` (`dept_id`),
  KEY `idx_visit_approve_config_role` (`role_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外出拜访部门审批角色配置';

CREATE TABLE IF NOT EXISTS `fe_visit_apply` (
  `visit_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '拜访单ID',
  `visit_no` varchar(32) NOT NULL COMMENT '拜访单号',
  `applicant_user_id` bigint(20) NOT NULL COMMENT '申请人用户ID',
  `applicant_dept_id` bigint(20) NOT NULL COMMENT '申请人部门ID',
  `customer_type` varchar(32) NOT NULL COMMENT '客户类型(contract/independent)',
  `contract_dept_id` bigint(20) DEFAULT NULL COMMENT '合同客户部门ID',
  `contract_config_id` bigint(20) DEFAULT NULL COMMENT '合同配置ID',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '独立客户ID',
  `customer_name_snapshot` varchar(100) NOT NULL COMMENT '客户名称快照',
  `contact_person_snapshot` varchar(50) DEFAULT NULL COMMENT '联系人快照',
  `contact_phone_snapshot` varchar(30) DEFAULT NULL COMMENT '联系电话快照',
  `visit_address` varchar(255) DEFAULT NULL COMMENT '拜访地址',
  `planned_start_time` datetime NOT NULL COMMENT '计划开始时间',
  `planned_end_time` datetime NOT NULL COMMENT '计划结束时间',
  `visit_reason` varchar(500) NOT NULL COMMENT '拜访事由',
  `visit_target` varchar(500) DEFAULT NULL COMMENT '拜访目标',
  `companion_members` varchar(255) DEFAULT NULL COMMENT '同行人员',
  `status` varchar(32) NOT NULL COMMENT '状态',
  `approve_role_id_snapshot` bigint(20) DEFAULT NULL COMMENT '审批角色快照ID',
  `approve_user_id` bigint(20) DEFAULT NULL COMMENT '审批人用户ID',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_comment` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  `result_desc` varchar(1000) DEFAULT NULL COMMENT '结果说明',
  `visit_conclusion` varchar(32) DEFAULT NULL COMMENT '拜访结论',
  `intention_level` varchar(32) DEFAULT NULL COMMENT '意向等级',
  `next_follow_time` datetime DEFAULT NULL COMMENT '下次跟进时间',
  `result_attachments` text COMMENT '结果附件(JSON数组)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`visit_id`),
  UNIQUE KEY `uk_visit_apply_visit_no` (`visit_no`),
  KEY `idx_visit_apply_user_status` (`applicant_user_id`, `status`, `del_flag`),
  KEY `idx_visit_apply_dept_status` (`applicant_dept_id`, `status`, `del_flag`),
  KEY `idx_visit_apply_customer` (`customer_type`, `contract_config_id`, `customer_id`),
  KEY `idx_visit_apply_planned_start` (`planned_start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外出拜访申请单';

CREATE TABLE IF NOT EXISTS `fe_visit_apply_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `visit_id` bigint(20) NOT NULL COMMENT '拜访单ID',
  `action_type` varchar(32) NOT NULL COMMENT '动作类型',
  `from_status` varchar(32) DEFAULT NULL COMMENT '原状态',
  `to_status` varchar(32) DEFAULT NULL COMMENT '目标状态',
  `operator_user_id` bigint(20) NOT NULL COMMENT '操作人用户ID',
  `operator_dept_id` bigint(20) DEFAULT NULL COMMENT '操作人部门ID',
  `action_time` datetime NOT NULL COMMENT '操作时间',
  `action_comment` varchar(500) DEFAULT NULL COMMENT '操作说明',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_visit_apply_log_visit` (`visit_id`, `action_time`),
  KEY `idx_visit_apply_log_operator` (`operator_user_id`, `action_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外出拜访流转日志';

INSERT INTO sys_dict_type (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT '外出拜访客户类型', 'fe_visit_customer_type', '0', 'admin', NOW(), '外出拜访客户类型'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'fe_visit_customer_type');

INSERT INTO sys_dict_type (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT '外出拜访申请状态', 'fe_visit_apply_status', '0', 'admin', NOW(), '外出拜访申请状态'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'fe_visit_apply_status');

INSERT INTO sys_dict_type (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT '外出拜访结论', 'fe_visit_conclusion', '0', 'admin', NOW(), '外出拜访结论'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'fe_visit_conclusion');

INSERT INTO sys_dict_type (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT '外出拜访意向等级', 'fe_visit_intention_level', '0', 'admin', NOW(), '外出拜访意向等级'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'fe_visit_intention_level');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 1, '合同客户', 'contract', 'fe_visit_customer_type', '', 'primary', 'Y', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_customer_type' AND dict_value = 'contract');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 2, '独立客户', 'independent', 'fe_visit_customer_type', '', 'success', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_customer_type' AND dict_value = 'independent');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 1, '待审批', 'pending_approve', 'fe_visit_apply_status', '', 'warning', 'Y', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_apply_status' AND dict_value = 'pending_approve');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 2, '已驳回', 'rejected', 'fe_visit_apply_status', '', 'danger', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_apply_status' AND dict_value = 'rejected');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 3, '已撤回', 'withdrawn', 'fe_visit_apply_status', '', 'info', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_apply_status' AND dict_value = 'withdrawn');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 4, '待回填', 'approved_pending_feedback', 'fe_visit_apply_status', '', 'primary', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_apply_status' AND dict_value = 'approved_pending_feedback');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 5, '已完成', 'completed', 'fe_visit_apply_status', '', 'success', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_apply_status' AND dict_value = 'completed');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 1, '达成合作', 'deal', 'fe_visit_conclusion', '', 'success', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_conclusion' AND dict_value = 'deal');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 2, '继续跟进', 'follow_up', 'fe_visit_conclusion', '', 'primary', 'Y', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_conclusion' AND dict_value = 'follow_up');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 3, '暂无意向', 'no_intent', 'fe_visit_conclusion', '', 'info', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_conclusion' AND dict_value = 'no_intent');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 4, '终止跟进', 'lost', 'fe_visit_conclusion', '', 'danger', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_conclusion' AND dict_value = 'lost');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 1, 'A', 'A', 'fe_visit_intention_level', '', 'danger', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_intention_level' AND dict_value = 'A');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 2, 'B', 'B', 'fe_visit_intention_level', '', 'warning', 'Y', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_intention_level' AND dict_value = 'B');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 3, 'C', 'C', 'fe_visit_intention_level', '', 'primary', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_intention_level' AND dict_value = 'C');

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 4, 'D', 'D', 'fe_visit_intention_level', '', 'info', 'N', '0', 'admin', NOW(), ''
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'fe_visit_intention_level' AND dict_value = 'D');

SET @visit_apply_role_sort := IFNULL((SELECT MAX(role_sort) + 1 FROM sys_role), 10);
INSERT INTO sys_role (`role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
SELECT '外出拜访申请', 'visit_apply_user', @visit_apply_role_sort, '4', 1, 1, '0', '0', 'admin', NOW(), '外出拜访申请角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'visit_apply_user');

SET @visit_approve_role_sort := IFNULL((SELECT MAX(role_sort) + 1 FROM sys_role), @visit_apply_role_sort + 1);
INSERT INTO sys_role (`role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
SELECT '外出拜访审批', 'visit_approver', @visit_approve_role_sort, '4', 1, 1, '0', '0', 'admin', NOW(), '外出拜访审批角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'visit_approver');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '外出拜访', 0, IFNULL((SELECT MAX(order_num) + 1 FROM sys_menu WHERE parent_id = 0), 10), 'visit', NULL, NULL, '',
  1, 0, 'M', '0', '0', '', 'guide',
  'admin', NOW(), 'admin', NOW(), '外出拜访目录'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'visit' AND menu_type = 'M');

SET @visit_parent_menu_id := (SELECT menu_id FROM sys_menu WHERE path = 'visit' AND menu_type = 'M' ORDER BY menu_id LIMIT 1);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '拜访申请', @visit_parent_menu_id, 1, 'apply', 'visit/apply/index', NULL, '',
  1, 0, 'C', '0', '0', 'visit:apply:list', 'editPen',
  'admin', NOW(), 'admin', NOW(), '拜访申请菜单'
FROM dual
WHERE @visit_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'visit/apply/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '拜访审批', @visit_parent_menu_id, 2, 'approve', 'visit/approve/index', NULL, '',
  1, 0, 'C', '0', '0', 'visit:apply:query', 'finished',
  'admin', NOW(), 'admin', NOW(), '拜访审批菜单'
FROM dual
WHERE @visit_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'visit/approve/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '独立客户档案', @visit_parent_menu_id, 3, 'customer', 'visit/customer/index', NULL, '',
  1, 0, 'C', '0', '0', 'visit:customer:list', 'user',
  'admin', NOW(), 'admin', NOW(), '独立客户档案菜单'
FROM dual
WHERE @visit_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'visit/customer/index');

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '审批角色配置', @visit_parent_menu_id, 4, 'approveConfig', 'visit/approveConfig/index', NULL, '',
  1, 0, 'C', '0', '0', 'visit:approveConfig:list', 'postcard',
  'admin', NOW(), 'admin', NOW(), '审批角色配置菜单'
FROM dual
WHERE @visit_parent_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'visit/approveConfig/index');

SET @visit_apply_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'visit/apply/index' ORDER BY menu_id LIMIT 1);
SET @visit_approve_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'visit/approve/index' ORDER BY menu_id LIMIT 1);
SET @visit_customer_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'visit/customer/index' ORDER BY menu_id LIMIT 1);
SET @visit_config_menu_id := (SELECT menu_id FROM sys_menu WHERE component = 'visit/approveConfig/index' ORDER BY menu_id LIMIT 1);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @visit_apply_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '拜访申请查询' AS menu_name, 1 AS order_num, 'visit:apply:query' AS perms, '拜访申请查询按钮' AS remark
  UNION ALL SELECT '拜访申请新增', 2, 'visit:apply:add', '拜访申请新增按钮'
  UNION ALL SELECT '拜访申请修改', 3, 'visit:apply:edit', '拜访申请修改按钮'
  UNION ALL SELECT '拜访申请撤回', 4, 'visit:apply:withdraw', '拜访申请撤回按钮'
  UNION ALL SELECT '拜访申请重提', 5, 'visit:apply:resubmit', '拜访申请重提按钮'
  UNION ALL SELECT '拜访结果回填', 6, 'visit:apply:feedback', '拜访结果回填按钮'
) t
WHERE @visit_apply_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @visit_apply_menu_id AND s.perms = t.perms);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @visit_approve_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '拜访审批查询' AS menu_name, 1 AS order_num, 'visit:apply:query' AS perms, '拜访审批查询按钮' AS remark
  UNION ALL SELECT '拜访审批通过', 2, 'visit:apply:approve', '拜访审批通过按钮'
  UNION ALL SELECT '拜访审批驳回', 3, 'visit:apply:reject', '拜访审批驳回按钮'
) t
WHERE @visit_approve_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @visit_approve_menu_id AND s.perms = t.perms);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @visit_customer_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '独立客户查询' AS menu_name, 1 AS order_num, 'visit:customer:query' AS perms, '独立客户查询按钮' AS remark
  UNION ALL SELECT '独立客户新增', 2, 'visit:customer:add', '独立客户新增按钮'
  UNION ALL SELECT '独立客户修改', 3, 'visit:customer:edit', '独立客户修改按钮'
  UNION ALL SELECT '独立客户删除', 4, 'visit:customer:remove', '独立客户删除按钮'
) t
WHERE @visit_customer_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @visit_customer_menu_id AND s.perms = t.perms);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @visit_config_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT '审批配置查询' AS menu_name, 1 AS order_num, 'visit:approveConfig:query' AS perms, '审批配置查询按钮' AS remark
  UNION ALL SELECT '审批配置新增', 2, 'visit:approveConfig:add', '审批配置新增按钮'
  UNION ALL SELECT '审批配置修改', 3, 'visit:approveConfig:edit', '审批配置修改按钮'
  UNION ALL SELECT '审批配置删除', 4, 'visit:approveConfig:remove', '审批配置删除按钮'
) t
WHERE @visit_config_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @visit_config_menu_id AND s.perms = t.perms);

SET @visit_apply_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'visit_apply_user' ORDER BY role_id LIMIT 1);
SET @visit_approver_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'visit_approver' ORDER BY role_id LIMIT 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (
    m.path = 'visit'
    OR m.component IN ('visit/apply/index', 'visit/approve/index', 'visit/customer/index', 'visit/approveConfig/index')
    OR m.perms LIKE 'visit:%'
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @visit_apply_role_id, m.menu_id
FROM sys_menu m
WHERE @visit_apply_role_id IS NOT NULL
  AND (
    m.path = 'visit'
    OR m.component IN ('visit/apply/index', 'visit/customer/index')
    OR m.perms IN (
      'visit:apply:query', 'visit:apply:add', 'visit:apply:edit', 'visit:apply:withdraw', 'visit:apply:resubmit', 'visit:apply:feedback',
      'visit:customer:query', 'visit:customer:add', 'visit:customer:edit', 'visit:customer:remove'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @visit_apply_role_id AND rm.menu_id = m.menu_id);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @visit_approver_role_id, m.menu_id
FROM sys_menu m
WHERE @visit_approver_role_id IS NOT NULL
  AND (
    m.path = 'visit'
    OR m.component IN ('visit/approve/index', 'visit/customer/index')
    OR m.perms IN (
      'visit:apply:query', 'visit:apply:approve', 'visit:apply:reject', 'visit:customer:query'
    )
  )
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @visit_approver_role_id AND rm.menu_id = m.menu_id);
