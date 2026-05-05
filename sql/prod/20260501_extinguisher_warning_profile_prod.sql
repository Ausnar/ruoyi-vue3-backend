-- Extinguisher warning profile fields.
-- These fields are local cached warning profile facts derived from SDK/raw label-code data plus manual fallback.

SET @add_extinguisher_type_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'extinguisher_type'),
    'SELECT ''skip fe_extinguisher.extinguisher_type''',
    'ALTER TABLE fe_extinguisher ADD COLUMN extinguisher_type VARCHAR(32) NULL COMMENT ''Extinguisher type: water_based/dry_powder/clean_gas/co2'' AFTER specification'
);
PREPARE stmt FROM @add_extinguisher_type_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_extinguisher_form_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'extinguisher_form'),
    'SELECT ''skip fe_extinguisher.extinguisher_form''',
    'ALTER TABLE fe_extinguisher ADD COLUMN extinguisher_form VARCHAR(32) NULL COMMENT ''Extinguisher form: portable/wheeled'' AFTER extinguisher_type'
);
PREPARE stmt FROM @add_extinguisher_form_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_standard_code_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'standard_code'),
    'SELECT ''skip fe_extinguisher.standard_code''',
    'ALTER TABLE fe_extinguisher ADD COLUMN standard_code VARCHAR(32) NULL COMMENT ''Standard code'' AFTER extinguisher_form'
);
PREPARE stmt FROM @add_standard_code_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_temperature_range_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'temperature_range'),
    'SELECT ''skip fe_extinguisher.temperature_range''',
    'ALTER TABLE fe_extinguisher ADD COLUMN temperature_range VARCHAR(32) NULL COMMENT ''Temperature range'' AFTER standard_code'
);
PREPARE stmt FROM @add_temperature_range_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_profile_source_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'profile_source'),
    'SELECT ''skip fe_extinguisher.profile_source''',
    'ALTER TABLE fe_extinguisher ADD COLUMN profile_source VARCHAR(32) NULL COMMENT ''Profile source: sdk/derived/manual/mixed'' AFTER temperature_range'
);
PREPARE stmt FROM @add_profile_source_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_profile_sync_time_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'profile_sync_time'),
    'SELECT ''skip fe_extinguisher.profile_sync_time''',
    'ALTER TABLE fe_extinguisher ADD COLUMN profile_sync_time DATETIME NULL COMMENT ''Profile sync time'' AFTER profile_source'
);
PREPARE stmt FROM @add_profile_sync_time_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_profile_sync_status_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'profile_sync_status'),
    'SELECT ''skip fe_extinguisher.profile_sync_status''',
    'ALTER TABLE fe_extinguisher ADD COLUMN profile_sync_status VARCHAR(32) NULL COMMENT ''Profile sync status: success/failed/incomplete'' AFTER profile_sync_time'
);
PREPARE stmt FROM @add_profile_sync_status_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_profile_sync_message_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'profile_sync_message'),
    'SELECT ''skip fe_extinguisher.profile_sync_message''',
    'ALTER TABLE fe_extinguisher ADD COLUMN profile_sync_message VARCHAR(500) NULL COMMENT ''Profile sync message'' AFTER profile_sync_status'
);
PREPARE stmt FROM @add_profile_sync_message_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_extinguisher_type_index_sql = IF(
    EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fe_extinguisher' AND INDEX_NAME = 'idx_extinguisher_type'),
    'SELECT ''skip idx_extinguisher_type''',
    'CREATE INDEX idx_extinguisher_type ON fe_extinguisher (extinguisher_type)'
);
PREPARE stmt FROM @add_extinguisher_type_index_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE fe_extinguisher
   SET extinguisher_type = CASE
        WHEN concat_ws(' ', specification, product_name) LIKE '%水基%'
          OR lower(concat_ws(' ', specification, product_name)) LIKE '%water%' THEN 'water_based'
        WHEN concat_ws(' ', specification, product_name) LIKE '%干粉%'
          OR lower(concat_ws(' ', specification, product_name)) REGEXP '(^|[^a-z0-9])mf'
          OR lower(concat_ws(' ', specification, product_name)) LIKE '%abc%' THEN 'dry_powder'
        WHEN concat_ws(' ', specification, product_name) LIKE '%洁净气体%'
          OR concat_ws(' ', specification, product_name) LIKE '%洁净%' THEN 'clean_gas'
        WHEN concat_ws(' ', specification, product_name) LIKE '%二氧化碳%'
          OR lower(concat_ws(' ', specification, product_name)) LIKE '%co2%'
          OR lower(concat_ws(' ', specification, product_name)) REGEXP '(^|[^a-z0-9])mt' THEN 'co2'
        ELSE extinguisher_type
       END
 WHERE extinguisher_type IS NULL OR extinguisher_type = '';

UPDATE fe_extinguisher
   SET extinguisher_form = CASE
        WHEN concat_ws(' ', specification, product_name) LIKE '%推车%'
          OR lower(concat_ws(' ', specification, product_name)) REGEXP '(^|[^a-z0-9])mft' THEN 'wheeled'
        WHEN concat_ws(' ', specification, product_name) LIKE '%手提%'
          OR lower(concat_ws(' ', specification, product_name)) REGEXP '(^|[^a-z0-9])mf'
          OR lower(concat_ws(' ', specification, product_name)) REGEXP '(^|[^a-z0-9])mt' THEN 'portable'
        ELSE extinguisher_form
       END
 WHERE extinguisher_form IS NULL OR extinguisher_form = '';

UPDATE fe_extinguisher
   SET standard_code = CASE
        WHEN extinguisher_form = 'portable' AND production_date >= '2025-01-01' THEN 'GB 4351-2023'
        WHEN extinguisher_form = 'portable' AND production_date < '2025-01-01' THEN 'GB 4351.1-2005'
        WHEN extinguisher_form = 'wheeled' AND production_date >= '2025-01-01' THEN 'GB 8109-2023'
        WHEN extinguisher_form = 'wheeled' AND production_date < '2025-01-01' THEN 'GB 8109-2005'
        ELSE standard_code
       END
 WHERE production_date IS NOT NULL
   AND extinguisher_form IS NOT NULL
   AND extinguisher_form != ''
   AND (standard_code IS NULL OR standard_code = '');

UPDATE fe_extinguisher
   SET expiry_date = CASE
        WHEN extinguisher_type = 'water_based' THEN date_add(production_date, interval 6 year)
        WHEN extinguisher_type IN ('dry_powder', 'clean_gas') THEN date_add(production_date, interval 10 year)
        WHEN extinguisher_type = 'co2' THEN date_add(production_date, interval 12 year)
        ELSE expiry_date
       END
 WHERE production_date IS NOT NULL
   AND extinguisher_type IS NOT NULL
   AND extinguisher_type != ''
   AND expiry_date IS NULL;

UPDATE fe_extinguisher
   SET profile_sync_time = coalesce(profile_sync_time, now()),
       profile_source = CASE
        WHEN temperature_range IS NOT NULL AND temperature_range != '' THEN 'mixed'
        ELSE 'derived'
       END,
       profile_sync_status = CASE
        WHEN production_date IS NOT NULL
         AND extinguisher_type IS NOT NULL AND extinguisher_type != ''
         AND extinguisher_form IS NOT NULL AND extinguisher_form != '' THEN 'success'
        ELSE 'incomplete'
       END,
       profile_sync_message = CASE
        WHEN production_date IS NOT NULL
         AND extinguisher_type IS NOT NULL AND extinguisher_type != ''
         AND extinguisher_form IS NOT NULL AND extinguisher_form != '' THEN 'Historical profile backfilled by local rules'
        ELSE 'Historical profile is incomplete'
       END
 WHERE profile_source IS NULL OR profile_source = '';
