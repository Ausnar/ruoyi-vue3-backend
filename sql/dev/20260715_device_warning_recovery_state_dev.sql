-- Separate the current device fact state from the platform workflow state.
-- Existing warning rows remain active until a later valid SDK sample confirms recovery.

SET @schema_name = DATABASE();

SET @ddl = IF(
    (SELECT COUNT(1) FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_device_warning' AND COLUMN_NAME = 'alarm_state') = 0,
    'ALTER TABLE fe_device_warning ADD COLUMN alarm_state varchar(16) NOT NULL DEFAULT ''active'' COMMENT ''Device fact state: active/recovered'' AFTER evidence_summary',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(1) FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_device_warning' AND COLUMN_NAME = 'recovery_time') = 0,
    'ALTER TABLE fe_device_warning ADD COLUMN recovery_time datetime DEFAULT NULL COMMENT ''Latest valid sample time that confirmed recovery'' AFTER alarm_state',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(1) FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_device_warning' AND COLUMN_NAME = 'recovery_source') = 0,
    'ALTER TABLE fe_device_warning ADD COLUMN recovery_source varchar(32) DEFAULT NULL COMMENT ''Recovery source: sdk_data'' AFTER recovery_time',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(1) FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_device_warning' AND COLUMN_NAME = 'recovery_evidence') = 0,
    'ALTER TABLE fe_device_warning ADD COLUMN recovery_evidence varchar(1000) DEFAULT NULL COMMENT ''Recovery evidence summary'' AFTER recovery_source',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(1) FROM information_schema.STATISTICS
      WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_device_warning' AND INDEX_NAME = 'idx_warning_alarm_time') = 0,
    'ALTER TABLE fe_device_warning ADD INDEX idx_warning_alarm_time (alarm_state, last_trigger_time)',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE fe_device_warning
   SET alarm_state = 'active'
 WHERE alarm_state IS NULL OR alarm_state = '';
