-- 单位中心新增外部单位差异核对只读页菜单
-- 可重复执行

SET @unit_center_menu_id := (
  SELECT menu_id FROM sys_menu WHERE path = 'unitCenter' AND menu_type = 'M' ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, `query`, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '外部单位差异核对', @unit_center_menu_id, 5, 'companyDeptMappingDiff', 'manage/companyDeptMappingDiff/index', NULL, '',
  1, 0, 'C', '0', '0', 'manage:companyDeptMapping:list', 'list-checks',
  'admin', NOW(), 'admin', NOW(), '单位中心-外部单位差异核对'
WHERE @unit_center_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE component = 'manage/companyDeptMappingDiff/index'
  );

UPDATE sys_menu
SET parent_id = @unit_center_menu_id,
    order_num = 5,
    menu_name = '外部单位差异核对',
    path = 'companyDeptMappingDiff',
    perms = 'manage:companyDeptMapping:list',
    icon = 'list-checks',
    visible = '0',
    status = '0',
    remark = '单位中心-外部单位差异核对',
    update_by = 'admin',
    update_time = NOW()
WHERE @unit_center_menu_id IS NOT NULL
  AND component = 'manage/companyDeptMappingDiff/index';

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE m.component = 'manage/companyDeptMappingDiff/index'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id
  );
