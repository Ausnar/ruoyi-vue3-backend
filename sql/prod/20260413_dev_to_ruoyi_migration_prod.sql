-- 将 ruoyi_ai_dev 中已验证通过的设备联动业务数据迁移到正式库 ruoyi
-- 执行前提：
-- 1. 如正式库中不存在 fe_ai_conversation / fe_ai_message，才需要先执行 sql/prod/20260402_ai_history_prod.sql
-- 2. 已先执行 sql/prod/20260410_device_sdk_sync_prod.sql
-- 3. 已先执行 sql/prod/20260411_company_dept_mapping_menu_prod.sql
-- 4. 源库 ruoyi_ai_dev 与目标库 ruoyi 在同一个 MySQL 实例中
-- 5. 已完成两边数据库备份
-- 6. 当前执行账号必须同时具备：
--    - 对 ruoyi_ai_dev 的 SELECT 权限
--    - 对 ruoyi 的 INSERT / UPDATE 权限
--
-- 如果执行时报类似以下错误：
--   1142 - SELECT command denied ...
-- 说明当前账号不具备跨 schema 读取权限。
-- 此时不要继续强行执行本脚本，建议改用 Navicat“数据传输”完成迁移。

SET @source_schema = 'ruoyi_ai_dev';
SET @target_schema = 'ruoyi';

-- 仅用于人工确认，正式执行时建议先单独跑一遍检查
SELECT @source_schema AS source_schema, @target_schema AS target_schema;

-- 1. 迁移 SDK 配置
INSERT INTO ruoyi.sys_dept_api_config (
    config_id, dept_id, contract_no, api_id, api_key, status, expire_date,
    remark, create_by, create_time, update_by, update_time
)
SELECT
    config_id, dept_id, contract_no, api_id, api_key, status, expire_date,
    remark, create_by, create_time, update_by, update_time
FROM ruoyi_ai_dev.sys_dept_api_config
ON DUPLICATE KEY UPDATE
    dept_id = VALUES(dept_id),
    contract_no = VALUES(contract_no),
    api_id = VALUES(api_id),
    api_key = VALUES(api_key),
    status = VALUES(status),
    expire_date = VALUES(expire_date),
    remark = VALUES(remark),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time);

-- 2. 迁移外部公司快照
INSERT INTO ruoyi.fe_external_company (
    company_record_id, external_company_id, external_company_name, number_prefix, org_path,
    parent_external_company_id, sync_status, first_seen_time, last_seen_time, last_source_dept_id,
    create_by, create_time, update_by, update_time, remark
)
SELECT
    company_record_id, external_company_id, external_company_name, number_prefix, org_path,
    parent_external_company_id, sync_status, first_seen_time, last_seen_time, last_source_dept_id,
    create_by, create_time, update_by, update_time, remark
FROM ruoyi_ai_dev.fe_external_company
ON DUPLICATE KEY UPDATE
    external_company_name = VALUES(external_company_name),
    number_prefix = VALUES(number_prefix),
    org_path = VALUES(org_path),
    parent_external_company_id = VALUES(parent_external_company_id),
    sync_status = VALUES(sync_status),
    first_seen_time = VALUES(first_seen_time),
    last_seen_time = VALUES(last_seen_time),
    last_source_dept_id = VALUES(last_source_dept_id),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time),
    remark = VALUES(remark);

-- 3. 迁移 API 配置可见范围
INSERT INTO ruoyi.fe_api_config_company_scope (
    scope_id, config_id, source_dept_id, external_company_id, external_company_name,
    first_seen_time, last_seen_time, sync_status, remark, create_by, create_time, update_by, update_time
)
SELECT
    scope_id, config_id, source_dept_id, external_company_id, external_company_name,
    first_seen_time, last_seen_time, sync_status, remark, create_by, create_time, update_by, update_time
FROM ruoyi_ai_dev.fe_api_config_company_scope
ON DUPLICATE KEY UPDATE
    source_dept_id = VALUES(source_dept_id),
    external_company_name = VALUES(external_company_name),
    first_seen_time = VALUES(first_seen_time),
    last_seen_time = VALUES(last_seen_time),
    sync_status = VALUES(sync_status),
    remark = VALUES(remark),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time);

-- 4. 迁移外部公司到本地单位映射
INSERT INTO ruoyi.fe_company_dept_mapping (
    mapping_id, external_company_id, external_company_name, dept_id, sync_status,
    remark, create_by, create_time, update_by, update_time
)
SELECT
    mapping_id, external_company_id, external_company_name, dept_id, sync_status,
    remark, create_by, create_time, update_by, update_time
FROM ruoyi_ai_dev.fe_company_dept_mapping
ON DUPLICATE KEY UPDATE
    external_company_name = VALUES(external_company_name),
    dept_id = VALUES(dept_id),
    sync_status = VALUES(sync_status),
    remark = VALUES(remark),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time);

-- 5. 迁移消防点
INSERT INTO ruoyi.fe_fire_point (
    fire_point_id, external_station_id, fire_point_code, fire_point_name, station_number, station_type,
    external_company_id, source_dept_id, dept_id, point_type, location, floor, building,
    longitude, latitude, contact_person, contact_phone, qr_code, sort_order,
    status, sync_status, last_sync_time, create_by, create_time, update_by, update_time, remark, del_flag
)
SELECT
    fire_point_id, external_station_id, fire_point_code, fire_point_name, station_number, station_type,
    external_company_id, source_dept_id, dept_id, point_type, location, floor, building,
    longitude, latitude, contact_person, contact_phone, qr_code, sort_order,
    status, sync_status, last_sync_time, create_by, create_time, update_by, update_time, remark, del_flag
FROM ruoyi_ai_dev.fe_fire_point
ON DUPLICATE KEY UPDATE
    external_station_id = VALUES(external_station_id),
    fire_point_code = VALUES(fire_point_code),
    fire_point_name = VALUES(fire_point_name),
    station_number = VALUES(station_number),
    station_type = VALUES(station_type),
    external_company_id = VALUES(external_company_id),
    source_dept_id = VALUES(source_dept_id),
    dept_id = VALUES(dept_id),
    point_type = VALUES(point_type),
    location = VALUES(location),
    floor = VALUES(floor),
    building = VALUES(building),
    longitude = VALUES(longitude),
    latitude = VALUES(latitude),
    contact_person = VALUES(contact_person),
    contact_phone = VALUES(contact_phone),
    qr_code = VALUES(qr_code),
    sort_order = VALUES(sort_order),
    status = VALUES(status),
    sync_status = VALUES(sync_status),
    last_sync_time = VALUES(last_sync_time),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time),
    remark = VALUES(remark),
    del_flag = VALUES(del_flag);

-- 6. 迁移网关
INSERT INTO ruoyi.fe_gateway (
    gateway_id, external_tbox_id, imei, sim, fire_point_id, dept_id, source_dept_id,
    external_company_id, gps_longitude, gps_latitude, last_online_time,
    sync_status, last_sync_time, status, create_by, create_time, update_by, update_time, remark, del_flag
)
SELECT
    gateway_id, external_tbox_id, imei, sim, fire_point_id, dept_id, source_dept_id,
    external_company_id, gps_longitude, gps_latitude, last_online_time,
    sync_status, last_sync_time, status, create_by, create_time, update_by, update_time, remark, del_flag
FROM ruoyi_ai_dev.fe_gateway
ON DUPLICATE KEY UPDATE
    external_tbox_id = VALUES(external_tbox_id),
    imei = VALUES(imei),
    sim = VALUES(sim),
    fire_point_id = VALUES(fire_point_id),
    dept_id = VALUES(dept_id),
    source_dept_id = VALUES(source_dept_id),
    external_company_id = VALUES(external_company_id),
    gps_longitude = VALUES(gps_longitude),
    gps_latitude = VALUES(gps_latitude),
    last_online_time = VALUES(last_online_time),
    sync_status = VALUES(sync_status),
    last_sync_time = VALUES(last_sync_time),
    status = VALUES(status),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time),
    remark = VALUES(remark),
    del_flag = VALUES(del_flag);

-- 7. 迁移传感器
INSERT INTO ruoyi.fe_sensor (
    sensor_id, external_sensor_id, sensor_code, dept_id, source_dept_id, gateway_id, gateway_code,
    mac, pressure, temperature, battery_level, signal_strength, last_online_time,
    status, sync_status, last_sync_time, create_by, create_time, update_by, update_time, remark, del_flag
)
SELECT
    sensor_id, external_sensor_id, sensor_code, dept_id, source_dept_id, gateway_id, gateway_code,
    mac, pressure, temperature, battery_level, signal_strength, last_online_time,
    status, sync_status, last_sync_time, create_by, create_time, update_by, update_time, remark, del_flag
FROM ruoyi_ai_dev.fe_sensor
ON DUPLICATE KEY UPDATE
    external_sensor_id = VALUES(external_sensor_id),
    sensor_code = VALUES(sensor_code),
    dept_id = VALUES(dept_id),
    source_dept_id = VALUES(source_dept_id),
    gateway_id = VALUES(gateway_id),
    gateway_code = VALUES(gateway_code),
    mac = VALUES(mac),
    pressure = VALUES(pressure),
    temperature = VALUES(temperature),
    battery_level = VALUES(battery_level),
    signal_strength = VALUES(signal_strength),
    last_online_time = VALUES(last_online_time),
    status = VALUES(status),
    sync_status = VALUES(sync_status),
    last_sync_time = VALUES(last_sync_time),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time),
    remark = VALUES(remark),
    del_flag = VALUES(del_flag);

-- 8. 迁移灭火器
INSERT INTO ruoyi.fe_extinguisher (
    extinguisher_id, external_extinguisher_id, external_company_id, label_code, specification,
    sensor_id, sensor_code, dept_id, source_dept_id, product_name, manufacturer, service_provider,
    production_date, inspection_date, expiry_date, scrap_date,
    fire_point_id, install_location, qr_code, status, sync_status, last_sync_time,
    create_by, create_time, update_by, update_time, remark, del_flag
)
SELECT
    extinguisher_id, external_extinguisher_id, external_company_id, label_code, specification,
    sensor_id, sensor_code, dept_id, source_dept_id, product_name, manufacturer, service_provider,
    production_date, inspection_date, expiry_date, scrap_date,
    fire_point_id, install_location, qr_code, status, sync_status, last_sync_time,
    create_by, create_time, update_by, update_time, remark, del_flag
FROM ruoyi_ai_dev.fe_extinguisher
ON DUPLICATE KEY UPDATE
    external_extinguisher_id = VALUES(external_extinguisher_id),
    external_company_id = VALUES(external_company_id),
    label_code = VALUES(label_code),
    specification = VALUES(specification),
    sensor_id = VALUES(sensor_id),
    sensor_code = VALUES(sensor_code),
    dept_id = VALUES(dept_id),
    source_dept_id = VALUES(source_dept_id),
    product_name = VALUES(product_name),
    manufacturer = VALUES(manufacturer),
    service_provider = VALUES(service_provider),
    production_date = VALUES(production_date),
    inspection_date = VALUES(inspection_date),
    expiry_date = VALUES(expiry_date),
    scrap_date = VALUES(scrap_date),
    fire_point_id = VALUES(fire_point_id),
    install_location = VALUES(install_location),
    qr_code = VALUES(qr_code),
    status = VALUES(status),
    sync_status = VALUES(sync_status),
    last_sync_time = VALUES(last_sync_time),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time),
    remark = VALUES(remark),
    del_flag = VALUES(del_flag);

-- 9. 迁移绑定历史
INSERT INTO ruoyi.fe_extinguisher_sensor_binding (
    binding_id, extinguisher_id, sensor_id, external_extinguisher_id, external_sensor_id,
    bind_time, unbind_time, is_active, source_type,
    create_by, create_time, update_by, update_time, remark
)
SELECT
    binding_id, extinguisher_id, sensor_id, external_extinguisher_id, external_sensor_id,
    bind_time, unbind_time, is_active, source_type,
    create_by, create_time, update_by, update_time, remark
FROM ruoyi_ai_dev.fe_extinguisher_sensor_binding
ON DUPLICATE KEY UPDATE
    extinguisher_id = VALUES(extinguisher_id),
    sensor_id = VALUES(sensor_id),
    external_extinguisher_id = VALUES(external_extinguisher_id),
    external_sensor_id = VALUES(external_sensor_id),
    bind_time = VALUES(bind_time),
    unbind_time = VALUES(unbind_time),
    is_active = VALUES(is_active),
    source_type = VALUES(source_type),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time),
    remark = VALUES(remark);

-- 10. 可选：迁移传感器历史
-- 数据量可能较大，如需迁移请取消注释执行
/*
INSERT INTO ruoyi.fe_sensor_history (
    history_id, sensor_id, sensor_code, pressure, temperature, battery_level,
    signal_strength, status, create_time
)
SELECT
    history_id, sensor_id, sensor_code, pressure, temperature, battery_level,
    signal_strength, status, create_time
FROM ruoyi_ai_dev.fe_sensor_history
ON DUPLICATE KEY UPDATE
    sensor_id = VALUES(sensor_id),
    sensor_code = VALUES(sensor_code),
    pressure = VALUES(pressure),
    temperature = VALUES(temperature),
    battery_level = VALUES(battery_level),
    signal_strength = VALUES(signal_strength),
    status = VALUES(status),
    create_time = VALUES(create_time);
*/

-- 11. 可选：迁移设备同步定时任务
-- 推荐优先手工在正式库重建这类任务。
-- 如果要迁移，请先确认正式库中不存在需要保留的 deviceSyncTask 任务。
/*
DELETE FROM ruoyi.sys_job
WHERE invoke_target LIKE 'deviceSyncTask.%';

INSERT INTO ruoyi.sys_job (
    job_id, job_name, job_group, invoke_target, cron_expression,
    misfire_policy, concurrent, status, remark, create_by, create_time
)
SELECT
    job_id, job_name, job_group, invoke_target, cron_expression,
    misfire_policy, concurrent, status, remark, create_by, create_time
FROM ruoyi_ai_dev.sys_job
WHERE invoke_target LIKE 'deviceSyncTask.%';
*/

-- 12. 不默认迁移：
-- fe_sdk_sync_log
-- fe_ai_conversation
-- fe_ai_message
-- sys_job_log
