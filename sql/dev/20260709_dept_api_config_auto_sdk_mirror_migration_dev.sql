-- 合同/API 凭证自动迁移到 SDK 镜像授权节点（dev）
-- 口径：
-- 1. 只处理已通过 SDK 观测到外部单位、且能唯一识别顶层授权主体的配置。
-- 2. 目标必须是 sys_dept.dept_source = 'sdk_company' 的镜像节点。
-- 3. 若目标 SDK 镜像节点已被其他合同/API 配置占用，则跳过，避免误迁。
-- 4. 同步修正 fe_api_config_company_scope.source_dept_id 与 fe_external_company.last_source_dept_id。

DROP TEMPORARY TABLE IF EXISTS tmp_api_config_sdk_source_migration;

CREATE TEMPORARY TABLE tmp_api_config_sdk_source_migration AS
SELECT root.config_id,
       root.old_dept_id,
       sdk.dept_id AS target_dept_id,
       root.root_external_company_id,
       sdk.dept_name AS target_dept_name
FROM (
    SELECT candidate.config_id,
           candidate.old_dept_id,
           MIN(candidate.external_company_id) AS root_external_company_id,
           COUNT(DISTINCT candidate.external_company_id) AS root_count
    FROM (
        SELECT s.config_id,
               c.dept_id AS old_dept_id,
               ec.external_company_id
        FROM fe_api_config_company_scope s
        INNER JOIN sys_dept_api_config c ON s.config_id = c.config_id
        INNER JOIN fe_external_company ec ON s.external_company_id = ec.external_company_id
        WHERE s.external_company_id IS NOT NULL
          AND (
              ec.parent_external_company_id IS NULL
              OR ec.parent_external_company_id = ec.external_company_id
              OR NOT EXISTS (
                  SELECT 1
                  FROM fe_api_config_company_scope ps
                  WHERE ps.config_id = s.config_id
                    AND ps.external_company_id = ec.parent_external_company_id
              )
          )
    ) candidate
    GROUP BY candidate.config_id, candidate.old_dept_id
    HAVING root_count = 1
) root
INNER JOIN sys_dept old_dept ON old_dept.dept_id = root.old_dept_id
INNER JOIN sys_dept sdk
        ON sdk.external_company_id = root.root_external_company_id
       AND sdk.dept_source = 'sdk_company'
       AND sdk.del_flag = '0'
LEFT JOIN sys_dept_api_config occupied
       ON occupied.dept_id = sdk.dept_id
      AND occupied.config_id <> root.config_id
WHERE IFNULL(old_dept.dept_source, 'manual') <> 'sdk_company'
  AND old_dept.dept_id <> sdk.dept_id
  AND occupied.config_id IS NULL;

SELECT 'preview_api_config_sdk_source_migration' AS check_item,
       m.config_id,
       m.old_dept_id,
       old_dept.dept_name AS old_dept_name,
       m.target_dept_id,
       m.target_dept_name,
       m.root_external_company_id
FROM tmp_api_config_sdk_source_migration m
LEFT JOIN sys_dept old_dept ON m.old_dept_id = old_dept.dept_id
ORDER BY m.config_id;

UPDATE sys_dept_api_config c
INNER JOIN tmp_api_config_sdk_source_migration m ON c.config_id = m.config_id
SET c.dept_id = m.target_dept_id,
    c.update_by = 'sql:auto-sdk-mirror',
    c.update_time = NOW();

UPDATE fe_api_config_company_scope s
INNER JOIN tmp_api_config_sdk_source_migration m
        ON s.config_id = m.config_id
       AND s.source_dept_id = m.old_dept_id
SET s.source_dept_id = m.target_dept_id,
    s.update_by = 'sql:auto-sdk-mirror',
    s.update_time = NOW();

UPDATE fe_external_company ec
INNER JOIN fe_api_config_company_scope s ON ec.external_company_id = s.external_company_id
INNER JOIN tmp_api_config_sdk_source_migration m
        ON s.config_id = m.config_id
       AND ec.last_source_dept_id = m.old_dept_id
SET ec.last_source_dept_id = m.target_dept_id,
    ec.update_by = 'sql:auto-sdk-mirror',
    ec.update_time = NOW();

SELECT 'after_api_config_sdk_source_migration' AS check_item,
       c.config_id,
       c.dept_id,
       d.dept_name,
       d.dept_source,
       d.external_company_id
FROM sys_dept_api_config c
INNER JOIN tmp_api_config_sdk_source_migration m ON c.config_id = m.config_id
LEFT JOIN sys_dept d ON c.dept_id = d.dept_id
ORDER BY c.config_id;

DROP TEMPORARY TABLE IF EXISTS tmp_api_config_sdk_source_migration;
