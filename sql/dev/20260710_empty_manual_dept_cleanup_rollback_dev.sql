-- 历史手工空壳单位清理回滚脚本（dev）
-- 回滚批次：20260710-empty-manual-dept-cleanup

SET @cleanup_batch := '20260710-empty-manual-dept-cleanup';

DROP PROCEDURE IF EXISTS rollback_empty_manual_dept_cleanup_20260710;
DELIMITER $$
CREATE PROCEDURE rollback_empty_manual_dept_cleanup_20260710()
BEGIN
    DECLARE v_backup_count INT DEFAULT 0;
    DECLARE v_updated_count INT DEFAULT 0;

    SELECT COUNT(1) INTO v_backup_count
    FROM sys_dept_empty_cleanup_backup_20260710
    WHERE cleanup_batch = @cleanup_batch;
    IF v_backup_count <> 34 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty manual dept rollback blocked: backup count is not 34';
    END IF;

    START TRANSACTION;
    UPDATE sys_dept d
    INNER JOIN sys_dept_empty_cleanup_backup_20260710 b
            ON b.dept_id = d.dept_id
           AND b.cleanup_batch = @cleanup_batch
    SET d.parent_id = b.parent_id,
        d.ancestors = b.ancestors,
        d.dept_name = b.dept_name,
        d.order_num = b.order_num,
        d.leader = b.leader,
        d.phone = b.phone,
        d.email = b.email,
        d.status = b.status,
        d.del_flag = b.del_flag,
        d.create_by = b.create_by,
        d.create_time = b.create_time,
        d.update_by = b.update_by,
        d.update_time = b.update_time,
        d.province = b.province,
        d.city = b.city,
        d.area = b.area,
        d.longitude = b.longitude,
        d.latitude = b.latitude,
        d.dept_source = b.dept_source,
        d.external_company_id = b.external_company_id,
        d.external_parent_company_id = b.external_parent_company_id,
        d.external_org_path = b.external_org_path,
        d.source_api_config_id = b.source_api_config_id,
        d.last_company_sync_time = b.last_company_sync_time;
    SET v_updated_count = ROW_COUNT();

    IF v_updated_count <> v_backup_count THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty manual dept rollback failed: updated count mismatch';
    END IF;
    COMMIT;

    SELECT 'empty_manual_dept_cleanup_rollback_completed' AS check_item,
           v_updated_count AS restored_node_count;
END$$
DELIMITER ;

CALL rollback_empty_manual_dept_cleanup_20260710();
DROP PROCEDURE IF EXISTS rollback_empty_manual_dept_cleanup_20260710;

SELECT d.dept_id, d.dept_name, d.status, d.del_flag, d.update_by, d.update_time
FROM sys_dept d
INNER JOIN sys_dept_empty_cleanup_backup_20260710 b
        ON b.dept_id = d.dept_id
       AND b.cleanup_batch = @cleanup_batch
ORDER BY d.dept_id;
