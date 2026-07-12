-- 历史手工空壳单位清理预览（dev）
-- 只读口径：仅识别平台根直属、整棵子树均为 manual、且全库单位引用为 0 的手工单位树。

DROP TEMPORARY TABLE IF EXISTS tmp_dept_reference_20260710;
CREATE TEMPORARY TABLE tmp_dept_reference_20260710 (
    dept_id BIGINT NOT NULL PRIMARY KEY
);

-- 系统、合同与权限引用。
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM sys_dept_api_config WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM sys_user WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM sys_role_dept WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM sys_notice_dept WHERE dept_id IS NOT NULL;

-- SDK 来源、历史映射与同步日志引用。
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_api_config_company_scope WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_company_dept_mapping WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT last_source_dept_id FROM fe_external_company WHERE last_source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_sdk_sync_log WHERE dept_id IS NOT NULL;

-- 设备主数据、快照、历史与人工台账引用。
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_fire_point WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_fire_point WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_fire_point_device_snapshot WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_fire_point_device_snapshot WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_gateway WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_gateway WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_gateway_gps_history WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_gateway_manual_record WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_gateway_manual_record WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_sensor WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_sensor WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_sensor_manual_record WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_extinguisher WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_extinguisher WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_iot_card WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_iot_card WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_alarm_record WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_device_warning WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_device_warning WHERE source_dept_id IS NOT NULL;

-- 外出拜访相关单位引用；target_id 只在 target_type = contract_dept 时代表单位。
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT applicant_dept_id FROM fe_visit_apply WHERE applicant_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT contract_dept_id FROM fe_visit_apply WHERE contract_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT operator_dept_id FROM fe_visit_apply_log WHERE operator_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_visit_approve_config WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_visit_customer WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT owner_dept_id FROM fe_visit_owner_assign WHERE owner_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT target_id FROM fe_visit_owner_assign WHERE target_type = 'contract_dept' AND target_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_visit_passive_event WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT selected_target_id FROM fe_visit_passive_event WHERE selected_target_type = 'contract_dept' AND selected_target_id IS NOT NULL;

DROP TEMPORARY TABLE IF EXISTS tmp_empty_manual_root_20260710;
CREATE TEMPORARY TABLE tmp_empty_manual_root_20260710 (
    dept_id BIGINT NOT NULL PRIMARY KEY
);

INSERT INTO tmp_empty_manual_root_20260710 (dept_id)
SELECT r.dept_id
FROM sys_dept r
INNER JOIN sys_dept platform_root
        ON platform_root.dept_id = r.parent_id
       AND platform_root.dept_source = 'platform_root'
       AND platform_root.del_flag = '0'
WHERE r.dept_source = 'manual'
  AND r.del_flag = '0'
  -- 子树中不得存在 SDK 镜像、平台根或其他非手工来源节点。
  AND NOT EXISTS (
      SELECT 1
      FROM sys_dept n
      WHERE n.del_flag = '0'
        AND (n.dept_id = r.dept_id OR FIND_IN_SET(r.dept_id, n.ancestors))
        AND n.dept_source <> 'manual'
  )
  -- 子树中任何节点只要被任一业务表引用，整棵树就不进入自动清理。
  AND NOT EXISTS (
      SELECT 1
      FROM sys_dept n
      INNER JOIN tmp_dept_reference_20260710 ref ON ref.dept_id = n.dept_id
      WHERE n.del_flag = '0'
        AND (n.dept_id = r.dept_id OR FIND_IN_SET(r.dept_id, n.ancestors))
  );

DROP TEMPORARY TABLE IF EXISTS tmp_empty_manual_target_20260710;
CREATE TEMPORARY TABLE tmp_empty_manual_target_20260710 (
    dept_id BIGINT NOT NULL PRIMARY KEY,
    root_dept_id BIGINT NOT NULL
);

INSERT INTO tmp_empty_manual_target_20260710 (dept_id, root_dept_id)
SELECT n.dept_id, r.dept_id
FROM tmp_empty_manual_root_20260710 r
INNER JOIN sys_dept n
        ON n.dept_id = r.dept_id
        OR FIND_IN_SET(r.dept_id, n.ancestors)
WHERE n.dept_source = 'manual'
  AND n.del_flag = '0';

SELECT 'empty_manual_tree_summary' AS check_item,
       r.dept_id AS root_dept_id,
       root_dept.dept_name AS root_dept_name,
       COUNT(t.dept_id) AS candidate_node_count
FROM tmp_empty_manual_root_20260710 r
INNER JOIN sys_dept root_dept ON root_dept.dept_id = r.dept_id
INNER JOIN tmp_empty_manual_target_20260710 t ON t.root_dept_id = r.dept_id
GROUP BY r.dept_id, root_dept.dept_name
ORDER BY r.dept_id;

SELECT 'empty_manual_tree_candidates' AS check_item,
       t.root_dept_id,
       t.dept_id,
       d.dept_name,
       d.parent_id,
       d.ancestors,
       d.create_time
FROM tmp_empty_manual_target_20260710 t
INNER JOIN sys_dept d ON d.dept_id = t.dept_id
ORDER BY t.root_dept_id, LENGTH(d.ancestors), d.order_num, d.dept_id;

SELECT 'blocked_manual_roots' AS check_item,
       r.dept_id,
       r.dept_name,
       COUNT(DISTINCT n.dept_id) AS active_node_count,
       COUNT(DISTINCT CASE WHEN n.dept_source <> 'manual' THEN n.dept_id END) AS non_manual_node_count,
       COUNT(DISTINCT ref.dept_id) AS referenced_node_count
FROM sys_dept r
INNER JOIN sys_dept platform_root
        ON platform_root.dept_id = r.parent_id
       AND platform_root.dept_source = 'platform_root'
       AND platform_root.del_flag = '0'
INNER JOIN sys_dept n
        ON n.del_flag = '0'
       AND (n.dept_id = r.dept_id OR FIND_IN_SET(r.dept_id, n.ancestors))
LEFT JOIN tmp_dept_reference_20260710 ref ON ref.dept_id = n.dept_id
WHERE r.dept_source = 'manual'
  AND r.del_flag = '0'
  AND NOT EXISTS (SELECT 1 FROM tmp_empty_manual_root_20260710 c WHERE c.dept_id = r.dept_id)
GROUP BY r.dept_id, r.dept_name
ORDER BY r.dept_id;

DROP TEMPORARY TABLE IF EXISTS tmp_empty_manual_target_20260710;
DROP TEMPORARY TABLE IF EXISTS tmp_empty_manual_root_20260710;
DROP TEMPORARY TABLE IF EXISTS tmp_dept_reference_20260710;
