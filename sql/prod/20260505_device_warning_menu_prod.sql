-- 2026-05-05 device warning readonly menu (prod)
-- Adds readonly device warning page under Device Management. No table structure changes.

SET NAMES utf8mb4;

SET @device_warning_menu_name := CONVERT(UNHEX('e8aebee5a487e9a284e8ada6') USING utf8mb4);
SET @device_warning_menu_remark := CONVERT(UNHEX('e8aebee5a487e7aea1e790862de8aebee5a487e9a284e8ada6') USING utf8mb4);
SET @device_warning_query_name := CONVERT(UNHEX('e8aebee5a487e9a284e8ada6e69fa5e8afa2') USING utf8mb4);
SET @device_warning_query_remark := CONVERT(UNHEX('e8aebee5a487e7aea1e790862de8aebee5a487e9a284e8ada6e69fa5e8afa2') USING utf8mb4);

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
  @device_warning_menu_name, @device_menu_id, 4, 'deviceWarning', 'manage/deviceWarning/index', NULL, '',
  1, 0, 'C', '0', '0', 'manage:deviceWarning:list', 'warning',
  'admin', NOW(), 'admin', NOW(), @device_warning_menu_remark
FROM dual
WHERE @device_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'manage/deviceWarning/index');

SET @device_warning_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE component = 'manage/deviceWarning/index'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @device_warning_menu_name,
    parent_id = @device_menu_id,
    order_num = 4,
    path = 'deviceWarning',
    component = 'manage/deviceWarning/index',
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'manage:deviceWarning:list',
    icon = 'warning',
    remark = @device_warning_menu_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @device_menu_id IS NOT NULL
  AND @device_warning_menu_id IS NOT NULL
  AND menu_id = @device_warning_menu_id;

UPDATE sys_menu
SET order_num = order_num + 1,
    update_by = 'admin',
    update_time = NOW()
WHERE parent_id = @device_menu_id
  AND component = 'manage/deviceReport/index'
  AND order_num <= 4;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @device_warning_query_name, @device_warning_menu_id, 1, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'manage:deviceWarning:query', '#',
  'admin', NOW(), 'admin', NOW(), @device_warning_query_remark
FROM dual
WHERE @device_warning_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE parent_id = @device_warning_menu_id
      AND perms = 'manage:deviceWarning:query'
  );

UPDATE sys_menu
SET menu_name = @device_warning_query_name,
    parent_id = @device_warning_menu_id,
    order_num = 1,
    path = '',
    component = NULL,
    route_name = '',
    menu_type = 'F',
    visible = '0',
    status = '0',
    icon = '#',
    remark = @device_warning_query_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @device_warning_menu_id IS NOT NULL
  AND perms = 'manage:deviceWarning:query';

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (
    m.component = 'manage/deviceWarning/index'
    OR m.perms IN ('manage:deviceWarning:list', 'manage:deviceWarning:query')
  )
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = 1
      AND rm.menu_id = m.menu_id
  );
