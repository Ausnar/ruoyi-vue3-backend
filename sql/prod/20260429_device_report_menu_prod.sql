-- 2026-04-29 device report menu (prod)
-- Adds the manual Word report page under Device Management. No table structure changes.

SET NAMES utf8mb4;

SET @device_report_menu_name := CONVERT(UNHEX('e8aebee5a487e68aa5e5918a') USING utf8mb4);
SET @device_report_menu_remark := CONVERT(UNHEX('e8aebee5a487e7aea1e790862de8aebee5a487e68aa5e5918ae88f9ce58d95') USING utf8mb4);
SET @device_report_export_name := CONVERT(UNHEX('e8aebee5a487e68aa5e5918ae5afbce587ba') USING utf8mb4);
SET @device_report_export_remark := CONVERT(UNHEX('e8aebee5a487e7aea1e790862de8aebee5a487e68aa5e5918ae5afbce587ba') USING utf8mb4);

SET @device_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE path = 'manage'
    AND menu_type = 'M'
  ORDER BY menu_id
  LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @device_report_menu_name, @device_menu_id, 4, 'deviceReport', 'manage/deviceReport/index', NULL, '',
  1, 0, 'C', '0', '0', 'manage:deviceReport:list', 'document',
  'admin', NOW(), 'admin', NOW(), @device_report_menu_remark
FROM dual
WHERE @device_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'manage/deviceReport/index');

SET @device_report_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE component = 'manage/deviceReport/index'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @device_report_menu_name,
    parent_id = @device_menu_id,
    order_num = 4,
    path = 'deviceReport',
    component = 'manage/deviceReport/index',
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'manage:deviceReport:list',
    icon = 'document',
    remark = @device_report_menu_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @device_menu_id IS NOT NULL
  AND @device_report_menu_id IS NOT NULL
  AND menu_id = @device_report_menu_id;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @device_report_export_name, @device_report_menu_id, 1, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'manage:deviceReport:export', '#',
  'admin', NOW(), 'admin', NOW(), @device_report_export_remark
FROM dual
WHERE @device_report_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE parent_id = @device_report_menu_id
      AND perms = 'manage:deviceReport:export'
  );

UPDATE sys_menu
SET menu_name = @device_report_export_name,
    parent_id = @device_report_menu_id,
    order_num = 1,
    path = '',
    component = NULL,
    route_name = '',
    menu_type = 'F',
    visible = '0',
    status = '0',
    icon = '#',
    remark = @device_report_export_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @device_report_menu_id IS NOT NULL
  AND perms = 'manage:deviceReport:export';

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (
    m.component = 'manage/deviceReport/index'
    OR m.perms IN ('manage:deviceReport:list', 'manage:deviceReport:export')
  )
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = 1
      AND rm.menu_id = m.menu_id
  );
