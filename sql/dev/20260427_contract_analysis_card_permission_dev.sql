-- 2026-04-27 contract analysis as homepage card permission (dev)
-- Only adjusts sys_menu permission data. No table structure changes.

SET @contract_analysis_menu_name := CONVERT(UNHEX('e59088e5908ce794a8e688b7e58886e69e90') USING utf8mb4);
SET @contract_analysis_remark := CONVERT(UNHEX('e9a696e9a1b52de59088e5908ce794a8e688b7e58886e69e90e58da1e78987e69d83e99990') USING utf8mb4);
SET @alarm_card_menu_name := CONVERT(UNHEX('e5be85e5a484e79086e68aa5e8ada6') USING utf8mb4);
SET @alarm_card_remark := CONVERT(UNHEX('e9a696e9a1b52de5be85e5a484e79086e68aa5e8ada6e58da1e78987e69d83e99990') USING utf8mb4);
SET @maintenance_card_menu_name := CONVERT(UNHEX('e69cace69c88e7bbb4e68aa4') USING utf8mb4);
SET @maintenance_card_remark := CONVERT(UNHEX('e9a696e9a1b52de69cace69c88e7bbb4e68aa4e58da1e78987e69d83e99990') USING utf8mb4);
SET @dashboard_permission_menu_name := CONVERT(UNHEX('e9a696e9a1b5e58da1e78987e58886e69e90') USING utf8mb4);
SET @dashboard_permission_remark := CONVERT(UNHEX('e9a696e9a1b5e58da1e78987e58886e69e90e58886e7bb84') USING utf8mb4);

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

UPDATE sys_menu
SET menu_name = @dashboard_permission_menu_name,
    parent_id = 0,
    order_num = 99,
    path = 'dashboardPermission',
    component = NULL,
    query = NULL,
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'M',
    visible = '1',
    status = '0',
    perms = '',
    icon = '#',
    remark = @dashboard_permission_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @dashboard_permission_menu_id IS NOT NULL
  AND menu_id = @dashboard_permission_menu_id;

SET @analysis_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'system:contract:analysis'
     OR component = 'system/contract/analysis'
  ORDER BY menu_id
  LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @contract_analysis_menu_name, IFNULL(@dashboard_permission_menu_id, 0), 1, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'system:contract:analysis', '#',
  'admin', NOW(), 'admin', NOW(), @contract_analysis_remark
FROM dual
WHERE @analysis_menu_id IS NULL;

SET @analysis_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'system:contract:analysis'
     OR component = 'system/contract/analysis'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @contract_analysis_menu_name,
    parent_id = IFNULL(@dashboard_permission_menu_id, 0),
    order_num = 1,
    path = '',
    component = NULL,
    query = NULL,
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'F',
    visible = '0',
    status = '0',
    perms = 'system:contract:analysis',
    icon = '#',
    remark = @contract_analysis_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @analysis_menu_id IS NOT NULL
  AND menu_id = @analysis_menu_id;

SET @alarm_card_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'dashboard:card:alarm'
  ORDER BY menu_id
  LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @alarm_card_menu_name, IFNULL(@dashboard_permission_menu_id, 0), 2, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'dashboard:card:alarm', '#',
  'admin', NOW(), 'admin', NOW(), @alarm_card_remark
FROM dual
WHERE @alarm_card_menu_id IS NULL;

SET @alarm_card_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'dashboard:card:alarm'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @alarm_card_menu_name,
    parent_id = IFNULL(@dashboard_permission_menu_id, 0),
    order_num = 2,
    path = '',
    component = NULL,
    query = NULL,
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'F',
    visible = '0',
    status = '0',
    perms = 'dashboard:card:alarm',
    icon = '#',
    remark = @alarm_card_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @alarm_card_menu_id IS NOT NULL
  AND menu_id = @alarm_card_menu_id;

SET @maintenance_card_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'dashboard:card:maintenance'
  ORDER BY menu_id
  LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @maintenance_card_menu_name, IFNULL(@dashboard_permission_menu_id, 0), 3, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'dashboard:card:maintenance', '#',
  'admin', NOW(), 'admin', NOW(), @maintenance_card_remark
FROM dual
WHERE @maintenance_card_menu_id IS NULL;

SET @maintenance_card_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'dashboard:card:maintenance'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @maintenance_card_menu_name,
    parent_id = IFNULL(@dashboard_permission_menu_id, 0),
    order_num = 3,
    path = '',
    component = NULL,
    query = NULL,
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'F',
    visible = '0',
    status = '0',
    perms = 'dashboard:card:maintenance',
    icon = '#',
    remark = @maintenance_card_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @maintenance_card_menu_id IS NOT NULL
  AND menu_id = @maintenance_card_menu_id;
