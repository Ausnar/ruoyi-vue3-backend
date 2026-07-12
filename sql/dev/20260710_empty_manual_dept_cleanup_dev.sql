-- 历史手工空壳单位清理执行脚本（dev）
-- 固定范围：按名称识别保底库中的 3 棵空壳树，并要求总节点数仍为 34。
-- 根节点：河北科技工程职业技术大学、邯郸机场集团有限公司、山东高速服开集团。
-- 任何数量、来源、引用或备份校验不符合预期时，脚本都会报错并停止。

SET @cleanup_batch := '20260710-empty-manual-dept-cleanup';

DROP TEMPORARY TABLE IF EXISTS tmp_empty_manual_target_20260710;
CREATE TEMPORARY TABLE tmp_empty_manual_target_20260710 (
    dept_id BIGINT NOT NULL PRIMARY KEY,
    root_dept_id BIGINT NOT NULL
);

INSERT INTO tmp_empty_manual_target_20260710 (dept_id, root_dept_id)
SELECT n.dept_id, roots.root_dept_id
FROM (
    SELECT r.dept_id AS root_dept_id
    FROM sys_dept r
    INNER JOIN sys_dept platform_root
            ON platform_root.dept_id = r.parent_id
           AND platform_root.dept_source = 'platform_root'
           AND platform_root.del_flag = '0'
    WHERE r.dept_source = 'manual'
      AND r.del_flag = '0'
      AND r.dept_name IN ('河北科技工程职业技术大学', '邯郸机场集团有限公司', '山东高速服开集团')
) roots
INNER JOIN sys_dept n
        ON n.dept_id = roots.root_dept_id
        OR FIND_IN_SET(roots.root_dept_id, n.ancestors)
WHERE n.del_flag = '0';

DROP TEMPORARY TABLE IF EXISTS tmp_dept_reference_20260710;
CREATE TEMPORARY TABLE tmp_dept_reference_20260710 (
    dept_id BIGINT NOT NULL PRIMARY KEY
);

INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM sys_dept_api_config WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM sys_user WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM sys_role_dept WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM sys_notice_dept WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_api_config_company_scope WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_company_dept_mapping WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT last_source_dept_id FROM fe_external_company WHERE last_source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_sdk_sync_log WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_fire_point WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_fire_point WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_fire_point_device_snapshot WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_fire_point_device_snapshot WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_gateway WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_gateway WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_gateway_gps_history WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_gateway_manual_record WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_gateway_manual_record WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_sensor WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_sensor WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_sensor_manual_record WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_extinguisher WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_extinguisher WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_iot_card WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_iot_card WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_alarm_record WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_device_warning WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT source_dept_id FROM fe_device_warning WHERE source_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT applicant_dept_id FROM fe_visit_apply WHERE applicant_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT contract_dept_id FROM fe_visit_apply WHERE contract_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT operator_dept_id FROM fe_visit_apply_log WHERE operator_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_visit_approve_config WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_visit_customer WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT owner_dept_id FROM fe_visit_owner_assign WHERE owner_dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT target_id FROM fe_visit_owner_assign WHERE target_type = 'contract_dept' AND target_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT dept_id FROM fe_visit_passive_event WHERE dept_id IS NOT NULL;
INSERT IGNORE INTO tmp_dept_reference_20260710 SELECT selected_target_id FROM fe_visit_passive_event WHERE selected_target_type = 'contract_dept' AND selected_target_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS sys_dept_empty_cleanup_backup_20260710 (
    cleanup_batch VARCHAR(64) NOT NULL,
    backup_time DATETIME NOT NULL,
    dept_id BIGINT NOT NULL,
    parent_id BIGINT NULL,
    ancestors VARCHAR(50) NULL,
    dept_name VARCHAR(30) NULL,
    order_num INT NULL,
    leader VARCHAR(20) NULL,
    phone VARCHAR(11) NULL,
    email VARCHAR(50) NULL,
    status CHAR(1) NULL,
    del_flag CHAR(1) NULL,
    create_by VARCHAR(64) NULL,
    create_time DATETIME NULL,
    update_by VARCHAR(64) NULL,
    update_time DATETIME NULL,
    province VARCHAR(50) NULL,
    city VARCHAR(50) NULL,
    area VARCHAR(50) NULL,
    longitude VARCHAR(50) NULL,
    latitude VARCHAR(50) NULL,
    dept_source VARCHAR(32) NULL,
    external_company_id BIGINT NULL,
    external_parent_company_id BIGINT NULL,
    external_org_path VARCHAR(500) NULL,
    source_api_config_id BIGINT NULL,
    last_company_sync_time DATETIME NULL,
    PRIMARY KEY (cleanup_batch, dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS execute_empty_manual_dept_cleanup_20260710;
DELIMITER $$
CREATE PROCEDURE execute_empty_manual_dept_cleanup_20260710()
BEGIN
    DECLARE v_target_count INT DEFAULT 0;
    DECLARE v_valid_root_count INT DEFAULT 0;
    DECLARE v_invalid_count INT DEFAULT 0;
    DECLARE v_reference_count INT DEFAULT 0;
    DECLARE v_backup_count INT DEFAULT 0;
    DECLARE v_updated_count INT DEFAULT 0;

    SELECT COUNT(1) INTO v_target_count FROM tmp_empty_manual_target_20260710;
    IF v_target_count <> 34 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty manual dept cleanup blocked: target count is not 34';
    END IF;

    SELECT COUNT(1) INTO v_valid_root_count
    FROM sys_dept r
    INNER JOIN sys_dept platform_root
            ON platform_root.dept_id = r.parent_id
           AND platform_root.dept_source = 'platform_root'
           AND platform_root.del_flag = '0'
    WHERE r.dept_source = 'manual'
      AND r.del_flag = '0'
      AND r.dept_name IN ('河北科技工程职业技术大学', '邯郸机场集团有限公司', '山东高速服开集团');
    IF v_valid_root_count <> 3 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty manual dept cleanup blocked: root identity mismatch';
    END IF;

    SELECT COUNT(1) INTO v_invalid_count
    FROM tmp_empty_manual_target_20260710 t
    LEFT JOIN sys_dept d ON d.dept_id = t.dept_id
    WHERE d.dept_id IS NULL
       OR d.dept_source <> 'manual'
       OR d.del_flag <> '0';
    IF v_invalid_count <> 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty manual dept cleanup blocked: target contains invalid node';
    END IF;

    SELECT COUNT(1) INTO v_reference_count
    FROM tmp_empty_manual_target_20260710 t
    INNER JOIN tmp_dept_reference_20260710 ref ON ref.dept_id = t.dept_id;
    IF v_reference_count <> 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty manual dept cleanup blocked: target has business references';
    END IF;

    INSERT IGNORE INTO sys_dept_empty_cleanup_backup_20260710 (
        cleanup_batch, backup_time, dept_id, parent_id, ancestors, dept_name, order_num,
        leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time,
        province, city, area, longitude, latitude, dept_source, external_company_id,
        external_parent_company_id, external_org_path, source_api_config_id, last_company_sync_time
    )
    SELECT @cleanup_batch, NOW(), d.dept_id, d.parent_id, d.ancestors, d.dept_name, d.order_num,
           d.leader, d.phone, d.email, d.status, d.del_flag, d.create_by, d.create_time, d.update_by, d.update_time,
           d.province, d.city, d.area, d.longitude, d.latitude, d.dept_source, d.external_company_id,
           d.external_parent_company_id, d.external_org_path, d.source_api_config_id, d.last_company_sync_time
    FROM sys_dept d
    INNER JOIN tmp_empty_manual_target_20260710 t ON t.dept_id = d.dept_id;

    SELECT COUNT(1) INTO v_backup_count
    FROM sys_dept_empty_cleanup_backup_20260710
    WHERE cleanup_batch = @cleanup_batch;
    IF v_backup_count <> v_target_count THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty manual dept cleanup blocked: backup is incomplete';
    END IF;

    START TRANSACTION;
    UPDATE sys_dept d
    INNER JOIN tmp_empty_manual_target_20260710 t ON t.dept_id = d.dept_id
    SET d.status = '1',
        d.del_flag = '2',
        d.update_by = 'sql:empty-manual-dept-cleanup',
        d.update_time = NOW();
    SET v_updated_count = ROW_COUNT();

    IF v_updated_count <> v_target_count THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty manual dept cleanup rolled back: updated count mismatch';
    END IF;
    COMMIT;

    SELECT 'empty_manual_dept_cleanup_completed' AS check_item,
           v_target_count AS cleaned_node_count,
           v_backup_count AS backup_node_count;
END$$
DELIMITER ;

CALL execute_empty_manual_dept_cleanup_20260710();
DROP PROCEDURE IF EXISTS execute_empty_manual_dept_cleanup_20260710;

SELECT d.dept_id, d.dept_name, d.status, d.del_flag, d.update_by, d.update_time
FROM sys_dept d
INNER JOIN sys_dept_empty_cleanup_backup_20260710 b
        ON b.dept_id = d.dept_id
       AND b.cleanup_batch = @cleanup_batch
ORDER BY d.dept_id;

DROP TEMPORARY TABLE IF EXISTS tmp_dept_reference_20260710;
DROP TEMPORARY TABLE IF EXISTS tmp_empty_manual_target_20260710;
