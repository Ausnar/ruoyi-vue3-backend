-- 历史手工单位合并回滚（dev）
-- 按逐行引用备份、完整单位备份和完整历史映射备份恢复批次。

USE ruoyi_ai_dev;

SET @merge_batch := '20260710-manual-dept-to-sdk-merge';

DROP PROCEDURE IF EXISTS rollback_manual_dept_merge_20260710;
DELIMITER $$
CREATE PROCEDURE rollback_manual_dept_merge_20260710()
BEGIN
    DECLARE v_done INT DEFAULT 0;
    DECLARE v_node_backup_count BIGINT DEFAULT 0;
    DECLARE v_mapping_backup_count BIGINT DEFAULT 0;
    DECLARE v_reference_backup_count BIGINT DEFAULT 0;
    DECLARE v_mismatch_count BIGINT DEFAULT 0;
    DECLARE v_table_name VARCHAR(64);
    DECLARE v_pk_column VARCHAR(64);
    DECLARE v_dept_column VARCHAR(64);
    DECLARE restore_cursor CURSOR FOR
        SELECT DISTINCT table_name,pk_column,dept_column
        FROM sys_dept_merge_reference_backup_20260710
        WHERE merge_batch=@merge_batch
          AND table_name <> 'fe_company_dept_mapping'
        ORDER BY table_name,dept_column;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done=1;

    SELECT COUNT(*) INTO v_node_backup_count
    FROM sys_dept_merge_node_backup_20260710 WHERE merge_batch=@merge_batch;
    SELECT COUNT(*) INTO v_mapping_backup_count
    FROM fe_company_dept_mapping_merge_backup_20260710 WHERE merge_batch=@merge_batch;
    SELECT COUNT(*) INTO v_reference_backup_count
    FROM sys_dept_merge_reference_backup_20260710 WHERE merge_batch=@merge_batch;

    IF v_node_backup_count <> 25 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge rollback blocked: node backup count is not 25';
    END IF;
    IF v_mapping_backup_count <> 14 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge rollback blocked: mapping backup count is not 14';
    END IF;
    IF v_reference_backup_count = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge rollback blocked: reference backup is empty';
    END IF;

    START TRANSACTION;
    SET v_done=0;
    OPEN restore_cursor;
    restore_loop: LOOP
        FETCH restore_cursor INTO v_table_name,v_pk_column,v_dept_column;
        IF v_done=1 THEN LEAVE restore_loop; END IF;
        SET @restore_sql=CONCAT(
            'UPDATE `',v_table_name,'` x JOIN sys_dept_merge_reference_backup_20260710 b ',
            'ON b.merge_batch=',QUOTE(@merge_batch),' AND b.table_name=',QUOTE(v_table_name),
            ' AND b.dept_column=',QUOTE(v_dept_column),' AND x.`',v_pk_column,'`=b.record_id ',
            'SET x.`',v_dept_column,'`=b.old_dept_id'
        );
        PREPARE stmt FROM @restore_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
    END LOOP;
    CLOSE restore_cursor;

    UPDATE fe_company_dept_mapping map
    JOIN fe_company_dept_mapping_merge_backup_20260710 b
      ON b.merge_batch=@merge_batch AND b.mapping_id=map.mapping_id
    SET map.external_company_id=b.external_company_id,
        map.external_company_name=b.external_company_name,
        map.dept_id=b.dept_id,
        map.sync_status=b.sync_status,
        map.remark=b.remark,
        map.create_by=b.create_by,
        map.create_time=b.create_time,
        map.update_by=b.update_by,
        map.update_time=b.update_time;

    UPDATE sys_dept d
    JOIN sys_dept_merge_node_backup_20260710 b
      ON b.merge_batch=@merge_batch AND b.dept_id=d.dept_id
    SET d.parent_id=b.parent_id,d.ancestors=b.ancestors,d.dept_name=b.dept_name,d.order_num=b.order_num,
        d.leader=b.leader,d.phone=b.phone,d.email=b.email,d.status=b.status,d.del_flag=b.del_flag,
        d.create_by=b.create_by,d.create_time=b.create_time,d.update_by=b.update_by,d.update_time=b.update_time,
        d.province=b.province,d.city=b.city,d.area=b.area,d.longitude=b.longitude,d.latitude=b.latitude,
        d.dept_source=b.dept_source,d.external_company_id=b.external_company_id,
        d.external_parent_company_id=b.external_parent_company_id,d.external_org_path=b.external_org_path,
        d.source_api_config_id=b.source_api_config_id,d.last_company_sync_time=b.last_company_sync_time;

    -- 验证每条引用都已恢复到备份中的旧单位值。
    SET v_mismatch_count=0;
    SET v_done=0;
    OPEN restore_cursor;
    verify_loop: LOOP
        FETCH restore_cursor INTO v_table_name,v_pk_column,v_dept_column;
        IF v_done=1 THEN LEAVE verify_loop; END IF;
        SET @mismatch=0;
        SET @verify_sql=CONCAT(
            'SELECT COUNT(*) INTO @mismatch FROM sys_dept_merge_reference_backup_20260710 b ',
            'LEFT JOIN `',v_table_name,'` x ON x.`',v_pk_column,'`=b.record_id ',
            'WHERE b.merge_batch=',QUOTE(@merge_batch),' AND b.table_name=',QUOTE(v_table_name),
            ' AND b.dept_column=',QUOTE(v_dept_column),
            ' AND (x.`',v_pk_column,'` IS NULL OR x.`',v_dept_column,'`<>b.old_dept_id)'
        );
        PREPARE stmt FROM @verify_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
        SET v_mismatch_count=v_mismatch_count+IFNULL(@mismatch,0);
    END LOOP;
    CLOSE restore_cursor;

    SELECT v_mismatch_count + COUNT(*) INTO v_mismatch_count
    FROM fe_company_dept_mapping_merge_backup_20260710 b
    LEFT JOIN fe_company_dept_mapping map ON map.mapping_id=b.mapping_id
    WHERE b.merge_batch=@merge_batch
      AND (map.mapping_id IS NULL OR map.dept_id<>b.dept_id OR NOT (map.remark<=>b.remark));

    IF v_mismatch_count <> 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge rollback failed: restored data mismatch';
    END IF;

    COMMIT;
    SELECT 'manual_dept_merge_rollback_completed' AS check_item,
           v_node_backup_count AS restored_node_count,
           v_reference_backup_count AS restored_reference_count,
           v_mapping_backup_count AS restored_mapping_count;
END$$
DELIMITER ;

CALL rollback_manual_dept_merge_20260710();
DROP PROCEDURE IF EXISTS rollback_manual_dept_merge_20260710;

