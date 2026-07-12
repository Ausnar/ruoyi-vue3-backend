-- 历史手工单位合并到 SDK 镜像单位预览（dev）
-- 只读脚本：固定旧单位与 SDK 镜像目标，并区分业务归属、凭证来源、合同目标三类语义。

USE ruoyi_ai_dev;

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

SELECT 'dynamic_seed_resolution' AS check_item,
       seed.old_dept_name,
       (SELECT COUNT(*) FROM sys_dept d
        WHERE d.dept_name=seed.old_dept_name AND d.dept_source='manual' AND d.del_flag='0') AS old_manual_match_count,
       (SELECT COUNT(*) FROM sys_dept d
        WHERE d.external_company_id=seed.owner_external_company_id AND d.dept_source='sdk_company' AND d.del_flag='0') AS owner_target_match_count,
       (SELECT COUNT(*) FROM sys_dept d
        WHERE d.external_company_id=seed.source_external_company_id AND d.dept_source='sdk_company' AND d.del_flag='0') AS source_target_match_count,
       CASE WHEN EXISTS (
           SELECT 1 FROM tmp_manual_dept_merge_map_20260710 m
           JOIN sys_dept old_dept ON old_dept.dept_id=m.old_dept_id
           WHERE old_dept.dept_name=seed.old_dept_name
       ) THEN 'ready' ELSE 'blocked' END AS resolution_status
FROM tmp_manual_dept_merge_seed_20260710 seed
ORDER BY seed.old_dept_name;

DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_merge_impact_20260710;
CREATE TEMPORARY TABLE tmp_manual_dept_merge_impact_20260710 (
    old_dept_id BIGINT NOT NULL,
    ref_table VARCHAR(64) NOT NULL,
    ref_column VARCHAR(64) NOT NULL,
    target_semantic VARCHAR(32) NOT NULL,
    ref_count BIGINT NOT NULL,
    PRIMARY KEY (old_dept_id, ref_table, ref_column)
);

-- 当前业务归属与历史业务归属。
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'sys_user','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN sys_user x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'sys_role_dept','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN sys_role_dept x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'sys_notice_dept','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN sys_notice_dept x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_fire_point','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_fire_point x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_fire_point_device_snapshot','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_fire_point_device_snapshot x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_gateway','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_gateway x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_gateway_gps_history','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_gateway_gps_history x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_gateway_manual_record','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_gateway_manual_record x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_sensor','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_sensor x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_sensor_manual_record','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_sensor_manual_record x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_extinguisher','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_extinguisher x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_iot_card','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_iot_card x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_alarm_record','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_alarm_record x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_device_warning','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_device_warning x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_visit_apply','applicant_dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_visit_apply x ON x.applicant_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_visit_apply_log','operator_dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_visit_apply_log x ON x.operator_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_visit_approve_config','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_visit_approve_config x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_visit_customer','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_visit_customer x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_visit_owner_assign','owner_dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_visit_owner_assign x ON x.owner_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_visit_passive_event','dept_id','owner',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_visit_passive_event x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;

-- 凭证来源语义。
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'sys_dept_api_config','dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN sys_dept_api_config x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_api_config_company_scope','source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_api_config_company_scope x ON x.source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_external_company','last_source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_external_company x ON x.last_source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_sdk_sync_log','dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_sdk_sync_log x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_fire_point','source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_fire_point x ON x.source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_fire_point_device_snapshot','source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_fire_point_device_snapshot x ON x.source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_gateway','source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_gateway x ON x.source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_gateway_manual_record','source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_gateway_manual_record x ON x.source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_sensor','source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_sensor x ON x.source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_extinguisher','source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_extinguisher x ON x.source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_iot_card','source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_iot_card x ON x.source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_device_warning','source_dept_id','source',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_device_warning x ON x.source_dept_id=m.old_dept_id GROUP BY m.old_dept_id;

-- 合同目标语义与历史映射；旧映射目标按 external_company_id 直接归位，不按 old_dept_id 粗分。
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_visit_apply','contract_dept_id','contract',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_visit_apply x ON x.contract_dept_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_visit_owner_assign','target_id(contract_dept)','contract',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_visit_owner_assign x ON x.target_type='contract_dept' AND x.target_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_visit_passive_event','selected_target_id(contract_dept)','contract',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_visit_passive_event x ON x.selected_target_type='contract_dept' AND x.selected_target_id=m.old_dept_id GROUP BY m.old_dept_id;
INSERT INTO tmp_manual_dept_merge_impact_20260710 SELECT m.old_dept_id,'fe_company_dept_mapping','dept_id','external_identity',COUNT(*) FROM tmp_manual_dept_merge_map_20260710 m JOIN fe_company_dept_mapping x ON x.dept_id=m.old_dept_id GROUP BY m.old_dept_id;

SELECT 'merge_map_validation' AS check_item,
       m.old_dept_id, old_dept.dept_name AS old_dept_name, old_dept.parent_id,
       m.owner_target_dept_id, owner_target.dept_name AS owner_target_name,
       m.source_target_dept_id, source_target.dept_name AS source_target_name,
       m.contract_target_dept_id, contract_target.dept_name AS contract_target_name,
       m.match_evidence,
       CASE
           WHEN old_dept.dept_source <> 'manual' OR old_dept.del_flag <> '0' THEN 'invalid_old'
           WHEN owner_target.dept_source <> 'sdk_company' OR owner_target.del_flag <> '0' THEN 'invalid_owner_target'
           WHEN source_target.dept_source <> 'sdk_company' OR source_target.del_flag <> '0' THEN 'invalid_source_target'
           WHEN contract_target.dept_source <> 'sdk_company' OR contract_target.del_flag <> '0' THEN 'invalid_contract_target'
           ELSE 'ready'
       END AS validation_status
FROM tmp_manual_dept_merge_map_20260710 m
LEFT JOIN sys_dept old_dept ON old_dept.dept_id=m.old_dept_id
LEFT JOIN sys_dept owner_target ON owner_target.dept_id=m.owner_target_dept_id
LEFT JOIN sys_dept source_target ON source_target.dept_id=m.source_target_dept_id
LEFT JOIN sys_dept contract_target ON contract_target.dept_id=m.contract_target_dept_id
ORDER BY m.old_dept_id;

SELECT 'merge_impact_summary' AS check_item,
       m.old_dept_id, old_dept.dept_name AS old_dept_name,
       IFNULL(SUM(CASE WHEN i.target_semantic='owner' THEN i.ref_count ELSE 0 END),0) AS owner_refs,
       IFNULL(SUM(CASE WHEN i.target_semantic='source' THEN i.ref_count ELSE 0 END),0) AS source_refs,
       IFNULL(SUM(CASE WHEN i.target_semantic='contract' THEN i.ref_count ELSE 0 END),0) AS contract_refs,
       IFNULL(SUM(CASE WHEN i.target_semantic='external_identity' THEN i.ref_count ELSE 0 END),0) AS mapping_refs,
       IFNULL(SUM(i.ref_count),0) AS total_refs
FROM tmp_manual_dept_merge_map_20260710 m
INNER JOIN sys_dept old_dept ON old_dept.dept_id=m.old_dept_id
LEFT JOIN tmp_manual_dept_merge_impact_20260710 i ON i.old_dept_id=m.old_dept_id
GROUP BY m.old_dept_id, old_dept.dept_name
ORDER BY total_refs DESC, m.old_dept_id;

SELECT 'merge_impact_detail' AS check_item,
       i.old_dept_id, d.dept_name, i.ref_table, i.ref_column, i.target_semantic, i.ref_count
FROM tmp_manual_dept_merge_impact_20260710 i
INNER JOIN sys_dept d ON d.dept_id=i.old_dept_id
ORDER BY i.old_dept_id, i.target_semantic, i.ref_table, i.ref_column;

SELECT 'unmapped_active_manual_dept' AS check_item,
       d.dept_id, d.dept_name, d.parent_id, d.ancestors
FROM sys_dept d
LEFT JOIN tmp_manual_dept_merge_map_20260710 m ON m.old_dept_id=d.dept_id
WHERE d.dept_source='manual'
  AND d.del_flag='0'
  AND m.old_dept_id IS NULL
ORDER BY d.ancestors,d.order_num,d.dept_id;

SELECT 'mapping_without_sdk_identity_target' AS check_item,
       map.mapping_id,map.external_company_id,map.external_company_name,map.dept_id AS old_mapping_dept_id
FROM fe_company_dept_mapping map
LEFT JOIN sys_dept sdk
       ON sdk.external_company_id=map.external_company_id
      AND sdk.dept_source='sdk_company'
      AND sdk.del_flag='0'
WHERE sdk.dept_id IS NULL
ORDER BY map.mapping_id;

DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_merge_impact_20260710;
DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_merge_map_20260710;
DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_merge_seed_20260710;

