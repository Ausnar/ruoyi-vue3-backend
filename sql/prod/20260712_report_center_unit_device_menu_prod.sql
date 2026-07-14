-- 2026-07-12 report center and unit device report menu
-- Adds the first report center page. No business table structure changes.

SET NAMES utf8mb4;

SET @report_center_name := CONVERT(UNHEX('e68aa5e8a1a8e4b8ade5bf83') USING utf8mb4);
SET @report_center_remark := CONVERT(UNHEX('e68aa5e8a1a8e4b8ade5bf83e4b880e7baa7e88f9ce58d95') USING utf8mb4);
SET @unit_device_report_name := CONVERT(UNHEX('e58d95e4bd8de8aebee5a487e68aa5e5918a') USING utf8mb4);
SET @unit_device_report_remark := CONVERT(UNHEX('e68aa5e8a1a8e4b8ade5bf832de58d95e4bd8de8aebee5a487e68aa5e5918ae88f9ce58d95') USING utf8mb4);
SET @unit_device_export_name := CONVERT(UNHEX('e58d95e4bd8de8aebee5a487e68aa5e5918ae5afbce587ba') USING utf8mb4);
SET @unit_device_export_remark := CONVERT(UNHEX('e68aa5e8a1a8e4b8ade5bf832de58d95e4bd8de8aebee5a487e68aa5e5918ae5afbce587ba') USING utf8mb4);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @report_center_name, 0, 7, 'report', NULL, NULL, '',
  1, 0, 'M', '0', '0', '', 'chart',
  'admin', NOW(), 'admin', NOW(), @report_center_remark
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu
  WHERE path = 'report' AND menu_type = 'M'
);

SET @report_center_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE path = 'report' AND menu_type = 'M'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @report_center_name,
    parent_id = 0,
    order_num = 7,
    component = NULL,
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'chart',
    remark = @report_center_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @report_center_id;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @unit_device_report_name, @report_center_id, 1, 'unitDevice', 'report/unitDevice/index', NULL, '',
  1, 0, 'C', '0', '0', 'report:unitDevice:list', '#',
  'admin', NOW(), 'admin', NOW(), @unit_device_report_remark
FROM dual
WHERE @report_center_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE component = 'report/unitDevice/index'
  );

SET @unit_device_report_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE component = 'report/unitDevice/index'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @unit_device_report_name,
    parent_id = @report_center_id,
    order_num = 1,
    path = 'unitDevice',
    component = 'report/unitDevice/index',
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'report:unitDevice:list',
    icon = '#',
    remark = @unit_device_report_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @report_center_id IS NOT NULL
  AND @unit_device_report_id IS NOT NULL
  AND menu_id = @unit_device_report_id;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @unit_device_export_name, @unit_device_report_id, 1, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'report:unitDevice:export', '#',
  'admin', NOW(), 'admin', NOW(), @unit_device_export_remark
FROM dual
WHERE @unit_device_report_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE parent_id = @unit_device_report_id
      AND perms = 'report:unitDevice:export'
  );

UPDATE sys_menu
SET menu_name = @unit_device_export_name,
    parent_id = @unit_device_report_id,
    order_num = 1,
    path = '',
    component = NULL,
    route_name = '',
    menu_type = 'F',
    visible = '0',
    status = '0',
    perms = 'report:unitDevice:export',
    icon = '#',
    remark = @unit_device_export_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @unit_device_report_id IS NOT NULL
  AND perms = 'report:unitDevice:export';

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN sys_menu m
  ON (m.menu_id IN (@report_center_id, @unit_device_report_id)
   OR m.perms = 'report:unitDevice:export')
WHERE r.role_id IN (1, 2)
  AND r.status = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = r.role_id
      AND rm.menu_id = m.menu_id
  );
