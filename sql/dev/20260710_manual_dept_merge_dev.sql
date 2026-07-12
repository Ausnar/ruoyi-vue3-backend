-- 历史手工单位合并到 SDK 镜像单位（dev）
-- 执行边界：固定 25 个手工节点；按 owner/source/contract 三类语义迁移引用；旧节点逻辑删除。

USE ruoyi_ai_dev;

SET @merge_batch := '20260710-manual-dept-to-sdk-merge';

DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_merge_seed_20260710;
CREATE TEMPORARY TABLE tmp_manual_dept_merge_seed_20260710 (
    old_dept_name VARCHAR(128) NOT NULL PRIMARY KEY,
    owner_external_company_id BIGINT NOT NULL,
    source_external_company_id BIGINT NOT NULL,
    contract_external_company_id BIGINT NOT NULL,
    match_evidence VARCHAR(128) NOT NULL
);

INSERT INTO tmp_manual_dept_merge_seed_20260710 VALUES
    ('苏州公共交通集团有限公司',3264,3264,3264,'历史映射外部单位3264+SDK授权根'),
    ('苏州市公交集团吴江公司',42,42,42,'历史映射外部单位42'),
    ('苏州公交集团园区公司',33,33,33,'历史映射外部单位33'),
    ('苏州公交集团相城公司',16,16,16,'历史映射外部单位16'),
    ('苏州公交集团新区公司',43,43,43,'历史映射外部单位43'),
    ('吴江公交城市彩虹分公司',3182,3182,3182,'历史映射外部单位3182'),
    ('无锡公共交通集团有限公司',3240,3240,3240,'SDK授权根+完整无锡镜像树'),
    ('无锡公交新城分公司',3246,3246,3246,'唯一同名+父级镜像'),
    ('新城分公司瑞景道营运部',3247,3247,3247,'唯一同名+父级镜像'),
    ('无锡公共交通集团有限公司（本部）',3249,3249,3249,'唯一同名+父级镜像'),
    ('常州公共交通集团有限公司',46,46,46,'SDK授权根+完整常州镜像树'),
    ('常州公交集团第一分公司',3203,3203,3203,'历史映射外部单位3203'),
    ('常州公交集团第二分公司',3243,3243,3243,'历史映射外部单位3243'),
    ('常州公交集团第三分公司',3244,3244,3244,'历史映射外部单位3244'),
    ('常州公交集团第四分公司',3245,3245,3245,'唯一同名+父级镜像'),
    ('北京威发新世纪信息技术有限公司',3254,3254,3254,'唯一同名SDK授权根；修正误挂默克树'),
    ('威发（西安）软件公司',3255,3254,3254,'业务单位外部3255；历史凭证迁到授权根3254'),
    ('中国民航技术装备有限责任公司',14,14,14,'唯一同名SDK授权根'),
    ('中国民航技术装备有限责任公司（本部）',3238,3238,3238,'唯一同名+父级镜像'),
    ('中航材智慧空港（广州）科技有限公司',3239,14,14,'业务单位外部3239；历史凭证迁到授权根14'),
    ('宸轩中消（北京）科技有限公司',3267,3267,3267,'历史映射外部单位3267+SDK授权根'),
    ('默克投资(中国)有限公司',3260,3260,3260,'唯一同名SDK授权根'),
    ('默克中国广州分公司',3270,3270,3270,'唯一同名+父级镜像'),
    ('默克化工技术（上海）有限公司成都分公司',3269,3269,3269,'唯一同名+父级镜像'),
    ('默克化工技术（上海）有限公司',3261,3261,3261,'唯一同名+父级镜像');

DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_merge_map_20260710;
CREATE TEMPORARY TABLE tmp_manual_dept_merge_map_20260710 AS
SELECT old_dept.dept_id AS old_dept_id,
       owner_target.dept_id AS owner_target_dept_id,
       source_target.dept_id AS source_target_dept_id,
       contract_target.dept_id AS contract_target_dept_id,
       seed.match_evidence
FROM tmp_manual_dept_merge_seed_20260710 seed
JOIN sys_dept old_dept
  ON old_dept.dept_name=seed.old_dept_name
 AND old_dept.dept_source='manual'
 AND old_dept.del_flag='0'
JOIN sys_dept owner_target
  ON owner_target.external_company_id=seed.owner_external_company_id
 AND owner_target.dept_source='sdk_company'
 AND owner_target.del_flag='0'
JOIN sys_dept source_target
  ON source_target.external_company_id=seed.source_external_company_id
 AND source_target.dept_source='sdk_company'
 AND source_target.del_flag='0'
JOIN sys_dept contract_target
  ON contract_target.external_company_id=seed.contract_external_company_id
 AND contract_target.dept_source='sdk_company'
 AND contract_target.del_flag='0';
ALTER TABLE tmp_manual_dept_merge_map_20260710 ADD PRIMARY KEY (old_dept_id);
DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_ref_rule_20260710;
CREATE TEMPORARY TABLE tmp_manual_dept_ref_rule_20260710 (
    rule_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(64) NOT NULL,
    pk_column VARCHAR(64) NOT NULL,
    dept_column VARCHAR(64) NOT NULL,
    target_column VARCHAR(64) NOT NULL,
    target_semantic VARCHAR(32) NOT NULL,
    extra_condition VARCHAR(255) NULL
);

INSERT INTO tmp_manual_dept_ref_rule_20260710
    (table_name,pk_column,dept_column,target_column,target_semantic,extra_condition)
VALUES
    ('sys_dept_api_config','config_id','dept_id','source_target_dept_id','source',NULL),
    ('sys_user','user_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_api_config_company_scope','scope_id','source_dept_id','source_target_dept_id','source',NULL),
    ('fe_external_company','company_record_id','last_source_dept_id','source_target_dept_id','source',NULL),
    ('fe_sdk_sync_log','sync_log_id','dept_id','source_target_dept_id','source',NULL),
    ('fe_fire_point','fire_point_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_fire_point','fire_point_id','source_dept_id','source_target_dept_id','source',NULL),
    ('fe_fire_point_device_snapshot','snapshot_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_fire_point_device_snapshot','snapshot_id','source_dept_id','source_target_dept_id','source',NULL),
    ('fe_gateway','gateway_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_gateway','gateway_id','source_dept_id','source_target_dept_id','source',NULL),
    ('fe_gateway_gps_history','history_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_gateway_manual_record','record_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_gateway_manual_record','record_id','source_dept_id','source_target_dept_id','source',NULL),
    ('fe_sensor','sensor_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_sensor','sensor_id','source_dept_id','source_target_dept_id','source',NULL),
    ('fe_sensor_manual_record','sensor_record_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_extinguisher','extinguisher_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_extinguisher','extinguisher_id','source_dept_id','source_target_dept_id','source',NULL),
    ('fe_iot_card','card_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_iot_card','card_id','source_dept_id','source_target_dept_id','source',NULL),
    ('fe_alarm_record','alarm_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_device_warning','warning_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_device_warning','warning_id','source_dept_id','source_target_dept_id','source',NULL),
    ('fe_visit_apply','visit_id','applicant_dept_id','owner_target_dept_id','owner',NULL),
    ('fe_visit_apply','visit_id','contract_dept_id','contract_target_dept_id','contract',NULL),
    ('fe_visit_apply_log','log_id','operator_dept_id','owner_target_dept_id','owner',NULL),
    ('fe_visit_approve_config','config_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_visit_customer','customer_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_visit_owner_assign','assign_id','owner_dept_id','owner_target_dept_id','owner',NULL),
    ('fe_visit_owner_assign','assign_id','target_id','contract_target_dept_id','contract','x.target_type = ''contract_dept'''),
    ('fe_visit_passive_event','event_id','dept_id','owner_target_dept_id','owner',NULL),
    ('fe_visit_passive_event','event_id','selected_target_id','contract_target_dept_id','contract','x.selected_target_type = ''contract_dept''');

CREATE TABLE IF NOT EXISTS sys_dept_merge_node_backup_20260710 (
    merge_batch VARCHAR(64) NOT NULL,
    backup_time DATETIME NOT NULL,
    dept_id BIGINT NOT NULL,
    parent_id BIGINT NULL, ancestors VARCHAR(50) NULL, dept_name VARCHAR(30) NULL, order_num INT NULL,
    leader VARCHAR(20) NULL, phone VARCHAR(11) NULL, email VARCHAR(50) NULL,
    status CHAR(1) NULL, del_flag CHAR(1) NULL, create_by VARCHAR(64) NULL, create_time DATETIME NULL,
    update_by VARCHAR(64) NULL, update_time DATETIME NULL,
    province VARCHAR(50) NULL, city VARCHAR(50) NULL, area VARCHAR(50) NULL,
    longitude VARCHAR(50) NULL, latitude VARCHAR(50) NULL, dept_source VARCHAR(32) NULL,
    external_company_id BIGINT NULL, external_parent_company_id BIGINT NULL,
    external_org_path VARCHAR(500) NULL, source_api_config_id BIGINT NULL, last_company_sync_time DATETIME NULL,
    owner_target_dept_id BIGINT NOT NULL, source_target_dept_id BIGINT NOT NULL,
    contract_target_dept_id BIGINT NOT NULL, match_evidence VARCHAR(128) NOT NULL,
    PRIMARY KEY (merge_batch,dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_dept_merge_reference_backup_20260710 (
    merge_batch VARCHAR(64) NOT NULL,
    backup_time DATETIME NOT NULL,
    table_name VARCHAR(64) NOT NULL,
    pk_column VARCHAR(64) NOT NULL,
    record_id BIGINT NOT NULL,
    dept_column VARCHAR(64) NOT NULL,
    target_semantic VARCHAR(32) NOT NULL,
    old_dept_id BIGINT NOT NULL,
    new_dept_id BIGINT NOT NULL,
    PRIMARY KEY (merge_batch,table_name,dept_column,record_id),
    KEY idx_dept_merge_reference_old (old_dept_id),
    KEY idx_dept_merge_reference_new (new_dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS fe_company_dept_mapping_merge_backup_20260710 (
    merge_batch VARCHAR(64) NOT NULL,
    backup_time DATETIME NOT NULL,
    mapping_id BIGINT NOT NULL,
    external_company_id BIGINT NULL,
    external_company_name VARCHAR(255) NULL,
    dept_id BIGINT NULL,
    sync_status VARCHAR(32) NULL,
    remark VARCHAR(500) NULL,
    create_by VARCHAR(64) NULL,
    create_time DATETIME NULL,
    update_by VARCHAR(64) NULL,
    update_time DATETIME NULL,
    PRIMARY KEY (merge_batch,mapping_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS execute_manual_dept_merge_20260710;
DELIMITER $$
CREATE PROCEDURE execute_manual_dept_merge_20260710()
BEGIN
    DECLARE v_done INT DEFAULT 0;
    DECLARE v_map_count INT DEFAULT 0;
    DECLARE v_invalid_count INT DEFAULT 0;
    DECLARE v_role_ref_count INT DEFAULT 0;
    DECLARE v_notice_ref_count INT DEFAULT 0;
    DECLARE v_remaining_count BIGINT DEFAULT 0;
    DECLARE v_rule_id INT;
    DECLARE v_table_name VARCHAR(64);
    DECLARE v_pk_column VARCHAR(64);
    DECLARE v_dept_column VARCHAR(64);
    DECLARE v_target_column VARCHAR(64);
    DECLARE v_target_semantic VARCHAR(32);
    DECLARE v_extra_condition VARCHAR(255);
    DECLARE rule_cursor CURSOR FOR
        SELECT rule_id,table_name,pk_column,dept_column,target_column,target_semantic,extra_condition
        FROM tmp_manual_dept_ref_rule_20260710 ORDER BY rule_id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done=1;

    SELECT COUNT(*) INTO v_map_count FROM tmp_manual_dept_merge_map_20260710;
    IF v_map_count <> 25 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge blocked: mapping count is not 25';
    END IF;

    SELECT COUNT(*) INTO v_invalid_count
    FROM tmp_manual_dept_merge_map_20260710 m
    LEFT JOIN sys_dept old_dept ON old_dept.dept_id=m.old_dept_id
    LEFT JOIN sys_dept owner_target ON owner_target.dept_id=m.owner_target_dept_id
    LEFT JOIN sys_dept source_target ON source_target.dept_id=m.source_target_dept_id
    LEFT JOIN sys_dept contract_target ON contract_target.dept_id=m.contract_target_dept_id
    WHERE old_dept.dept_source <> 'manual' OR old_dept.del_flag <> '0'
       OR owner_target.dept_source <> 'sdk_company' OR owner_target.del_flag <> '0'
       OR source_target.dept_source <> 'sdk_company' OR source_target.del_flag <> '0'
       OR contract_target.dept_source <> 'sdk_company' OR contract_target.del_flag <> '0';
    IF v_invalid_count <> 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge blocked: invalid old or target dept';
    END IF;

    SELECT COUNT(*) INTO v_invalid_count
    FROM sys_dept d
    LEFT JOIN tmp_manual_dept_merge_map_20260710 m ON m.old_dept_id=d.dept_id
    WHERE d.dept_source='manual' AND d.del_flag='0' AND m.old_dept_id IS NULL;
    IF v_invalid_count <> 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge blocked: active manual dept is not mapped';
    END IF;

    -- 复合主键权限范围当前必须为 0；若执行前新增则阻断，避免主键冲突。
    -- MySQL 5.7 不能在同一条语句中重复打开同一张临时表，因此分开核对两类复合主键引用。
    SELECT COUNT(*) INTO v_role_ref_count
    FROM sys_role_dept x
    JOIN tmp_manual_dept_merge_map_20260710 m ON x.dept_id=m.old_dept_id;
    SELECT COUNT(*) INTO v_notice_ref_count
    FROM sys_notice_dept x
    JOIN tmp_manual_dept_merge_map_20260710 m ON x.dept_id=m.old_dept_id;
    IF v_role_ref_count + v_notice_ref_count <> 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge blocked: role or notice dept reference exists';
    END IF;

    INSERT IGNORE INTO sys_dept_merge_node_backup_20260710 (
        merge_batch,backup_time,dept_id,parent_id,ancestors,dept_name,order_num,leader,phone,email,status,del_flag,
        create_by,create_time,update_by,update_time,province,city,area,longitude,latitude,dept_source,
        external_company_id,external_parent_company_id,external_org_path,source_api_config_id,last_company_sync_time,
        owner_target_dept_id,source_target_dept_id,contract_target_dept_id,match_evidence
    )
    SELECT @merge_batch,NOW(),d.dept_id,d.parent_id,d.ancestors,d.dept_name,d.order_num,d.leader,d.phone,d.email,d.status,d.del_flag,
           d.create_by,d.create_time,d.update_by,d.update_time,d.province,d.city,d.area,d.longitude,d.latitude,d.dept_source,
           d.external_company_id,d.external_parent_company_id,d.external_org_path,d.source_api_config_id,d.last_company_sync_time,
           m.owner_target_dept_id,m.source_target_dept_id,m.contract_target_dept_id,m.match_evidence
    FROM sys_dept d JOIN tmp_manual_dept_merge_map_20260710 m ON m.old_dept_id=d.dept_id;

    SELECT COUNT(*) INTO v_invalid_count FROM sys_dept_merge_node_backup_20260710 WHERE merge_batch=@merge_batch;
    IF v_invalid_count <> 25 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge blocked: node backup is incomplete';
    END IF;

    -- 按固定规则逐列备份所有待迁移引用。
    SET v_done=0;
    OPEN rule_cursor;
    backup_loop: LOOP
        FETCH rule_cursor INTO v_rule_id,v_table_name,v_pk_column,v_dept_column,v_target_column,v_target_semantic,v_extra_condition;
        IF v_done=1 THEN LEAVE backup_loop; END IF;
        SET @backup_sql=CONCAT(
            'INSERT IGNORE INTO sys_dept_merge_reference_backup_20260710 ',
            '(merge_batch,backup_time,table_name,pk_column,record_id,dept_column,target_semantic,old_dept_id,new_dept_id) ',
            'SELECT ',QUOTE(@merge_batch),',NOW(),',QUOTE(v_table_name),',',QUOTE(v_pk_column),',x.`',v_pk_column,'`,',
            QUOTE(v_dept_column),',',QUOTE(v_target_semantic),',x.`',v_dept_column,'`,m.`',v_target_column,'` ',
            'FROM `',v_table_name,'` x JOIN tmp_manual_dept_merge_map_20260710 m ON x.`',v_dept_column,'`=m.old_dept_id',
            IF(v_extra_condition IS NULL,'',CONCAT(' WHERE ',v_extra_condition))
        );
        PREPARE stmt FROM @backup_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
    END LOOP;
    CLOSE rule_cursor;

    -- 历史映射按 external_company_id 直接迁到对应 SDK 镜像，避免旧根一对多时误归并。
    INSERT IGNORE INTO fe_company_dept_mapping_merge_backup_20260710
        (merge_batch,backup_time,mapping_id,external_company_id,external_company_name,dept_id,sync_status,
         remark,create_by,create_time,update_by,update_time)
    SELECT @merge_batch,NOW(),map.mapping_id,map.external_company_id,map.external_company_name,map.dept_id,map.sync_status,
           map.remark,map.create_by,map.create_time,map.update_by,map.update_time
    FROM fe_company_dept_mapping map
    JOIN tmp_manual_dept_merge_map_20260710 m ON m.old_dept_id=map.dept_id;

    SELECT COUNT(*) INTO v_invalid_count
    FROM fe_company_dept_mapping_merge_backup_20260710
    WHERE merge_batch=@merge_batch;
    IF v_invalid_count <> 14 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge blocked: mapping backup count is not 14';
    END IF;

    INSERT IGNORE INTO sys_dept_merge_reference_backup_20260710
        (merge_batch,backup_time,table_name,pk_column,record_id,dept_column,target_semantic,old_dept_id,new_dept_id)
    SELECT @merge_batch,NOW(),'fe_company_dept_mapping','mapping_id',map.mapping_id,'dept_id','external_identity',map.dept_id,sdk.dept_id
    FROM fe_company_dept_mapping map
    JOIN tmp_manual_dept_merge_map_20260710 m ON m.old_dept_id=map.dept_id
    JOIN sys_dept sdk ON sdk.external_company_id=map.external_company_id AND sdk.dept_source='sdk_company' AND sdk.del_flag='0';

    START TRANSACTION;
    SET v_done=0;
    OPEN rule_cursor;
    update_loop: LOOP
        FETCH rule_cursor INTO v_rule_id,v_table_name,v_pk_column,v_dept_column,v_target_column,v_target_semantic,v_extra_condition;
        IF v_done=1 THEN LEAVE update_loop; END IF;
        SET @update_sql=CONCAT(
            'UPDATE `',v_table_name,'` x JOIN tmp_manual_dept_merge_map_20260710 m ',
            'ON x.`',v_dept_column,'`=m.old_dept_id SET x.`',v_dept_column,'`=m.`',v_target_column,'`',
            IF(v_extra_condition IS NULL,'',CONCAT(' WHERE ',v_extra_condition))
        );
        PREPARE stmt FROM @update_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
    END LOOP;
    CLOSE rule_cursor;

    UPDATE fe_company_dept_mapping map
    JOIN tmp_manual_dept_merge_map_20260710 m ON m.old_dept_id=map.dept_id
    JOIN sys_dept sdk ON sdk.external_company_id=map.external_company_id AND sdk.dept_source='sdk_company' AND sdk.del_flag='0'
    SET map.dept_id=sdk.dept_id,
        map.remark=CONCAT(IFNULL(map.remark,''),IF(IFNULL(map.remark,'')='','','；'),'历史手工单位归并至SDK镜像'),
        map.update_by='sql:manual-dept-merge',map.update_time=NOW();

    UPDATE sys_dept d JOIN tmp_manual_dept_merge_map_20260710 m ON m.old_dept_id=d.dept_id
    SET d.status='1',d.del_flag='2',d.update_by='sql:manual-dept-merge',d.update_time=NOW();
    IF ROW_COUNT() <> 25 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge rolled back: merged node count mismatch';
    END IF;

    -- 再次遍历全部引用规则；任何旧单位残留都会触发整体回滚。
    SET v_remaining_count=0;
    SET v_done=0;
    OPEN rule_cursor;
    verify_loop: LOOP
        FETCH rule_cursor INTO v_rule_id,v_table_name,v_pk_column,v_dept_column,v_target_column,v_target_semantic,v_extra_condition;
        IF v_done=1 THEN LEAVE verify_loop; END IF;
        SET @remaining=0;
        SET @verify_sql=CONCAT(
            'SELECT COUNT(*) INTO @remaining FROM `',v_table_name,'` x JOIN tmp_manual_dept_merge_map_20260710 m ',
            'ON x.`',v_dept_column,'`=m.old_dept_id',
            IF(v_extra_condition IS NULL,'',CONCAT(' WHERE ',v_extra_condition))
        );
        PREPARE stmt FROM @verify_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
        SET v_remaining_count=v_remaining_count+IFNULL(@remaining,0);
    END LOOP;
    CLOSE rule_cursor;
    SELECT v_remaining_count + COUNT(*) INTO v_remaining_count
    FROM fe_company_dept_mapping map JOIN tmp_manual_dept_merge_map_20260710 m ON map.dept_id=m.old_dept_id;
    IF v_remaining_count <> 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Manual dept merge rolled back: old dept references remain';
    END IF;

    COMMIT;
    SELECT 'manual_dept_merge_completed' AS check_item,
           25 AS merged_node_count,
           (SELECT COUNT(*) FROM sys_dept_merge_reference_backup_20260710 WHERE merge_batch=@merge_batch) AS migrated_reference_count;
END$$
DELIMITER ;

CALL execute_manual_dept_merge_20260710();
DROP PROCEDURE IF EXISTS execute_manual_dept_merge_20260710;

SELECT d.dept_id,d.dept_name,d.status,d.del_flag,b.owner_target_dept_id,b.source_target_dept_id,b.match_evidence
FROM sys_dept d
JOIN sys_dept_merge_node_backup_20260710 b ON b.dept_id=d.dept_id AND b.merge_batch=@merge_batch
ORDER BY d.dept_id;

DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_ref_rule_20260710;
DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_merge_map_20260710;
DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_merge_seed_20260710;

