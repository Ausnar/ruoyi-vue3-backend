-- Reset SDK sync schema fragments in development database (MySQL 5.7 safe)
-- Usage:
--   1. Run this file in ruoyi_ai_dev
--   2. Then run 20260410_device_sdk_sync_dev.sql
-- Note: this only resets the SDK-related schema additions and tables.

DROP TABLE IF EXISTS fe_sdk_sync_log;
DROP TABLE IF EXISTS fe_api_config_company_scope;
DROP TABLE IF EXISTS fe_external_company;
DROP TABLE IF EXISTS fe_extinguisher_sensor_binding;
DROP TABLE IF EXISTS fe_gateway;

DELIMITER $$

DROP PROCEDURE IF EXISTS drop_column_if_exists $$
CREATE PROCEDURE drop_column_if_exists(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64)
)
BEGIN
    DECLARE v_count INT DEFAULT 0;
    SELECT COUNT(1)
      INTO v_count
      FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = p_table_name
       AND COLUMN_NAME = p_column_name;

    IF v_count > 0 THEN
        SET @ddl = CONCAT('ALTER TABLE ', p_table_name, ' DROP COLUMN ', p_column_name);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

DROP PROCEDURE IF EXISTS drop_index_if_exists $$
CREATE PROCEDURE drop_index_if_exists(
    IN p_table_name VARCHAR(64),
    IN p_index_name VARCHAR(64)
)
BEGIN
    DECLARE v_count INT DEFAULT 0;
    SELECT COUNT(1)
      INTO v_count
      FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = p_table_name
       AND INDEX_NAME = p_index_name;

    IF v_count > 0 THEN
        SET @ddl = CONCAT('ALTER TABLE ', p_table_name, ' DROP INDEX ', p_index_name);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

DELIMITER ;

CALL drop_index_if_exists('fe_fire_point', 'idx_fire_point_external_station_id');
CALL drop_index_if_exists('fe_fire_point', 'idx_fire_point_external_company_id');
CALL drop_index_if_exists('fe_fire_point', 'idx_fire_point_source_dept_id');
CALL drop_index_if_exists('fe_fire_point', 'idx_fire_point_sync_status');

CALL drop_column_if_exists('fe_fire_point', 'last_sync_time');
CALL drop_column_if_exists('fe_fire_point', 'sync_status');
CALL drop_column_if_exists('fe_fire_point', 'source_dept_id');
CALL drop_column_if_exists('fe_fire_point', 'external_company_id');
CALL drop_column_if_exists('fe_fire_point', 'station_type');
CALL drop_column_if_exists('fe_fire_point', 'station_number');
CALL drop_column_if_exists('fe_fire_point', 'external_station_id');

CALL drop_index_if_exists('fe_sensor', 'idx_sensor_external_sensor_id');
CALL drop_index_if_exists('fe_sensor', 'idx_sensor_gateway_id');
CALL drop_index_if_exists('fe_sensor', 'idx_sensor_source_dept_id');
CALL drop_index_if_exists('fe_sensor', 'idx_sensor_mac');
CALL drop_index_if_exists('fe_sensor', 'idx_sensor_sync_status');

CALL drop_column_if_exists('fe_sensor', 'last_sync_time');
CALL drop_column_if_exists('fe_sensor', 'sync_status');
CALL drop_column_if_exists('fe_sensor', 'signal_strength');
CALL drop_column_if_exists('fe_sensor', 'mac');
CALL drop_column_if_exists('fe_sensor', 'source_dept_id');
CALL drop_column_if_exists('fe_sensor', 'gateway_id');
CALL drop_column_if_exists('fe_sensor', 'external_sensor_id');

CALL drop_index_if_exists('fe_extinguisher', 'idx_extinguisher_external_extinguisher_id');
CALL drop_index_if_exists('fe_extinguisher', 'idx_extinguisher_external_company_id');
CALL drop_index_if_exists('fe_extinguisher', 'idx_extinguisher_source_dept_id');
CALL drop_index_if_exists('fe_extinguisher', 'idx_extinguisher_sync_status');

CALL drop_column_if_exists('fe_extinguisher', 'last_sync_time');
CALL drop_column_if_exists('fe_extinguisher', 'sync_status');
CALL drop_column_if_exists('fe_extinguisher', 'source_dept_id');
CALL drop_column_if_exists('fe_extinguisher', 'external_company_id');
CALL drop_column_if_exists('fe_extinguisher', 'external_extinguisher_id');

DROP PROCEDURE IF EXISTS drop_column_if_exists;
DROP PROCEDURE IF EXISTS drop_index_if_exists;
