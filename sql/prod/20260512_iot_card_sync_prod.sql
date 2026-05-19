-- 2026-05-12 IOT card sync schema (prod)
-- Hodo API calls must be executed from the whitelisted server runtime.

CREATE TABLE IF NOT EXISTS fe_iot_card (
    card_id bigint NOT NULL AUTO_INCREMENT COMMENT 'IOT card ID',
    iccid varchar(64) NOT NULL COMMENT 'SIM ICCID',
    msisdn varchar(64) DEFAULT NULL COMMENT 'MSISDN',
    provider_account varchar(64) DEFAULT NULL COMMENT 'Hodo account',
    card_status int DEFAULT NULL COMMENT 'Hodo card status code',
    card_status_name varchar(64) DEFAULT NULL COMMENT 'Hodo card status name',
    active_time datetime DEFAULT NULL COMMENT 'Active time',
    package_name varchar(255) DEFAULT NULL COMMENT 'Main package name',
    total_flow decimal(12,4) DEFAULT NULL COMMENT 'Total flow MB',
    used_flow decimal(12,4) DEFAULT NULL COMMENT 'Used flow MB',
    left_flow decimal(12,4) DEFAULT NULL COMMENT 'Left flow MB',
    package_start_date datetime DEFAULT NULL COMMENT 'Package start time',
    package_stop_date datetime DEFAULT NULL COMMENT 'Package stop time',
    period_start_date datetime DEFAULT NULL COMMENT 'Current period start time',
    period_stop_date datetime DEFAULT NULL COMMENT 'Current period stop time',
    period varchar(64) DEFAULT NULL COMMENT 'Package period text',
    current_month varchar(6) DEFAULT NULL COMMENT 'Current synced month yyyyMM',
    month_used_flow decimal(12,4) DEFAULT NULL COMMENT 'Current month used flow MB',
    expiry_level varchar(32) DEFAULT NULL COMMENT 'Expiry risk: normal/near_expiry/expiring_soon/expired/unknown',
    flow_level varchar(32) DEFAULT NULL COMMENT 'Flow risk: normal/low/critical/exhausted/unknown',
    risk_summary varchar(500) DEFAULT NULL COMMENT 'Risk summary',
    gateway_id bigint DEFAULT NULL COMMENT 'Related gateway ID',
    gateway_imei varchar(64) DEFAULT NULL COMMENT 'Related gateway IMEI',
    fire_point_id bigint DEFAULT NULL COMMENT 'Related fire point ID',
    dept_id bigint DEFAULT NULL COMMENT 'Related governed dept ID',
    source_dept_id bigint DEFAULT NULL COMMENT 'Related source dept ID',
    data_source varchar(32) DEFAULT NULL COMMENT 'Data source: gateway/import/manual',
    sync_status varchar(32) DEFAULT 'pending' COMMENT 'Sync status: pending/success/failed/incomplete',
    sync_message varchar(1000) DEFAULT NULL COMMENT 'Sync message',
    last_sync_time datetime DEFAULT NULL COMMENT 'Last sync time',
    raw_card_info text COMMENT 'Raw queryCardInfo response',
    raw_package_info text COMMENT 'Raw queryCardPackage response',
    raw_month_flow text COMMENT 'Raw queryMonthFlow response',
    remark varchar(500) DEFAULT NULL COMMENT 'Remark',
    create_by varchar(64) DEFAULT '' COMMENT 'Created by',
    create_time datetime DEFAULT NULL COMMENT 'Created time',
    update_by varchar(64) DEFAULT '' COMMENT 'Updated by',
    update_time datetime DEFAULT NULL COMMENT 'Updated time',
    del_flag char(1) NOT NULL DEFAULT '0' COMMENT 'Delete flag: 0 exists, 2 deleted',
    PRIMARY KEY (card_id),
    UNIQUE KEY uk_iot_card_iccid (iccid),
    KEY idx_iot_card_msisdn (msisdn),
    KEY idx_iot_card_gateway (gateway_id),
    KEY idx_iot_card_dept (dept_id),
    KEY idx_iot_card_expiry_level (expiry_level),
    KEY idx_iot_card_flow_level (flow_level),
    KEY idx_iot_card_sync_status (sync_status),
    KEY idx_iot_card_package_stop (package_stop_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IOT card current snapshot';

CREATE TABLE IF NOT EXISTS fe_iot_card_month_flow (
    flow_id bigint NOT NULL AUTO_INCREMENT COMMENT 'Month flow ID',
    card_id bigint DEFAULT NULL COMMENT 'IOT card ID',
    iccid varchar(64) NOT NULL COMMENT 'SIM ICCID',
    flow_month varchar(6) NOT NULL COMMENT 'Flow month yyyyMM',
    month_used_flow decimal(12,4) DEFAULT NULL COMMENT 'Month used flow MB',
    raw_response text COMMENT 'Raw queryMonthFlow response',
    sync_time datetime DEFAULT NULL COMMENT 'Sync time',
    create_by varchar(64) DEFAULT '' COMMENT 'Created by',
    create_time datetime DEFAULT NULL COMMENT 'Created time',
    update_by varchar(64) DEFAULT '' COMMENT 'Updated by',
    update_time datetime DEFAULT NULL COMMENT 'Updated time',
    PRIMARY KEY (flow_id),
    UNIQUE KEY uk_iot_card_month (iccid, flow_month),
    KEY idx_iot_card_month_card (card_id),
    KEY idx_iot_card_month_time (sync_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IOT card monthly flow snapshot';

CREATE TABLE IF NOT EXISTS fe_iot_card_sync_log (
    sync_log_id bigint NOT NULL AUTO_INCREMENT COMMENT 'Sync log ID',
    sync_scope varchar(64) DEFAULT NULL COMMENT 'Sync scope',
    sync_status varchar(32) DEFAULT NULL COMMENT 'Sync status',
    start_time datetime DEFAULT NULL COMMENT 'Start time',
    end_time datetime DEFAULT NULL COMMENT 'End time',
    total_count int DEFAULT 0 COMMENT 'Total count',
    success_count int DEFAULT 0 COMMENT 'Success count',
    fail_count int DEFAULT 0 COMMENT 'Fail count',
    message varchar(1000) DEFAULT NULL COMMENT 'Message',
    create_by varchar(64) DEFAULT '' COMMENT 'Created by',
    create_time datetime DEFAULT NULL COMMENT 'Created time',
    update_by varchar(64) DEFAULT '' COMMENT 'Updated by',
    update_time datetime DEFAULT NULL COMMENT 'Updated time',
    PRIMARY KEY (sync_log_id),
    KEY idx_iot_card_sync_log_time (start_time),
    KEY idx_iot_card_sync_log_status (sync_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IOT card sync log';

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'Hodo IOT base URL', 'iot.hodo.baseUrl', 'https://api.hodo170.com', 'N', 'admin', NOW(), 'Hodo IOT card API base URL'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'iot.hodo.baseUrl');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'Hodo IOT accessKey', 'iot.hodo.accessKey', '', 'N', 'admin', NOW(), 'Set on server only'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'iot.hodo.accessKey');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'Hodo IOT secretKey', 'iot.hodo.secretKey', '', 'N', 'admin', NOW(), 'Set on server only'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'iot.hodo.secretKey');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'Hodo IOT account', 'iot.hodo.account', '', 'N', 'admin', NOW(), 'Hodo account for card query'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'iot.hodo.account');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'Hodo IOT skip SSL verify', 'iot.hodo.skipSslVerify', 'false', 'N', 'admin', NOW(), 'Use only when server certificate chain cannot be trusted temporarily'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'iot.hodo.skipSslVerify');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'IOT card expiry soon days', 'iot.card.expiry.soonDays', '7', 'N', 'admin', NOW(), 'Expiry risk threshold for expiring_soon'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'iot.card.expiry.soonDays');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'IOT card expiry near days', 'iot.card.expiry.nearDays', '30', 'N', 'admin', NOW(), 'Expiry risk threshold for near_expiry'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'iot.card.expiry.nearDays');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'IOT card flow low percent', 'iot.card.flow.lowPercent', '20', 'N', 'admin', NOW(), 'Left flow percentage threshold'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'iot.card.flow.lowPercent');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'IOT card flow critical MB', 'iot.card.flow.criticalMb', '10', 'N', 'admin', NOW(), 'Left flow MB fallback threshold'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'iot.card.flow.criticalMb');

INSERT INTO fe_iot_card (
    iccid, gateway_id, gateway_imei, fire_point_id, dept_id, source_dept_id,
    data_source, sync_status, create_by, create_time, update_by, update_time, del_flag
)
SELECT s.iccid, g.gateway_id, g.imei, g.fire_point_id, g.dept_id, g.source_dept_id,
       'gateway', 'pending', 'sql:init', NOW(), 'sql:init', NOW(), '0'
FROM (
    SELECT TRIM(sim) AS iccid, MIN(gateway_id) AS gateway_id
    FROM fe_gateway
    WHERE sim IS NOT NULL
      AND TRIM(sim) != ''
      AND (del_flag IS NULL OR del_flag = '0')
    GROUP BY TRIM(sim)
) s
INNER JOIN fe_gateway g ON g.gateway_id = s.gateway_id
ON DUPLICATE KEY UPDATE
    gateway_id = IFNULL(fe_iot_card.gateway_id, VALUES(gateway_id)),
    gateway_imei = IFNULL(fe_iot_card.gateway_imei, VALUES(gateway_imei)),
    fire_point_id = IFNULL(fe_iot_card.fire_point_id, VALUES(fire_point_id)),
    dept_id = IFNULL(fe_iot_card.dept_id, VALUES(dept_id)),
    source_dept_id = IFNULL(fe_iot_card.source_dept_id, VALUES(source_dept_id)),
    update_by = 'sql:init',
    update_time = NOW();
