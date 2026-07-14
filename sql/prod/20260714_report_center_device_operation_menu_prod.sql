-- 2026-07-14 report center device operation report menu
-- Adds the device operation report page and export permission. No business table structure changes.

SET NAMES utf8mb4;

SET @device_operation_name := CONVERT(UNHEX('e8aebee5a487e8bf90e8a18ce68aa5e5918a') USING utf8mb4);
SET @device_operation_remark := CONVERT(UNHEX('e68aa5e8a1a8e4b8ade5bf832de8aebee5a487e8bf90e8a18ce68aa5e5918ae88f9ce58d95') USING utf8mb4);
SET @device_operation_export_name := CONVERT(UNHEX('e8aebee5a487e8bf90e8a18ce68aa5e5918ae5afbce587ba') USING utf8mb4);
SET @device_operation_export_remark := CONVERT(UNHEX('e68aa5e8a1a8e4b8ade5bf832de8aebee5a487e8bf90e8a18ce68aa5e5918ae5afbce587ba') USING utf8mb4);

SET @report_center_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE path = 'report' AND menu_type = 'M'
  ORDER BY menu_id
  LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @device_operation_name, @report_center_id, 3, 'deviceOperation', 'report/deviceOperation/index', NULL, '',
  1, 0, 'C', '0', '0', 'report:deviceOperation:list', '#',
  'admin', NOW(), 'admin', NOW(), @device_operation_remark
FROM dual
WHERE @report_center_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE component = 'report/deviceOperation/index'
  );

SET @device_operation_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE component = 'report/deviceOperation/index'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @device_operation_name,
    parent_id = @report_center_id,
    order_num = 3,
    path = 'deviceOperation',
    component = 'report/deviceOperation/index',
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'report:deviceOperation:list',
    icon = '#',
    remark = @device_operation_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @report_center_id IS NOT NULL
  AND @device_operation_id IS NOT NULL
  AND menu_id = @device_operation_id;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @device_operation_export_name, @device_operation_id, 1, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'report:deviceOperation:export', '#',
  'admin', NOW(), 'admin', NOW(), @device_operation_export_remark
FROM dual
WHERE @device_operation_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE parent_id = @device_operation_id
      AND perms = 'report:deviceOperation:export'
  );

UPDATE sys_menu
SET menu_name = @device_operation_export_name,
    parent_id = @device_operation_id,
    order_num = 1,
    path = '',
    component = NULL,
    route_name = '',
    menu_type = 'F',
    visible = '0',
    status = '0',
    perms = 'report:deviceOperation:export',
    icon = '#',
    remark = @device_operation_export_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @device_operation_id IS NOT NULL
  AND perms = 'report:deviceOperation:export';

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN sys_menu m
  ON (m.menu_id IN (@report_center_id, @device_operation_id)
   OR m.perms = 'report:deviceOperation:export')
WHERE r.role_id IN (1, 2)
  AND r.status = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = r.role_id
      AND rm.menu_id = m.menu_id
  );
