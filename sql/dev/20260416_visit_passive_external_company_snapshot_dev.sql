SET NAMES utf8mb4;

SET @schema_name := DATABASE();

SET @sql := IF(
  EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'fe_visit_passive_event'
      AND COLUMN_NAME = 'selected_external_company_id'
  ),
  'SELECT 1',
  "ALTER TABLE `fe_visit_passive_event`
    ADD COLUMN `selected_external_company_id` bigint(20) DEFAULT NULL COMMENT 'selected external company id' AFTER `selected_target_id`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'fe_visit_passive_event'
      AND COLUMN_NAME = 'selected_external_company_name'
  ),
  'SELECT 1',
  "ALTER TABLE `fe_visit_passive_event`
    ADD COLUMN `selected_external_company_name` varchar(200) DEFAULT NULL COMMENT 'selected external company name' AFTER `selected_external_company_id`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'fe_visit_apply'
      AND COLUMN_NAME = 'trigger_external_company_id'
  ),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply`
    ADD COLUMN `trigger_external_company_id` bigint(20) DEFAULT NULL COMMENT 'trigger external company id' AFTER `trigger_distance_m`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'fe_visit_apply'
      AND COLUMN_NAME = 'trigger_external_company_name_snapshot'
  ),
  'SELECT 1',
  "ALTER TABLE `fe_visit_apply`
    ADD COLUMN `trigger_external_company_name_snapshot` varchar(200) DEFAULT NULL COMMENT 'trigger external company name snapshot' AFTER `trigger_external_company_id`"
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
