-- 修正设备管理下“传感器管理”菜单命名为“传感器信息”
-- 仅调整 sys_menu 展示名称/备注，不改变权限标识、路由、组件或业务表结构。

SET @sensor_menu_name = CONVERT(UNHEX('E4BCA0E6849FE599A8E4BFA1E681AF') USING utf8mb4);
SET @sensor_query_name = CONVERT(UNHEX('E4BCA0E6849FE599A8E4BFA1E681AFE69FA5E8AFA2') USING utf8mb4);
SET @sensor_add_name = CONVERT(UNHEX('E4BCA0E6849FE599A8E4BFA1E681AFE696B0E5A29E') USING utf8mb4);
SET @sensor_edit_name = CONVERT(UNHEX('E4BCA0E6849FE599A8E4BFA1E681AFE4BFAEE694B9') USING utf8mb4);
SET @sensor_remove_name = CONVERT(UNHEX('E4BCA0E6849FE599A8E4BFA1E681AFE588A0E999A4') USING utf8mb4);
SET @sensor_export_name = CONVERT(UNHEX('E4BCA0E6849FE599A8E4BFA1E681AFE5AFBCE587BA') USING utf8mb4);
SET @sensor_menu_remark = CONVERT(UNHEX('E4BCA0E6849FE599A8E4BFA1E681AFE88F9CE58D95') USING utf8mb4);

UPDATE sys_menu
SET menu_name = @sensor_menu_name,
    remark = @sensor_menu_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE perms = 'manage:sensor:list';

UPDATE sys_menu
SET menu_name = @sensor_query_name,
    update_by = 'admin',
    update_time = NOW()
WHERE perms = 'manage:sensor:query';

UPDATE sys_menu
SET menu_name = @sensor_add_name,
    update_by = 'admin',
    update_time = NOW()
WHERE perms = 'manage:sensor:add';

UPDATE sys_menu
SET menu_name = @sensor_edit_name,
    update_by = 'admin',
    update_time = NOW()
WHERE perms = 'manage:sensor:edit';

UPDATE sys_menu
SET menu_name = @sensor_remove_name,
    update_by = 'admin',
    update_time = NOW()
WHERE perms = 'manage:sensor:remove';

UPDATE sys_menu
SET menu_name = @sensor_export_name,
    update_by = 'admin',
    update_time = NOW()
WHERE perms = 'manage:sensor:export';
