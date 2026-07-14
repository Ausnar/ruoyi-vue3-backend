-- 2026-07-13 report center runtime detail menu
-- Adds the device runtime detail page and export permission. No business table structure changes.

SET NAMES utf8mb4;

SET @runtime_detail_name := CONVERT(UNHEX('e8aebee5a487e8bf90e8a18ce6988ee7bb86') USING utf8mb4);
SET @runtime_detail_remark := CONVERT(UNHEX('e68aa5e8a1a8e4b8ade5bf832de8aebee5a487e8bf90e8a18ce6988ee7bb86e88f9ce58d95') USING utf8mb4);
SET @runtime_export_name := CONVERT(UNHEX('e8aebee5a487e8bf90e8a18ce6988ee7bb86e5afbce587ba') USING utf8mb4);
SET @runtime_export_remark := CONVERT(UNHEX('e68aa5e8a1a8e4b8ade5bf832de8aebee5a487e8bf90e8a18ce6988ee7bb86e5afbce587ba') USING utf8mb4);

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
  @runtime_detail_name, @report_center_id, 2, 'runtimeDetail', 'report/runtimeDetail/index', NULL, '',
  1, 0, 'C', '0', '0', 'report:runtimeDetail:list', '#',
  'admin', NOW(), 'admin', NOW(), @runtime_detail_remark
FROM dual
WHERE @report_center_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE component = 'report/runtimeDetail/index'
  );

SET @runtime_detail_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE component = 'report/runtimeDetail/index'
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET menu_name = @runtime_detail_name,
    parent_id = @report_center_id,
    order_num = 2,
    path = 'runtimeDetail',
    component = 'report/runtimeDetail/index',
    route_name = '',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'report:runtimeDetail:list',
    icon = '#',
    remark = @runtime_detail_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @report_center_id IS NOT NULL
  AND @runtime_detail_id IS NOT NULL
  AND menu_id = @runtime_detail_id;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  @runtime_export_name, @runtime_detail_id, 1, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'report:runtimeDetail:export', '#',
  'admin', NOW(), 'admin', NOW(), @runtime_export_remark
FROM dual
WHERE @runtime_detail_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE parent_id = @runtime_detail_id
      AND perms = 'report:runtimeDetail:export'
  );

UPDATE sys_menu
SET menu_name = @runtime_export_name,
    parent_id = @runtime_detail_id,
    order_num = 1,
    path = '',
    component = NULL,
    route_name = '',
    menu_type = 'F',
    visible = '0',
    status = '0',
    perms = 'report:runtimeDetail:export',
    icon = '#',
    remark = @runtime_export_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE @runtime_detail_id IS NOT NULL
  AND perms = 'report:runtimeDetail:export';

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN sys_menu m
  ON (m.menu_id IN (@report_center_id, @runtime_detail_id)
   OR m.perms = 'report:runtimeDetail:export')
WHERE r.role_id IN (1, 2)
  AND r.status = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = r.role_id
      AND rm.menu_id = m.menu_id
  );
