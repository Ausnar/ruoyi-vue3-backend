-- 历史手工单位合并到 SDK 镜像单位预览（prod）
-- 只读脚本：固定旧单位与 SDK 镜像目标，并区分业务归属、凭证来源、合同目标三类语义。

DROP TEMPORARY TABLE IF EXISTS tmp_manual_dept_merge_map_20260710;
CREATE TEMPORARY TABLE tmp_manual_dept_merge_map_20260710 (
    old_dept_id BIGINT NOT NULL PRIMARY KEY,
    owner_target_dept_id BIGINT NOT NULL,
    source_target_dept_id BIGINT NOT NULL,
    contract_target_dept_id BIGINT NOT NULL,
    match_evidence VARCHAR(128) NOT NULL
);

INSERT INTO tmp_manual_dept_merge_map_20260710 VALUES
    (201, 265, 265, 265, '历史映射外部单位3264+SDK授权根'),
    (227, 266, 266, 266, '历史映射外部单位42'),
    (264, 267, 267, 267, '历史映射外部单位33'),
    (229, 268, 268, 268, '历史映射外部单位16'),
    (263, 269, 269, 269, '历史映射外部单位43'),
    (228, 270, 270, 270, '历史映射外部单位3182'),
    (202, 272, 272, 272, 'SDK授权根+完整无锡镜像树'),
    (221, 273, 273, 273, '唯一同名+父级镜像272'),
    (222, 274, 274, 274, '唯一同名+父级镜像273'),
    (220, 275, 275, 275, '唯一同名+父级镜像272'),
    (203, 276, 276, 276, 'SDK授权根+完整常州镜像树'),
    (223, 277, 277, 277, '历史映射外部单位3203'),
    (224, 278, 278, 278, '历史映射外部单位3243'),
    (225, 279, 279, 279, '历史映射外部单位3244'),
    (226, 280, 280, 280, '唯一同名+父级镜像276'),
    (204, 281, 281, 281, '唯一同名SDK授权根；修正误挂默克树'),
    (206, 282, 281, 281, '业务单位外部3265；历史凭证迁到授权根281'),
    (207, 283, 283, 283, '唯一同名SDK授权根'),
    (208, 285, 285, 285, '唯一同名+父级镜像283'),
    (209, 286, 283, 283, '业务单位外部3239；历史凭证迁到授权根283'),
    (212, 287, 287, 287, '历史映射外部单位3267+SDK授权根'),
    (213, 288, 288, 288, '唯一同名SDK授权根'),
    (215, 289, 289, 289, '唯一同名+父级镜像288'),
    (216, 290, 290, 290, '唯一同名+父级镜像288'),
    (214, 291, 291, 291, '唯一同名+父级镜像288');

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
