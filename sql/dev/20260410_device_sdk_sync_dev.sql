-- Device SDK sync schema for development database
-- Note: all changes are validated in ruoyi_ai_dev first.
SET @schema_name = DATABASE();

SELECT '20260410_device_sdk_sync_dev start' AS message;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND COLUMN_NAME = 'external_station_id'),
  'SELECT ''skip fe_fire_point.external_station_id''',
  'ALTER TABLE fe_fire_point ADD COLUMN external_station_id BIGINT NULL COMMENT ''External station id'' AFTER fire_point_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND COLUMN_NAME = 'station_number'),
  'SELECT ''skip fe_fire_point.station_number''',
  'ALTER TABLE fe_fire_point ADD COLUMN station_number VARCHAR(64) NULL COMMENT ''External station number'' AFTER external_station_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND COLUMN_NAME = 'station_type'),
  'SELECT ''skip fe_fire_point.station_type''',
  'ALTER TABLE fe_fire_point ADD COLUMN station_type VARCHAR(64) NULL COMMENT ''External station type'' AFTER station_number'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND COLUMN_NAME = 'external_company_id'),
  'SELECT ''skip fe_fire_point.external_company_id''',
  'ALTER TABLE fe_fire_point ADD COLUMN external_company_id BIGINT NULL COMMENT ''Observed external company id'' AFTER station_type'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND COLUMN_NAME = 'source_dept_id'),
  'SELECT ''skip fe_fire_point.source_dept_id''',
  'ALTER TABLE fe_fire_point ADD COLUMN source_dept_id BIGINT NULL COMMENT ''Credential source dept id'' AFTER external_company_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND COLUMN_NAME = 'sync_status'),
  'SELECT ''skip fe_fire_point.sync_status''',
  'ALTER TABLE fe_fire_point ADD COLUMN sync_status VARCHAR(32) NULL DEFAULT ''manual'' COMMENT ''Sync status'' AFTER source_dept_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND COLUMN_NAME = 'last_sync_time'),
  'SELECT ''skip fe_fire_point.last_sync_time''',
  'ALTER TABLE fe_fire_point ADD COLUMN last_sync_time DATETIME NULL COMMENT ''Last sync time'' AFTER sync_status'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND INDEX_NAME = 'idx_fire_point_external_station_id'),
  'SELECT ''skip idx_fire_point_external_station_id''',
  'CREATE INDEX idx_fire_point_external_station_id ON fe_fire_point (external_station_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND INDEX_NAME = 'idx_fire_point_external_company_id'),
  'SELECT ''skip idx_fire_point_external_company_id''',
  'CREATE INDEX idx_fire_point_external_company_id ON fe_fire_point (external_company_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND INDEX_NAME = 'idx_fire_point_source_dept_id'),
  'SELECT ''skip idx_fire_point_source_dept_id''',
  'CREATE INDEX idx_fire_point_source_dept_id ON fe_fire_point (source_dept_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_fire_point' AND INDEX_NAME = 'idx_fire_point_sync_status'),
  'SELECT ''skip idx_fire_point_sync_status''',
  'CREATE INDEX idx_fire_point_sync_status ON fe_fire_point (sync_status)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND COLUMN_NAME = 'external_sensor_id'),
  'SELECT ''skip fe_sensor.external_sensor_id''',
  'ALTER TABLE fe_sensor ADD COLUMN external_sensor_id BIGINT NULL COMMENT ''External sensor id'' AFTER sensor_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND COLUMN_NAME = 'gateway_id'),
  'SELECT ''skip fe_sensor.gateway_id''',
  'ALTER TABLE fe_sensor ADD COLUMN gateway_id BIGINT NULL COMMENT ''Gateway id'' AFTER dept_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND COLUMN_NAME = 'source_dept_id'),
  'SELECT ''skip fe_sensor.source_dept_id''',
  'ALTER TABLE fe_sensor ADD COLUMN source_dept_id BIGINT NULL COMMENT ''Credential source dept id'' AFTER gateway_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND COLUMN_NAME = 'mac'),
  'SELECT ''skip fe_sensor.mac''',
  'ALTER TABLE fe_sensor ADD COLUMN mac VARCHAR(64) NULL COMMENT ''Sensor MAC'' AFTER gateway_code'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND COLUMN_NAME = 'signal_strength'),
  'SELECT ''skip fe_sensor.signal_strength''',
  'ALTER TABLE fe_sensor ADD COLUMN signal_strength INTEGER NULL COMMENT ''Signal strength'' AFTER battery_level'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND COLUMN_NAME = 'sync_status'),
  'SELECT ''skip fe_sensor.sync_status''',
  'ALTER TABLE fe_sensor ADD COLUMN sync_status VARCHAR(32) NULL DEFAULT ''manual'' COMMENT ''Sync status'' AFTER status'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND COLUMN_NAME = 'last_sync_time'),
  'SELECT ''skip fe_sensor.last_sync_time''',
  'ALTER TABLE fe_sensor ADD COLUMN last_sync_time DATETIME NULL COMMENT ''Last sync time'' AFTER sync_status'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND INDEX_NAME = 'idx_sensor_external_sensor_id'),
  'SELECT ''skip idx_sensor_external_sensor_id''',
  'CREATE INDEX idx_sensor_external_sensor_id ON fe_sensor (external_sensor_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND INDEX_NAME = 'idx_sensor_gateway_id'),
  'SELECT ''skip idx_sensor_gateway_id''',
  'CREATE INDEX idx_sensor_gateway_id ON fe_sensor (gateway_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND INDEX_NAME = 'idx_sensor_source_dept_id'),
  'SELECT ''skip idx_sensor_source_dept_id''',
  'CREATE INDEX idx_sensor_source_dept_id ON fe_sensor (source_dept_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND INDEX_NAME = 'idx_sensor_mac'),
  'SELECT ''skip idx_sensor_mac''',
  'CREATE INDEX idx_sensor_mac ON fe_sensor (mac)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_sensor' AND INDEX_NAME = 'idx_sensor_sync_status'),
  'SELECT ''skip idx_sensor_sync_status''',
  'CREATE INDEX idx_sensor_sync_status ON fe_sensor (sync_status)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'external_extinguisher_id'),
  'SELECT ''skip fe_extinguisher.external_extinguisher_id''',
  'ALTER TABLE fe_extinguisher ADD COLUMN external_extinguisher_id BIGINT NULL COMMENT ''External extinguisher id'' AFTER extinguisher_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'external_company_id'),
  'SELECT ''skip fe_extinguisher.external_company_id''',
  'ALTER TABLE fe_extinguisher ADD COLUMN external_company_id BIGINT NULL COMMENT ''Observed external company id'' AFTER external_extinguisher_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'source_dept_id'),
  'SELECT ''skip fe_extinguisher.source_dept_id''',
  'ALTER TABLE fe_extinguisher ADD COLUMN source_dept_id BIGINT NULL COMMENT ''Credential source dept id'' AFTER dept_id'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'sync_status'),
  'SELECT ''skip fe_extinguisher.sync_status''',
  'ALTER TABLE fe_extinguisher ADD COLUMN sync_status VARCHAR(32) NULL DEFAULT ''manual'' COMMENT ''Sync status'' AFTER status'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_extinguisher' AND COLUMN_NAME = 'last_sync_time'),
  'SELECT ''skip fe_extinguisher.last_sync_time''',
  'ALTER TABLE fe_extinguisher ADD COLUMN last_sync_time DATETIME NULL COMMENT ''Last sync time'' AFTER sync_status'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_extinguisher' AND INDEX_NAME = 'idx_extinguisher_external_extinguisher_id'),
  'SELECT ''skip idx_extinguisher_external_extinguisher_id''',
  'CREATE INDEX idx_extinguisher_external_extinguisher_id ON fe_extinguisher (external_extinguisher_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_extinguisher' AND INDEX_NAME = 'idx_extinguisher_external_company_id'),
  'SELECT ''skip idx_extinguisher_external_company_id''',
  'CREATE INDEX idx_extinguisher_external_company_id ON fe_extinguisher (external_company_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_extinguisher' AND INDEX_NAME = 'idx_extinguisher_source_dept_id'),
  'SELECT ''skip idx_extinguisher_source_dept_id''',
  'CREATE INDEX idx_extinguisher_source_dept_id ON fe_extinguisher (source_dept_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_extinguisher' AND INDEX_NAME = 'idx_extinguisher_sync_status'),
  'SELECT ''skip idx_extinguisher_sync_status''',
  'CREATE INDEX idx_extinguisher_sync_status ON fe_extinguisher (sync_status)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS fe_gateway (
    gateway_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Gateway id',
    external_tbox_id BIGINT NULL COMMENT 'External tbox id',
    imei VARCHAR(64) NOT NULL COMMENT 'Gateway imei',
    sim VARCHAR(64) NULL COMMENT 'SIM number',
    fire_point_id BIGINT NULL COMMENT 'Fire point id',
    dept_id BIGINT NULL COMMENT 'Governed dept id',
    source_dept_id BIGINT NULL COMMENT 'Credential source dept id',
    external_company_id BIGINT NULL COMMENT 'Observed external company id',
    gps_longitude DECIMAL(18,6) NULL COMMENT 'GPS longitude',
    gps_latitude DECIMAL(18,6) NULL COMMENT 'GPS latitude',
    last_online_time DATETIME NULL COMMENT 'Last online time',
    sync_status VARCHAR(32) NULL DEFAULT 'manual' COMMENT 'Sync status',
    last_sync_time DATETIME NULL COMMENT 'Last sync time',
    status VARCHAR(32) NULL DEFAULT 'online' COMMENT 'Gateway status',
    create_by VARCHAR(64) NULL COMMENT 'Create by',
    create_time DATETIME NULL COMMENT 'Create time',
    update_by VARCHAR(64) NULL COMMENT 'Update by',
    update_time DATETIME NULL COMMENT 'Update time',
    remark VARCHAR(500) NULL COMMENT 'Remark',
    del_flag CHAR(1) NULL DEFAULT '0' COMMENT 'Delete flag',
    PRIMARY KEY (gateway_id),
    UNIQUE KEY uk_gateway_external_tbox_id (external_tbox_id),
    UNIQUE KEY uk_gateway_imei (imei),
    KEY idx_gateway_fire_point_id (fire_point_id),
    KEY idx_gateway_dept_id (dept_id),
    KEY idx_gateway_source_dept_id (source_dept_id),
    KEY idx_gateway_external_company_id (external_company_id),
    KEY idx_gateway_sync_status (sync_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Gateway snapshot table';

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'fe_gateway' AND COLUMN_NAME = 'last_sync_time'),
  'SELECT ''skip fe_gateway.last_sync_time''',
  'ALTER TABLE fe_gateway ADD COLUMN last_sync_time DATETIME NULL COMMENT ''Last sync time'' AFTER sync_status'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS fe_extinguisher_sensor_binding (
    binding_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Binding id',
    extinguisher_id BIGINT NULL COMMENT 'Local extinguisher id',
    sensor_id BIGINT NULL COMMENT 'Local sensor id',
    external_extinguisher_id BIGINT NULL COMMENT 'External extinguisher id',
    external_sensor_id BIGINT NULL COMMENT 'External sensor id',
    bind_time DATETIME NULL COMMENT 'Bind time',
    unbind_time DATETIME NULL COMMENT 'Unbind time',
    is_active CHAR(1) NOT NULL DEFAULT '1' COMMENT 'Is active',
    source_type VARCHAR(32) NULL DEFAULT 'sdk_sync' COMMENT 'Binding source',
    create_by VARCHAR(64) NULL COMMENT 'Create by',
    create_time DATETIME NULL COMMENT 'Create time',
    update_by VARCHAR(64) NULL COMMENT 'Update by',
    update_time DATETIME NULL COMMENT 'Update time',
    remark VARCHAR(500) NULL COMMENT 'Remark',
    PRIMARY KEY (binding_id),
    KEY idx_binding_extinguisher_id (extinguisher_id),
    KEY idx_binding_sensor_id (sensor_id),
    KEY idx_binding_external_extinguisher_id (external_extinguisher_id),
    KEY idx_binding_external_sensor_id (external_sensor_id),
    KEY idx_binding_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Extinguisher sensor binding history';

CREATE TABLE IF NOT EXISTS fe_external_company (
    company_record_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Local company snapshot id',
    external_company_id BIGINT NOT NULL COMMENT 'Observed external company id',
    external_company_name VARCHAR(255) NULL COMMENT 'Observed external company name',
    number_prefix VARCHAR(64) NULL COMMENT 'Observed external company number prefix',
    org_path VARCHAR(500) NULL COMMENT 'Observed company org path',
    parent_external_company_id BIGINT NULL COMMENT 'Parent external company id',
    sync_status VARCHAR(32) NULL DEFAULT 'observed' COMMENT 'Observation status',
    first_seen_time DATETIME NULL COMMENT 'First seen time',
    last_seen_time DATETIME NULL COMMENT 'Last seen time',
    last_source_dept_id BIGINT NULL COMMENT 'Last credential source dept id',
    create_by VARCHAR(64) NULL COMMENT 'Create by',
    create_time DATETIME NULL COMMENT 'Create time',
    update_by VARCHAR(64) NULL COMMENT 'Update by',
    update_time DATETIME NULL COMMENT 'Update time',
    remark VARCHAR(500) NULL COMMENT 'Remark',
    PRIMARY KEY (company_record_id),
    UNIQUE KEY uk_external_company_id (external_company_id),
    KEY idx_external_company_last_source_dept_id (last_source_dept_id),
    KEY idx_external_company_sync_status (sync_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Observed external company snapshot';

CREATE TABLE IF NOT EXISTS fe_api_config_company_scope (
    scope_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Scope id',
    config_id BIGINT NOT NULL COMMENT 'Sys dept api config id',
    source_dept_id BIGINT NOT NULL COMMENT 'Credential source dept id',
    external_company_id BIGINT NOT NULL COMMENT 'Observed external company id',
    external_company_name VARCHAR(255) NULL COMMENT 'Observed external company name',
    first_seen_time DATETIME NULL COMMENT 'First seen time',
    last_seen_time DATETIME NULL COMMENT 'Last seen time',
    sync_status VARCHAR(32) NULL DEFAULT 'observed' COMMENT 'Scope status',
    remark VARCHAR(500) NULL COMMENT 'Remark',
    create_by VARCHAR(64) NULL COMMENT 'Create by',
    create_time DATETIME NULL COMMENT 'Create time',
    update_by VARCHAR(64) NULL COMMENT 'Update by',
    update_time DATETIME NULL COMMENT 'Update time',
    PRIMARY KEY (scope_id),
    UNIQUE KEY uk_scope_config_company (config_id, external_company_id),
    KEY idx_scope_source_dept_id (source_dept_id),
    KEY idx_scope_external_company_id (external_company_id),
    KEY idx_scope_sync_status (sync_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Observed company scope under api config';

CREATE TABLE IF NOT EXISTS fe_company_dept_mapping (
    mapping_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Mapping id',
    external_company_id BIGINT NOT NULL COMMENT 'External company id',
    external_company_name VARCHAR(255) NULL COMMENT 'External company name',
    dept_id BIGINT NOT NULL COMMENT 'Local governed dept id',
    sync_status VARCHAR(32) NULL DEFAULT 'active' COMMENT 'Mapping status',
    remark VARCHAR(500) NULL COMMENT 'Remark',
    create_by VARCHAR(64) NULL COMMENT 'Create by',
    create_time DATETIME NULL COMMENT 'Create time',
    update_by VARCHAR(64) NULL COMMENT 'Update by',
    update_time DATETIME NULL COMMENT 'Update time',
    PRIMARY KEY (mapping_id),
    UNIQUE KEY uk_company_mapping_external_company_id (external_company_id),
    KEY idx_company_mapping_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='External company to governed dept mapping';

CREATE TABLE IF NOT EXISTS fe_sdk_sync_log (
    sync_log_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Sync log id',
    dept_id BIGINT NULL COMMENT 'Credential source dept id',
    external_company_id BIGINT NULL COMMENT 'Observed external company id',
    sync_scope VARCHAR(32) NOT NULL COMMENT 'Sync scope',
    sync_status VARCHAR(32) NOT NULL COMMENT 'Sync status',
    start_time DATETIME NULL COMMENT 'Start time',
    end_time DATETIME NULL COMMENT 'End time',
    success_count INT NULL DEFAULT 0 COMMENT 'Success count',
    fail_count INT NULL DEFAULT 0 COMMENT 'Fail count',
    message VARCHAR(2000) NULL COMMENT 'Result message',
    token_expire_time DATETIME NULL COMMENT 'Token expire time',
    remark VARCHAR(500) NULL COMMENT 'Remark',
    create_by VARCHAR(64) NULL COMMENT 'Create by',
    create_time DATETIME NULL COMMENT 'Create time',
    update_by VARCHAR(64) NULL COMMENT 'Update by',
    update_time DATETIME NULL COMMENT 'Update time',
    PRIMARY KEY (sync_log_id),
    KEY idx_sdk_sync_log_dept_id (dept_id),
    KEY idx_sdk_sync_log_external_company_id (external_company_id),
    KEY idx_sdk_sync_log_scope (sync_scope),
    KEY idx_sdk_sync_log_status (sync_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Device SDK sync log';

SELECT '20260410_device_sdk_sync_dev done' AS message;
