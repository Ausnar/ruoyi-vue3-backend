-- 合同/API 凭证迁移回滚到原手工单位
-- 执行库：保底库 / 开发样例库 ruoyi_ai_dev
-- 仅用于回滚 20260524_dept_api_config_sdk_mirror_migration_dev.sql

DROP TEMPORARY TABLE IF EXISTS tmp_dept_api_config_sdk_migration_rollback;
CREATE TEMPORARY TABLE tmp_dept_api_config_sdk_migration_rollback (
  config_id BIGINT NOT NULL PRIMARY KEY,
  old_dept_id BIGINT NOT NULL,
  target_dept_id BIGINT NOT NULL
) ENGINE=MEMORY;

INSERT INTO tmp_dept_api_config_sdk_migration_rollback
  (config_id, old_dept_id, target_dept_id)
VALUES
  (10, 201, 265),
  (11, 202, 272),
  (12, 203, 276),
  (14, 206, 281),
  (13, 209, 283),
  (17, 212, 287);

SELECT '回滚前校验' AS check_item,
       m.config_id,
       m.old_dept_id,
       old_dept.dept_name AS old_dept_name,
       m.target_dept_id,
       target_dept.dept_name AS target_dept_name,
       c.dept_id AS current_dept_id,
       other.config_id AS old_dept_occupied_by_config_id
FROM tmp_dept_api_config_sdk_migration_rollback m
LEFT JOIN sys_dept_api_config c ON c.config_id = m.config_id
LEFT JOIN sys_dept old_dept ON old_dept.dept_id = m.old_dept_id
LEFT JOIN sys_dept target_dept ON target_dept.dept_id = m.target_dept_id
LEFT JOIN sys_dept_api_config other
       ON other.dept_id = m.old_dept_id
      AND other.config_id <> m.config_id
WHERE c.config_id IS NULL
   OR c.dept_id NOT IN (m.old_dept_id, m.target_dept_id)
   OR old_dept.dept_id IS NULL
   OR other.config_id IS NOT NULL;

UPDATE sys_dept_api_config c
JOIN tmp_dept_api_config_sdk_migration_rollback m ON m.config_id = c.config_id
JOIN sys_dept old_dept ON old_dept.dept_id = m.old_dept_id
LEFT JOIN sys_dept_api_config other
       ON other.dept_id = m.old_dept_id
      AND other.config_id <> m.config_id
SET c.dept_id = m.old_dept_id,
    c.update_by = 'rollback:20260524',
    c.update_time = NOW()
WHERE c.dept_id = m.target_dept_id
  AND other.config_id IS NULL;

UPDATE fe_api_config_company_scope s
JOIN tmp_dept_api_config_sdk_migration_rollback m ON m.config_id = s.config_id
JOIN sys_dept_api_config c
     ON c.config_id = m.config_id
    AND c.dept_id = m.old_dept_id
SET s.source_dept_id = m.old_dept_id,
    s.update_by = 'rollback:20260524',
    s.update_time = NOW()
WHERE s.source_dept_id = m.target_dept_id;

SELECT '回滚结果' AS check_item,
       m.config_id,
       c.dept_id AS current_dept_id,
       d.dept_name AS current_dept_name,
       COUNT(s.scope_id) AS scope_count,
       SUM(CASE WHEN s.source_dept_id = m.old_dept_id THEN 1 ELSE 0 END) AS rollback_scope_count
FROM tmp_dept_api_config_sdk_migration_rollback m
LEFT JOIN sys_dept_api_config c ON c.config_id = m.config_id
LEFT JOIN sys_dept d ON d.dept_id = c.dept_id
LEFT JOIN fe_api_config_company_scope s ON s.config_id = m.config_id
GROUP BY m.config_id, c.dept_id, d.dept_name
ORDER BY m.config_id;

DROP TEMPORARY TABLE IF EXISTS tmp_dept_api_config_sdk_migration_rollback;
