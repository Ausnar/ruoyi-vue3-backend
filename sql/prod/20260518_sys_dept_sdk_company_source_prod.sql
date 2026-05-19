-- Sys dept SDK company source binding fields.
-- These fields let local business depts mirror SDK company tree without manual mapping maintenance.

SET @add_dept_source_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dept' AND COLUMN_NAME = 'dept_source'),
    'SELECT ''skip sys_dept.dept_source''',
    'ALTER TABLE sys_dept ADD COLUMN dept_source VARCHAR(32) NULL DEFAULT ''manual'' COMMENT ''Dept source: platform_root/sdk_company/manual'' AFTER latitude'
);
PREPARE stmt FROM @add_dept_source_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_external_company_id_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dept' AND COLUMN_NAME = 'external_company_id'),
    'SELECT ''skip sys_dept.external_company_id''',
    'ALTER TABLE sys_dept ADD COLUMN external_company_id BIGINT NULL COMMENT ''SDK external company ID'' AFTER dept_source'
);
PREPARE stmt FROM @add_external_company_id_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_external_parent_company_id_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dept' AND COLUMN_NAME = 'external_parent_company_id'),
    'SELECT ''skip sys_dept.external_parent_company_id''',
    'ALTER TABLE sys_dept ADD COLUMN external_parent_company_id BIGINT NULL COMMENT ''SDK external parent company ID'' AFTER external_company_id'
);
PREPARE stmt FROM @add_external_parent_company_id_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_external_org_path_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dept' AND COLUMN_NAME = 'external_org_path'),
    'SELECT ''skip sys_dept.external_org_path''',
    'ALTER TABLE sys_dept ADD COLUMN external_org_path VARCHAR(500) NULL COMMENT ''SDK external company org path'' AFTER external_parent_company_id'
);
PREPARE stmt FROM @add_external_org_path_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_source_api_config_id_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dept' AND COLUMN_NAME = 'source_api_config_id'),
    'SELECT ''skip sys_dept.source_api_config_id''',
    'ALTER TABLE sys_dept ADD COLUMN source_api_config_id BIGINT NULL COMMENT ''Latest source API config ID'' AFTER external_org_path'
);
PREPARE stmt FROM @add_source_api_config_id_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_last_company_sync_time_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dept' AND COLUMN_NAME = 'last_company_sync_time'),
    'SELECT ''skip sys_dept.last_company_sync_time''',
    'ALTER TABLE sys_dept ADD COLUMN last_company_sync_time DATETIME NULL COMMENT ''Latest SDK company sync time'' AFTER source_api_config_id'
);
PREPARE stmt FROM @add_last_company_sync_time_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_sys_dept_external_company_index_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dept' AND INDEX_NAME = 'uk_sys_dept_external_company_id'),
    'SELECT ''skip uk_sys_dept_external_company_id''',
    'CREATE UNIQUE INDEX uk_sys_dept_external_company_id ON sys_dept (external_company_id)'
);
PREPARE stmt FROM @add_sys_dept_external_company_index_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_sys_dept_source_index_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dept' AND INDEX_NAME = 'idx_sys_dept_dept_source'),
    'SELECT ''skip idx_sys_dept_dept_source''',
    'CREATE INDEX idx_sys_dept_dept_source ON sys_dept (dept_source)'
);
PREPARE stmt FROM @add_sys_dept_source_index_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_sys_dept_source_api_config_index_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dept' AND INDEX_NAME = 'idx_sys_dept_source_api_config_id'),
    'SELECT ''skip idx_sys_dept_source_api_config_id''',
    'CREATE INDEX idx_sys_dept_source_api_config_id ON sys_dept (source_api_config_id)'
);
PREPARE stmt FROM @add_sys_dept_source_api_config_index_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE sys_dept
   SET dept_source = 'platform_root'
 WHERE dept_id = 100
   AND (dept_source IS NULL OR dept_source = '' OR dept_source = 'manual');

