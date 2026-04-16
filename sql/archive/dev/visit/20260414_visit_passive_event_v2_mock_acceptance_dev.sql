SET NAMES utf8mb4;

SET @mock_customer_name := 'V2被动拜访验收客户';
SET @mock_contact_person := '被动验收联系人';
SET @mock_contact_phone := '13800000001';
SET @mock_address := '苏州安众道 V2 被动拜访验收点';
SET @mock_longitude := 120.6480000;
SET @mock_latitude := 31.4257000;
SET @mock_owner_user_name := 'zjw';
SET @mock_operator := 'mock_acceptance';
SET @mock_event_remark := 'V2模拟验收-独立客户被动事件';
SET @mock_customer_remark := 'V2模拟验收客户';
SET @mock_owner_remark := 'V2模拟验收负责人';

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
  WHERE del_flag = '0'
    AND dept_id IS NOT NULL
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

SET @mock_fire_point_id := (
  SELECT fire_point_id
  FROM fe_gateway
  WHERE gateway_id = @mock_gateway_id
  LIMIT 1
);

SET @mock_fire_point_name := (
  SELECT fire_point_name
  FROM fe_fire_point
  WHERE fire_point_id = @mock_fire_point_id
  LIMIT 1
);

SET @mock_to_longitude := (
  SELECT CAST(gps_longitude AS DECIMAL(12,7))
  FROM fe_gateway
  WHERE gateway_id = @mock_gateway_id
  LIMIT 1
);

SET @mock_to_latitude := (
  SELECT CAST(gps_latitude AS DECIMAL(12,7))
  FROM fe_gateway
  WHERE gateway_id = @mock_gateway_id
  LIMIT 1
);

SET @mock_from_longitude := ROUND(@mock_to_longitude - 0.0200000, 7);
SET @mock_from_latitude := ROUND(@mock_to_latitude - 0.0080000, 7);
SET @mock_distance_m := 2100.00;
SET @mock_trigger_time := NOW();
SET @mock_old_gps_time := DATE_SUB(@mock_trigger_time, INTERVAL 30 MINUTE);

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

INSERT INTO fe_visit_customer (
  customer_name, contact_person, contact_phone, address, longitude, latitude,
  dept_id, status, create_by, create_time, update_by, update_time, remark, del_flag
)
SELECT
  @mock_customer_name, @mock_contact_person, @mock_contact_phone, @mock_address, @mock_longitude, @mock_latitude,
  @mock_owner_dept_id, '0', @mock_operator, NOW(), @mock_operator, NOW(), @mock_customer_remark, '0'
FROM dual
WHERE NOT EXISTS (
  SELECT 1
  FROM fe_visit_customer
  WHERE customer_name = @mock_customer_name
    AND dept_id = @mock_owner_dept_id
    AND del_flag = '0'
);

UPDATE fe_visit_customer
SET contact_person = @mock_contact_person,
    contact_phone = @mock_contact_phone,
    address = @mock_address,
    longitude = @mock_longitude,
    latitude = @mock_latitude,
    status = '0',
    update_by = @mock_operator,
    update_time = NOW(),
    remark = @mock_customer_remark
WHERE customer_name = @mock_customer_name
  AND dept_id = @mock_owner_dept_id
  AND del_flag = '0';

SET @mock_customer_id := (
  SELECT customer_id
  FROM fe_visit_customer
  WHERE customer_name = @mock_customer_name
    AND dept_id = @mock_owner_dept_id
    AND del_flag = '0'
  ORDER BY customer_id
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
  'independent_customer', @mock_customer_id, @mock_owner_user_id, @mock_owner_dept_id, '0',
  @mock_operator, NOW(), @mock_operator, NOW(), @mock_owner_remark
)
ON DUPLICATE KEY UPDATE
  owner_user_id = VALUES(owner_user_id),
  owner_dept_id = VALUES(owner_dept_id),
  status = '0',
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

DELETE FROM fe_gateway_gps_history
WHERE gateway_id = @mock_gateway_id
  AND create_by = @mock_operator;

DELETE FROM fe_visit_apply_log
WHERE visit_id = @mock_old_visit_id;

DELETE FROM fe_visit_apply
WHERE visit_id = @mock_old_visit_id
  AND source_type = 'gateway_displacement';

DELETE FROM fe_visit_passive_event
WHERE remark = @mock_event_remark;

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
  distance_m, trigger_time, candidate_summary, status,
  create_by, create_time, update_by, update_time, remark
)
VALUES (
  @mock_gateway_id, @mock_event_dept_id, @mock_fire_point_id, @mock_from_longitude, @mock_from_latitude,
  @mock_to_longitude, @mock_to_latitude, @mock_distance_m, @mock_trigger_time,
  CAST(
    JSON_ARRAY(
      JSON_OBJECT(
        'targetType', 'independent_customer',
        'targetId', @mock_customer_id,
        'targetName', @mock_customer_name,
        'distanceM', 120.00,
        'firePointId', @mock_fire_point_id,
        'firePointName', @mock_fire_point_name
      )
    ) AS CHAR
  ),
  'pending_confirm',
  @mock_operator, NOW(), @mock_operator, NOW(), @mock_event_remark
);

SELECT 'mock_customer' AS item, @mock_customer_id AS id, @mock_customer_name AS name;
SELECT 'mock_owner_user' AS item, @mock_owner_user_id AS user_id, @mock_owner_user_name AS user_name, @mock_owner_dept_id AS dept_id;
SELECT 'mock_gateway' AS item, @mock_gateway_id AS gateway_id, @mock_fire_point_id AS fire_point_id, @mock_event_dept_id AS dept_id;
SELECT event_id, gateway_id, dept_id, fire_point_id, status, trigger_time, remark
FROM fe_visit_passive_event
WHERE remark = @mock_event_remark
ORDER BY event_id DESC
LIMIT 1;
