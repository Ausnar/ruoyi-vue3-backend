-- 合同管理新增合同性质字段
-- 基线库：ruoyi
-- 说明：
-- 1. 为 sys_dept_api_config 增加 contract_type，用于区分试用合同和正式付费合同
-- 2. 历史数据先允许为空，后续在管理页逐步补录
-- 3. 新增 idx_contract_type，支持合同管理与分析页按合同性质筛选

SET @contract_type_column_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_dept_api_config'
    AND COLUMN_NAME = 'contract_type'
);

SET @contract_type_column_sql := IF(
  @contract_type_column_exists = 0,
  'ALTER TABLE `sys_dept_api_config` ADD COLUMN `contract_type` varchar(16) NULL DEFAULT NULL COMMENT ''合同性质(trial/paid)'' AFTER `contract_no`',
  'SELECT 1'
);
PREPARE stmt_contract_type_column FROM @contract_type_column_sql;
EXECUTE stmt_contract_type_column;
DEALLOCATE PREPARE stmt_contract_type_column;

SET @contract_type_index_exists := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_dept_api_config'
    AND INDEX_NAME = 'idx_contract_type'
);

SET @contract_type_index_sql := IF(
  @contract_type_index_exists = 0,
  'ALTER TABLE `sys_dept_api_config` ADD INDEX `idx_contract_type` (`contract_type`)',
  'SELECT 1'
);
PREPARE stmt_contract_type_index FROM @contract_type_index_sql;
EXECUTE stmt_contract_type_index;
DEALLOCATE PREPARE stmt_contract_type_index;
