-- 合同/API 凭证迁移到 SDK 顶层授权主体 / 集团镜像节点
-- 执行库：保底库 / 开发样例库 ruoyi_ai_dev
-- 可重复执行：已迁移的数据会跳过；目标节点被其它配置占用时不会更新。

DROP TEMPORARY TABLE IF EXISTS tmp_dept_api_config_sdk_migration;
CREATE TEMPORARY TABLE tmp_dept_api_config_sdk_migration (
  config_id BIGINT NOT NULL PRIMARY KEY,
  old_dept_id BIGINT NOT NULL,
  target_dept_id BIGINT NOT NULL,
  target_external_company_id BIGINT NOT NULL,
  target_dept_name VARCHAR(100) NOT NULL
) ENGINE=MEMORY;

INSERT INTO tmp_dept_api_config_sdk_migration
  (config_id, old_dept_id, target_dept_id, target_external_company_id, target_dept_name)
VALUES
  (10, 201, 265, 3264, '苏州市公共交通集团有限公司'),
  (11, 202, 272, 3240, '无锡市公共交通集团有限公司'),
  (12, 203, 276, 46, '常州市公共交通集团有限责任公司'),
  (14, 206, 281, 3254, '北京威发新世纪信息技术有限公司'),
  (13, 209, 283, 14, '中国民航技术装备有限责任公司'),
  (17, 212, 287, 3267, '宸轩中消（北京）科技有限公司');

SELECT '待迁移配置校验' AS check_item,
       m.config_id,
       m.old_dept_id,
       old_dept.dept_name AS old_dept_name,
       m.target_dept_id,
       target_dept.dept_name AS target_dept_name,
       target_dept.dept_source AS target_dept_source,
       target_dept.external_company_id AS target_external_company_id,
       c.dept_id AS current_dept_id,
       other.config_id AS occupied_by_config_id
FROM tmp_dept_api_config_sdk_migration m
LEFT JOIN sys_dept_api_config c ON c.config_id = m.config_id
LEFT JOIN sys_dept old_dept ON old_dept.dept_id = m.old_dept_id
LEFT JOIN sys_dept target_dept ON target_dept.dept_id = m.target_dept_id
LEFT JOIN sys_dept_api_config other
       ON other.dept_id = m.target_dept_id
      AND other.config_id <> m.config_id
WHERE c.config_id IS NULL
   OR c.dept_id NOT IN (m.old_dept_id, m.target_dept_id)
   OR target_dept.dept_id IS NULL
   OR target_dept.dept_source <> 'sdk_company'
   OR target_dept.external_company_id <> m.target_external_company_id
   OR other.config_id IS NOT NULL;

UPDATE sys_dept_api_config c
JOIN tmp_dept_api_config_sdk_migration m ON m.config_id = c.config_id
JOIN sys_dept target_dept
     ON target_dept.dept_id = m.target_dept_id
    AND target_dept.dept_source = 'sdk_company'
    AND target_dept.external_company_id = m.target_external_company_id
LEFT JOIN sys_dept_api_config other
       ON other.dept_id = m.target_dept_id
      AND other.config_id <> m.config_id
SET c.dept_id = m.target_dept_id,
    c.update_by = 'migration:20260524',
    c.update_time = NOW()
WHERE c.dept_id = m.old_dept_id
  AND other.config_id IS NULL;

UPDATE fe_api_config_company_scope s
JOIN tmp_dept_api_config_sdk_migration m ON m.config_id = s.config_id
JOIN sys_dept_api_config c
     ON c.config_id = m.config_id
    AND c.dept_id = m.target_dept_id
SET s.source_dept_id = m.target_dept_id,
    s.update_by = 'migration:20260524',
    s.update_time = NOW()
WHERE s.source_dept_id = m.old_dept_id;

SELECT '迁移结果' AS check_item,
       m.config_id,
       c.dept_id AS current_dept_id,
       d.dept_name AS current_dept_name,
       d.dept_source AS current_dept_source,
       d.external_company_id,
       COUNT(s.scope_id) AS scope_count,
       SUM(CASE WHEN s.source_dept_id = m.target_dept_id THEN 1 ELSE 0 END) AS migrated_scope_count
FROM tmp_dept_api_config_sdk_migration m
LEFT JOIN sys_dept_api_config c ON c.config_id = m.config_id
LEFT JOIN sys_dept d ON d.dept_id = c.dept_id
LEFT JOIN fe_api_config_company_scope s ON s.config_id = m.config_id
GROUP BY m.config_id, c.dept_id, d.dept_name, d.dept_source, d.external_company_id
ORDER BY m.config_id;

DROP TEMPORARY TABLE IF EXISTS tmp_dept_api_config_sdk_migration;
