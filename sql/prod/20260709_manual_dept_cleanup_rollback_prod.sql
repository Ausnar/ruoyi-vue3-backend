-- 手工单位清理回滚脚本（prod）
-- 回滚批次：20260709-manual-dept-cleanup

SET @cleanup_batch := '20260709-manual-dept-cleanup';

UPDATE sys_dept d
INNER JOIN sys_dept_manual_cleanup_backup_20260709 b
        ON b.dept_id = d.dept_id
       AND b.cleanup_batch = @cleanup_batch
SET d.parent_id = b.parent_id,
    d.ancestors = b.ancestors,
    d.dept_name = b.dept_name,
    d.order_num = b.order_num,
    d.leader = b.leader,
    d.phone = b.phone,
    d.email = b.email,
    d.status = b.status,
    d.del_flag = b.del_flag,
    d.create_by = b.create_by,
    d.create_time = b.create_time,
    d.update_by = 'sql:manual-dept-cleanup-rollback',
    d.update_time = NOW(),
    d.province = b.province,
    d.city = b.city,
    d.area = b.area,
    d.longitude = b.longitude,
    d.latitude = b.latitude,
    d.dept_source = b.dept_source,
    d.external_company_id = b.external_company_id,
    d.external_parent_company_id = b.external_parent_company_id,
    d.external_org_path = b.external_org_path,
    d.source_api_config_id = b.source_api_config_id,
    d.last_company_sync_time = b.last_company_sync_time;

UPDATE fe_company_dept_mapping m
INNER JOIN fe_company_dept_mapping_manual_cleanup_backup_20260709 b
        ON b.mapping_id = m.mapping_id
       AND b.cleanup_batch = @cleanup_batch
SET m.external_company_id = b.external_company_id,
    m.external_company_name = b.external_company_name,
    m.dept_id = b.dept_id,
    m.sync_status = b.sync_status,
    m.remark = b.remark,
    m.create_by = b.create_by,
    m.create_time = b.create_time,
    m.update_by = 'sql:manual-dept-cleanup-rollback',
    m.update_time = NOW();

SELECT 'rollback_manual_dept_cleanup' AS check_item,
       COUNT(1) AS restored_dept_count
FROM sys_dept_manual_cleanup_backup_20260709
WHERE cleanup_batch = @cleanup_batch;

SELECT 'rollback_manual_mapping_cleanup' AS check_item,
       COUNT(1) AS restored_mapping_count
FROM fe_company_dept_mapping_manual_cleanup_backup_20260709
WHERE cleanup_batch = @cleanup_batch;
