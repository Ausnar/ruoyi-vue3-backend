-- 手工单位清理预览（prod）
-- 只读脚本：用于识别可清理的历史手工单位，不修改数据。

DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_cleanup_target;

CREATE TEMPORARY TABLE tmp_manual_dept_cleanup_target (
    dept_id BIGINT NOT NULL PRIMARY KEY,
    target_dept_id BIGINT NULL,
    cleanup_category VARCHAR(64) NOT NULL
);

-- 1. 有唯一同名 SDK 镜像、没有子节点、没有关键业务引用的手工叶子节点。
INSERT INTO tmp_manual_dept_cleanup_target (dept_id, target_dept_id, cleanup_category)
SELECT m.dept_id,
       sdk.dept_id AS target_dept_id,
       'leaf_unique_sdk_mirror' AS cleanup_category
FROM sys_dept m
INNER JOIN sys_dept sdk
        ON sdk.dept_source = 'sdk_company'
       AND sdk.del_flag = '0'
       AND sdk.dept_name = m.dept_name
WHERE m.dept_source = 'manual'
  AND m.del_flag = '0'
  AND (SELECT COUNT(1) FROM sys_dept c WHERE c.parent_id = m.dept_id AND c.del_flag = '0') = 0
  AND (SELECT COUNT(1) FROM sys_dept s WHERE s.dept_source = 'sdk_company' AND s.del_flag = '0' AND s.dept_name = m.dept_name) = 1
  AND (SELECT COUNT(1) FROM sys_dept_api_config x WHERE x.dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_api_config_company_scope x WHERE x.source_dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_fire_point x WHERE x.dept_id = m.dept_id OR x.source_dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_gateway x WHERE x.dept_id = m.dept_id OR x.source_dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_sensor x WHERE x.dept_id = m.dept_id OR x.source_dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_extinguisher x WHERE x.dept_id = m.dept_id OR x.source_dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_device_warning x WHERE x.dept_id = m.dept_id OR x.source_dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM sys_user x WHERE x.dept_id = m.dept_id AND x.del_flag = '0') = 0
  AND (SELECT COUNT(1) FROM sys_role_dept x WHERE x.dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM sys_notice_dept x WHERE x.dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_gateway_manual_record x WHERE x.dept_id = m.dept_id OR x.source_dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_sensor_manual_record x WHERE x.dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_visit_apply x WHERE x.applicant_dept_id = m.dept_id OR x.contract_dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_visit_apply_log x WHERE x.operator_dept_id = m.dept_id) = 0
  AND (SELECT COUNT(1) FROM fe_visit_approve_config x WHERE x.dept_id = m.dept_id) = 0;

-- 2. 整棵手工树无合同、设备、预警、用户、角色、公告、旧映射引用的空壳树。
DROP TEMPORARY TABLE IF EXISTS tmp_manual_empty_tree_root;

CREATE TEMPORARY TABLE tmp_manual_empty_tree_root AS
SELECT r.dept_id
FROM sys_dept r
WHERE r.dept_source = 'manual'
  AND r.del_flag = '0'
  AND r.parent_id = 100
  AND NOT EXISTS (
      SELECT 1
      FROM sys_dept n
      WHERE n.del_flag = '0'
        AND (n.dept_id = r.dept_id OR FIND_IN_SET(r.dept_id, n.ancestors))
        AND (
            EXISTS (SELECT 1 FROM sys_dept_api_config x WHERE x.dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_api_config_company_scope x WHERE x.source_dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_company_dept_mapping x WHERE x.dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_fire_point x WHERE x.dept_id = n.dept_id OR x.source_dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_gateway x WHERE x.dept_id = n.dept_id OR x.source_dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_sensor x WHERE x.dept_id = n.dept_id OR x.source_dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_extinguisher x WHERE x.dept_id = n.dept_id OR x.source_dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_device_warning x WHERE x.dept_id = n.dept_id OR x.source_dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM sys_user x WHERE x.dept_id = n.dept_id AND x.del_flag = '0')
            OR EXISTS (SELECT 1 FROM sys_role_dept x WHERE x.dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM sys_notice_dept x WHERE x.dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_gateway_manual_record x WHERE x.dept_id = n.dept_id OR x.source_dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_sensor_manual_record x WHERE x.dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_visit_apply x WHERE x.applicant_dept_id = n.dept_id OR x.contract_dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_visit_apply_log x WHERE x.operator_dept_id = n.dept_id)
            OR EXISTS (SELECT 1 FROM fe_visit_approve_config x WHERE x.dept_id = n.dept_id)
        )
  );

INSERT IGNORE INTO tmp_manual_dept_cleanup_target (dept_id, target_dept_id, cleanup_category)
SELECT n.dept_id,
       NULL AS target_dept_id,
       'empty_manual_tree' AS cleanup_category
FROM sys_dept n
INNER JOIN tmp_manual_empty_tree_root r
        ON n.dept_id = r.dept_id
        OR FIND_IN_SET(r.dept_id, n.ancestors)
WHERE n.dept_source = 'manual'
  AND n.del_flag = '0';

SELECT 'cleanup_candidates' AS check_item,
       t.cleanup_category,
       t.dept_id,
       d.dept_name,
       d.parent_id,
       p.dept_name AS parent_name,
       t.target_dept_id,
       sdk.dept_name AS target_dept_name,
       (SELECT COUNT(1) FROM fe_company_dept_mapping m WHERE m.dept_id = t.dept_id) AS old_mapping_refs
FROM tmp_manual_dept_cleanup_target t
INNER JOIN sys_dept d ON d.dept_id = t.dept_id
LEFT JOIN sys_dept p ON p.dept_id = d.parent_id
LEFT JOIN sys_dept sdk ON sdk.dept_id = t.target_dept_id
ORDER BY t.cleanup_category, t.dept_id;

SELECT 'blocked_manual_roots' AS check_item,
       r.dept_id,
       r.dept_name,
       r.parent_id,
       p.dept_name AS parent_name,
       (SELECT COUNT(1) FROM sys_dept n WHERE n.del_flag = '0' AND n.dept_source = 'manual' AND (n.dept_id = r.dept_id OR FIND_IN_SET(r.dept_id, n.ancestors))) AS manual_node_count,
       (SELECT COUNT(1) FROM fe_company_dept_mapping m WHERE m.dept_id IN (SELECT n.dept_id FROM sys_dept n WHERE n.del_flag = '0' AND (n.dept_id = r.dept_id OR FIND_IN_SET(r.dept_id, n.ancestors)))) AS old_mapping_refs,
       (SELECT COUNT(1) FROM sys_user u WHERE u.del_flag = '0' AND u.dept_id IN (SELECT n.dept_id FROM sys_dept n WHERE n.del_flag = '0' AND (n.dept_id = r.dept_id OR FIND_IN_SET(r.dept_id, n.ancestors)))) AS user_refs,
       (SELECT COUNT(1) FROM fe_device_warning w WHERE w.dept_id IN (SELECT n.dept_id FROM sys_dept n WHERE n.del_flag = '0' AND (n.dept_id = r.dept_id OR FIND_IN_SET(r.dept_id, n.ancestors))) OR w.source_dept_id IN (SELECT n.dept_id FROM sys_dept n WHERE n.del_flag = '0' AND (n.dept_id = r.dept_id OR FIND_IN_SET(r.dept_id, n.ancestors)))) AS warning_refs
FROM sys_dept r
LEFT JOIN sys_dept p ON p.dept_id = r.parent_id
WHERE r.dept_source = 'manual'
  AND r.del_flag = '0'
  AND r.parent_id = 100
  AND NOT EXISTS (SELECT 1 FROM tmp_manual_empty_tree_root x WHERE x.dept_id = r.dept_id)
  AND NOT EXISTS (SELECT 1 FROM tmp_manual_dept_cleanup_target t WHERE t.dept_id = r.dept_id)
ORDER BY r.dept_id;

DROP TEMPORARY TABLE IF EXISTS tmp_manual_empty_tree_root;
DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_cleanup_target;
