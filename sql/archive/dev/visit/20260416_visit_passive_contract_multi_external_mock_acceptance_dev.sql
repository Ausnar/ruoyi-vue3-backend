SET NAMES utf8mb4;

SET @mock_operator := 'mock_contract_multi_external';
SET @mock_owner_user_name := 'zjw';
SET @mock_event_remark := 'V2 contract multi external candidate acceptance';
SET @mock_fire_point_code := 'V2-CONTRACT-3249-MOCK-FP';
SET @mock_fire_point_name := 'V2 contract ext3249 mock fp';
SET @mock_target_dept_id := 202;
SET @mock_external_company_a_id := 3247;
SET @mock_external_company_b_id := 3249;
SET @mock_to_longitude := 120.3213500;
SET @mock_to_latitude := 31.4757000;
SET @mock_from_longitude := 120.3013500;
SET @mock_from_latitude := 31.4677000;
SET @mock_distance_m := 2100.00;
SET @mock_trigger_time := NOW();
SET @mock_old_gps_time := DATE_SUB(@mock_trigger_time, INTERVAL 30 MINUTE);

SET @mock_owner_user_id := (
  SELECT user_id
  FROM sys_user
  WHERE user_name = @mock_owner_user_name
    AND del_flag = '0'
  ORDER BY user_id
  LIMIT 1
);

SET @mock_owner_dept_id := (
  SELECT dept_id
  FROM sys_user
  WHERE user_id = @mock_owner_user_id
  LIMIT 1
);

SET @mock_role_id := (
  SELECT role_id
  FROM sys_role
  WHERE role_key = 'visit_passive_handler'
    AND del_flag = '0'
  ORDER BY role_id
  LIMIT 1
);

SET @mock_gateway_id := (
  SELECT gateway_id
  FROM fe_gateway
  WHERE dept_id = @mock_target_dept_id
    AND external_company_id = @mock_external_company_a_id
    AND del_flag = '0'
    AND gps_longitude IS NOT NULL
    AND gps_latitude IS NOT NULL
  ORDER BY gateway_id
  LIMIT 1
);

SET @mock_external_tbox_id := (
  SELECT external_tbox_id
  FROM fe_gateway
  WHERE gateway_id = @mock_gateway_id
  LIMIT 1
);

SET @mock_event_dept_id := (
  SELECT dept_id
  FROM fe_gateway
  WHERE gateway_id = @mock_gateway_id
  LIMIT 1
);

SET @mock_fire_point_a_id := (
  SELECT fire_point_id
  FROM fe_fire_point
  WHERE external_company_id = @mock_external_company_a_id
    AND dept_id = @mock_target_dept_id
    AND longitude IS NOT NULL
    AND latitude IS NOT NULL
    AND del_flag = '0'
  ORDER BY fire_point_id
  LIMIT 1
);

SET @mock_fire_point_a_name := (
  SELECT fire_point_name
  FROM fe_fire_point
  WHERE fire_point_id = @mock_fire_point_a_id
  LIMIT 1
);

SET @mock_external_company_a_name := (
  SELECT external_company_name
  FROM fe_company_dept_mapping
  WHERE dept_id = @mock_target_dept_id
    AND external_company_id = @mock_external_company_a_id
  LIMIT 1
);

SET @mock_external_company_b_name := (
  SELECT external_company_name
  FROM fe_company_dept_mapping
  WHERE dept_id = @mock_target_dept_id
    AND external_company_id = @mock_external_company_b_id
  LIMIT 1
);

SET @mock_target_dept_name := (
  SELECT dept_name
  FROM sys_dept
  WHERE dept_id = @mock_target_dept_id
  LIMIT 1
);

INSERT INTO fe_fire_point (
  external_station_id, station_number, station_type, external_company_id, source_dept_id,
  sync_status, last_sync_time, fire_point_code, fire_point_name, dept_id, point_type,
  longitude, latitude, sort_order, status, create_by, create_time, update_by, update_time, remark, del_flag
)
SELECT
  9000003249, @mock_fire_point_code, 'D', @mock_external_company_b_id, @mock_target_dept_id,
  'manual', NOW(), @mock_fire_point_code, @mock_fire_point_name, @mock_target_dept_id, 'D',
  120.321200, 31.475900, 0, '0', @mock_operator, NOW(), @mock_operator, NOW(),
  'V2 contract multi external candidate mock', '0'
FROM dual
WHERE NOT EXISTS (
  SELECT 1
  FROM fe_fire_point
  WHERE fire_point_code = @mock_fire_point_code
);

UPDATE fe_fire_point
SET external_company_id = @mock_external_company_b_id,
    source_dept_id = @mock_target_dept_id,
    dept_id = @mock_target_dept_id,
    longitude = 120.321200,
    latitude = 31.475900,
    status = '0',
    update_by = @mock_operator,
    update_time = NOW(),
    remark = 'V2 contract multi external candidate mock',
    del_flag = '0'
WHERE fire_point_code = @mock_fire_point_code;

SET @mock_fire_point_b_id := (
  SELECT fire_point_id
  FROM fe_fire_point
  WHERE fire_point_code = @mock_fire_point_code
  LIMIT 1
);

INSERT INTO sys_user_role (user_id, role_id)
SELECT @mock_owner_user_id, @mock_role_id
FROM dual
WHERE @mock_owner_user_id IS NOT NULL
  AND @mock_role_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_user_role
    WHERE user_id = @mock_owner_user_id
      AND role_id = @mock_role_id
  );

INSERT INTO fe_visit_owner_assign (
  target_type, target_id, owner_user_id, owner_dept_id, status,
  create_by, create_time, update_by, update_time, remark
)
VALUES (
  'contract_dept', @mock_target_dept_id, @mock_owner_user_id, @mock_owner_dept_id, '0',
  @mock_operator, NOW(), @mock_operator, NOW(), 'V2 contract multi external candidate owner'
)
ON DUPLICATE KEY UPDATE
  owner_user_id = VALUES(owner_user_id),
  owner_dept_id = VALUES(owner_dept_id),
  status = '0',
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

SET @mock_old_event_id := (
  SELECT event_id
  FROM fe_visit_passive_event
  WHERE remark = @mock_event_remark
  ORDER BY event_id DESC
  LIMIT 1
);

SET @mock_old_visit_id := (
  SELECT visit_id
  FROM fe_visit_passive_event
  WHERE event_id = @mock_old_event_id
  LIMIT 1
);

DELETE FROM fe_visit_apply_log
WHERE visit_id = @mock_old_visit_id;

DELETE FROM fe_visit_apply
WHERE visit_id = @mock_old_visit_id
  AND source_type = 'gateway_displacement';

DELETE FROM fe_visit_passive_event
WHERE remark = @mock_event_remark;

DELETE FROM fe_gateway_gps_history
WHERE gateway_id = @mock_gateway_id
  AND create_by = @mock_operator;

INSERT INTO fe_gateway_gps_history (
  gateway_id, external_tbox_id, dept_id, gps_longitude, gps_latitude,
  gps_time, sync_time, create_by, create_time
)
VALUES
(
  @mock_gateway_id, @mock_external_tbox_id, @mock_event_dept_id, @mock_from_longitude, @mock_from_latitude,
  @mock_old_gps_time, @mock_old_gps_time, @mock_operator, NOW()
),
(
  @mock_gateway_id, @mock_external_tbox_id, @mock_event_dept_id, @mock_to_longitude, @mock_to_latitude,
  @mock_trigger_time, @mock_trigger_time, @mock_operator, NOW()
);

INSERT INTO fe_visit_passive_event (
  gateway_id, dept_id, fire_point_id, from_longitude, from_latitude, to_longitude, to_latitude,
  distance_m, trigger_time, candidate_summary, status, create_by, create_time, update_by, update_time, remark
)
VALUES (
  @mock_gateway_id, @mock_event_dept_id, @mock_fire_point_a_id, @mock_from_longitude, @mock_from_latitude,
  @mock_to_longitude, @mock_to_latitude, @mock_distance_m, @mock_trigger_time,
  CAST(
    JSON_ARRAY(
      JSON_OBJECT(
        'targetType', 'contract_dept',
        'targetId', @mock_target_dept_id,
        'targetName', @mock_target_dept_name,
        'distanceM', 25.00,
        'firePointId', @mock_fire_point_a_id,
        'firePointName', @mock_fire_point_a_name,
        'externalCompanyId', @mock_external_company_a_id,
        'externalCompanyName', @mock_external_company_a_name
      ),
      JSON_OBJECT(
        'targetType', 'contract_dept',
        'targetId', @mock_target_dept_id,
        'targetName', @mock_target_dept_name,
        'distanceM', 32.00,
        'firePointId', @mock_fire_point_b_id,
        'firePointName', @mock_fire_point_name,
        'externalCompanyId', @mock_external_company_b_id,
        'externalCompanyName', @mock_external_company_b_name
      )
    ) AS CHAR
  ),
  'pending_confirm', @mock_operator, NOW(), @mock_operator, NOW(), @mock_event_remark
);

SELECT 'mock_target_dept' AS item, @mock_target_dept_id AS dept_id, @mock_target_dept_name AS dept_name;
SELECT 'mock_external_company_a' AS item, @mock_external_company_a_id AS external_company_id, @mock_external_company_a_name AS external_company_name, @mock_fire_point_a_id AS fire_point_id, @mock_fire_point_a_name AS fire_point_name;
SELECT 'mock_external_company_b' AS item, @mock_external_company_b_id AS external_company_id, @mock_external_company_b_name AS external_company_name, @mock_fire_point_b_id AS fire_point_id, @mock_fire_point_name AS fire_point_name;
SELECT event_id, gateway_id, dept_id, fire_point_id, status, trigger_time, remark
FROM fe_visit_passive_event
WHERE remark = @mock_event_remark
ORDER BY event_id DESC
LIMIT 1;
