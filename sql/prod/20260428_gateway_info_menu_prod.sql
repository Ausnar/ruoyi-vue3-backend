-- 2026-04-28 gateway information menu (prod)
-- Adds the SDK gateway master-data page under Device Management. No table structure changes.

SET NAMES utf8mb4;

SET @gateway_menu_name := CONVERT(UNHEX('e7bd91e585b3e4bfa1e681af') USING utf8mb4);
SET @gateway_menu_remark := CONVERT(UNHEX('e8aebee5a487e7aea1e790862d53444be7bd91e585b3e4b8bbe695b0e68daee58faae8afbbe9a1b5') USING utf8mb4);
SET @gateway_query_name := CONVERT(UNHEX('e7bd91e585b3e4bfa1e681afe69fa5e8afa2') USING utf8mb4);
SET @gateway_query_remark := CONVERT(UNHEX('e8aebee5a487e7aea1e790862de7bd91e585b3e4bfa1e681afe69fa5e8afa2') USING utf8mb4);
SET @gateway_export_name := CONVERT(UNHEX('e7bd91e585b3e4bfa1e681afe5afbce587ba') USING utf8mb4);
SET @gateway_export_remark := CONVERT(UNHEX('e8aebee5a487e7aea1e790862de7bd91e585b3e4bfa1e681afe5afbce587ba') USING utf8mb4);

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
  @gateway_menu_name, @device_menu_id, 2, 'gateway', 'manage/gateway/index', NULL, '',
  1, 0, 'C', '0', '0', 'manage:gateway:list', 'connection',
  'admin', NOW(), 'admin', NOW(), @gateway_menu_remark
FROM dual
WHERE @device_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'manage/gateway/index');

SET @gateway_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE component = 'manage/gateway/index'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @gateway_menu_name,
    parent_id = @device_menu_id,
    order_num = 2,
    path = 'gateway',
    component = 'manage/gateway/index',
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'manage:gateway:list',
    icon = 'connection',
    remark = @gateway_menu_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @device_menu_id IS NOT NULL
  AND @gateway_menu_id IS NOT NULL
  AND menu_id = @gateway_menu_id;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  menu_name, @gateway_menu_id, order_num, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', perms, '#',
  'admin', NOW(), 'admin', NOW(), remark
FROM (
  SELECT @gateway_query_name AS menu_name, 1 AS order_num, 'manage:gateway:query' AS perms, @gateway_query_remark AS remark
  UNION ALL SELECT @gateway_export_name, 2, 'manage:gateway:export', @gateway_export_remark
) t
WHERE @gateway_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.parent_id = @gateway_menu_id AND s.perms = t.perms);

UPDATE sys_menu
SET menu_name = @gateway_query_name,
    parent_id = @gateway_menu_id,
    order_num = 1,
    path = '',
    component = NULL,
    route_name = '',
    menu_type = 'F',
    visible = '0',
    status = '0',
    icon = '#',
    remark = @gateway_query_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @gateway_menu_id IS NOT NULL
  AND perms = 'manage:gateway:query';

UPDATE sys_menu
SET menu_name = @gateway_export_name,
    parent_id = @gateway_menu_id,
    order_num = 2,
    path = '',
    component = NULL,
    route_name = '',
    menu_type = 'F',
    visible = '0',
    status = '0',
    icon = '#',
    remark = @gateway_export_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @gateway_menu_id IS NOT NULL
  AND perms = 'manage:gateway:export';

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (
    m.component = 'manage/gateway/index'
    OR m.perms IN ('manage:gateway:list', 'manage:gateway:query', 'manage:gateway:export')
  )
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = 1
      AND rm.menu_id = m.menu_id
  );
