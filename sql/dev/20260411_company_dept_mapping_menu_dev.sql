-- 外部公司映射菜单（开发联调）
-- 说明：
-- 1. 本脚本只补充菜单与按钮权限，不改表结构
-- 2. 默认挂载到“设备管理”目录下；优先复用“传感器管理”所在父目录

SET @parent_menu_id := IFNULL(
    (SELECT parent_id FROM sys_menu WHERE component = 'manage/sensor/index' ORDER BY menu_id LIMIT 1),
    (SELECT menu_id FROM sys_menu WHERE menu_name = '设备管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1)
);

SET @order_num := IFNULL(
    (SELECT MAX(order_num) + 1 FROM sys_menu WHERE parent_id = @parent_menu_id),
    1
);

INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, query, route_name,
    is_frame, is_cache, menu_type, visible, status, perms, icon,
    create_by, create_time, update_by, update_time, remark
)
SELECT
    '外部公司映射', @parent_menu_id, @order_num, 'companyDeptMapping', 'manage/companyDeptMapping/index', NULL, '',
    1, 0, 'C', '0', '0', 'manage:companyDeptMapping:list', 'tree',
    'admin', NOW(), 'admin', NOW(), '外部公司映射菜单'
FROM dual
WHERE @parent_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu WHERE component = 'manage/companyDeptMapping/index'
  );

SET @menu_id := (
    SELECT menu_id FROM sys_menu WHERE component = 'manage/companyDeptMapping/index' ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, query, route_name,
    is_frame, is_cache, menu_type, visible, status, perms, icon,
    create_by, create_time, update_by, update_time, remark
)
SELECT
    menu_name, @menu_id, order_num, '', NULL, NULL, '',
    1, 0, 'F', '0', '0', perms, '#',
    'admin', NOW(), 'admin', NOW(), remark
FROM (
    SELECT '外部公司映射查询' AS menu_name, 1 AS order_num, 'manage:companyDeptMapping:query' AS perms, '' AS remark
    UNION ALL
    SELECT '外部公司映射新增', 2, 'manage:companyDeptMapping:add', ''
    UNION ALL
    SELECT '外部公司映射修改', 3, 'manage:companyDeptMapping:edit', ''
    UNION ALL
    SELECT '外部公司映射删除', 4, 'manage:companyDeptMapping:remove', ''
    UNION ALL
    SELECT '外部公司映射导出', 5, 'manage:companyDeptMapping:export', ''
) t
WHERE @menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu s WHERE s.parent_id = @menu_id AND s.perms = t.perms
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, s.menu_id
FROM sys_menu s
WHERE s.component = 'manage/companyDeptMapping/index'
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = s.menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, s.menu_id
FROM sys_menu s
WHERE s.parent_id = @menu_id
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = s.menu_id
  );
