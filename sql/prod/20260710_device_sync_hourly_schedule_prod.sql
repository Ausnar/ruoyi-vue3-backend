-- 设备完整同步任务调整为每小时执行（prod）
-- 口径：每小时整点执行；错过的调度不补跑；禁止并发。

UPDATE sys_job
SET cron_expression = '0 0 0/1 * * ?',
    misfire_policy = '3',
    concurrent = '1',
    update_by = 'sql:device-sync-hourly',
    update_time = NOW()
WHERE invoke_target = 'deviceSyncTask.syncAllActiveConfigs()';

SELECT job_id,
       job_name,
       invoke_target,
       cron_expression,
       misfire_policy,
       concurrent,
       status,
       update_time
FROM sys_job
WHERE invoke_target = 'deviceSyncTask.syncAllActiveConfigs()';

