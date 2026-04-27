-- 2026-04-27 homepage business unit map card permission (prod)
-- Only adjusts sys_menu permission data. No table structure changes.

SET @dashboard_permission_menu_name := CONVERT(UNHEX('e9a696e9a1b5e58da1e78987e58886e69e90') USING utf8mb4);
SET @dashboard_permission_remark := CONVERT(UNHEX('e9a696e9a1b5e58da1e78987e58886e69e90e58886e7bb84') USING utf8mb4);
SET @map_card_menu_name := CONVERT(UNHEX('e4b89ae58aa1e58d95e4bd8de59cb0e59bbe') USING utf8mb4);
SET @map_card_remark := CONVERT(UNHEX('e9a696e9a1b52de4b89ae58aa1e58d95e4bd8de59cb0e59bbee58da1e78987e69d83e99990') USING utf8mb4);

SET @dashboard_permission_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE path = 'dashboardPermission'
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
  @dashboard_permission_menu_name, 0, 99, 'dashboardPermission', NULL, NULL, '',
  1, 0, 'M', '1', '0', '', '#',
  'admin', NOW(), 'admin', NOW(), @dashboard_permission_remark
FROM dual
WHERE @dashboard_permission_menu_id IS NULL;

SET @dashboard_permission_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE path = 'dashboardPermission'
    AND menu_type = 'M'
  ORDER BY menu_id
  LIMIT 1
);

SET @map_card_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'dashboard:card:map'
  ORDER BY menu_id
  LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @map_card_menu_name, IFNULL(@dashboard_permission_menu_id, 0), 4, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'dashboard:card:map', '#',
  'admin', NOW(), 'admin', NOW(), @map_card_remark
FROM dual
WHERE @map_card_menu_id IS NULL;

SET @map_card_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'dashboard:card:map'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @map_card_menu_name,
    parent_id = IFNULL(@dashboard_permission_menu_id, 0),
    order_num = 4,
    path = '',
    component = NULL,
    query = NULL,
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'F',
    visible = '0',
    status = '0',
    perms = 'dashboard:card:map',
    icon = '#',
    remark = @map_card_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @map_card_menu_id IS NOT NULL
  AND menu_id = @map_card_menu_id;
