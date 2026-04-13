# `ruoyi_ai_dev` 切回 `ruoyi` 执行清单

> 文档状态：历史记录 / 已归档
>
> 使用说明：
> - 本文记录的是 `2026-04-13` 这次从 `ruoyi_ai_dev` 切回正式库 `ruoyi` 的执行过程与验收清单。
> - 当前默认迁库入口已切换为 [db-switch-sop.md](/D:/ai工具/qclaw_workspace/ruoyi_frontend/ruoyi/doc/db-switch-sop.md)。
> - 本文保留的是当次实操路径、补充结论与验收记录。

## 目标

将当前已经在 `ruoyi_ai_dev` 验证通过的 SDK 联动能力切回正式业务库 `ruoyi`，并尽量保留以下能力：

- SDK 配置与自动同步能力
- 外部公司观测与映射
- 设备主数据与本地单位归属
- 本地单位树权限
- 首页地图消防点展示

## 当前事实

- 当前后端数据源仍指向 `ruoyi_ai_dev`
- 正式结构脚本已沉淀在 `sql/prod/`
- 当前联调数据已经不只是测试空壳，至少包含：
  - `sys_dept_api_config`
  - `fe_fire_point`
  - `fe_gateway`
  - `fe_sensor`
  - `fe_extinguisher`
  - `fe_external_company`
  - `fe_api_config_company_scope`
  - `fe_company_dept_mapping`

## 当前状态（2026-04-13 晚）

本清单最初用于指导从 `ruoyi_ai_dev` 切回 `ruoyi` 的执行顺序。以下为实际执行结果补充：

1. 两边数据库备份已完成：
   - `ruoyi_ai_dev.sql`
   - `ruoyi.sql`
2. `ruoyi` 中已确认存在：
   - `fe_ai_conversation`
   - `fe_ai_message`
   因此 `sql/prod/20260402_ai_history_prod.sql` 本次未执行。
3. 已在 `ruoyi` 执行正式结构脚本：
   - `sql/prod/20260410_device_sdk_sync_prod.sql`
   - `sql/prod/20260411_company_dept_mapping_menu_prod.sql`
4. 原计划中的 `sql/prod/20260413_dev_to_ruoyi_migration_prod.sql` 因数据库账号缺少跨 schema `SELECT` 权限未能直接执行。
5. 实际业务数据迁移采用的是 Navicat `数据传输`，而不是跨库 SQL。
6. `fe_extinguisher_sensor_binding` 初次迁移遗漏，后续已单表补传完成。
7. 当前正式库运行数据源已经切到 `ruoyi`。
8. 正式库中的 Quartz 任务未迁移 `sys_job`，因此已在正式环境中手工重建：
   - `deviceSyncTask.syncAllActiveConfigs()`
   - `cron = 0 0/10 * * * ?`
   - `禁止并发`

## 推荐执行顺序

1. 备份 `ruoyi_ai_dev`
2. 备份 `ruoyi`
3. 在 `ruoyi` 执行正式结构脚本：
   - `sql/prod/20260410_device_sdk_sync_prod.sql`
   - `sql/prod/20260411_company_dept_mapping_menu_prod.sql`
4. 如果数据库账号支持跨 schema 读写，可执行 `sql/prod/20260413_dev_to_ruoyi_migration_prod.sql`
5. 如果数据库账号不支持跨 schema 读写，改用 Navicat `数据传输`
6. 校验正式库关键表数据量
7. 校验正式库手工同步、映射回填、权限、地图
8. 最后再修改 `ruoyi-admin/src/main/resources/application-druid.yml` 指向 `ruoyi`
9. 重启后端并做一轮完整验收

## 建议迁移的数据

建议迁移：

- `sys_dept_api_config`
- `fe_external_company`
- `fe_api_config_company_scope`
- `fe_company_dept_mapping`
- `fe_fire_point`
- `fe_gateway`
- `fe_sensor`
- `fe_extinguisher`
- `fe_extinguisher_sensor_binding`

按需要迁移：

- `fe_sensor_history`
  - 如果正式库需要保留当前历史趋势数据，则迁移
  - 如果希望正式库从切换时刻开始重新积累，也可以不迁

不建议默认迁移：

- `fe_sdk_sync_log`
  - 开发联调日志价值主要在排障，切正式库后通常不必带入
- `fe_ai_conversation`
- `fe_ai_message`
  - 除非明确确认这些会话已属于正式业务数据
- `sys_job_log`
  - 只属于执行日志，不建议带入

## 定时任务注意点

Quartz 任务配置存在 `sys_job` 表里。

如果你们已经在 `ruoyi_ai_dev` 中创建了：

- `deviceSyncTask.syncAllActiveConfigs()`
- 或多个 `deviceSyncTask.syncByConfigId(...)`

那么切到 `ruoyi` 后，这些任务不会自动跟过去。

推荐两种方式二选一：

1. 在正式库手工重建任务
2. 从 `ruoyi_ai_dev.sys_job` 中只迁移 `invoke_target like 'deviceSyncTask.%'` 的任务

推荐优先手工重建，最干净。

本次实际执行即采用：

- 不迁移 `sys_job`
- 在正式库页面手工新建 `deviceSyncTask.syncAllActiveConfigs()`

## 执行后校验 SQL

### 1. 核心业务表数量

```sql
select count(*) as cnt from sys_dept_api_config;
select count(*) as cnt from fe_external_company;
select count(*) as cnt from fe_api_config_company_scope;
select count(*) as cnt from fe_company_dept_mapping;
select count(*) as cnt from fe_fire_point;
select count(*) as cnt from fe_gateway;
select count(*) as cnt from fe_sensor;
select count(*) as cnt from fe_extinguisher;
select count(*) as cnt from fe_extinguisher_sensor_binding;
```

### 2. 设备归属分布

```sql
select dept_id, count(*) cnt from fe_fire_point group by dept_id order by dept_id;
select dept_id, count(*) cnt from fe_gateway group by dept_id order by dept_id;
select dept_id, count(*) cnt from fe_sensor group by dept_id order by dept_id;
select dept_id, count(*) cnt from fe_extinguisher group by dept_id order by dept_id;
```

### 3. 映射分布

```sql
select dept_id, count(*) cnt
from fe_company_dept_mapping
group by dept_id
order by dept_id;
```

### 4. SDK 配置状态

```sql
select config_id, dept_id, contract_no, status, expire_date
from sys_dept_api_config
order by dept_id, config_id;
```

## 应用切换后验收项

1. 手工执行一次设备同步
2. 检查 `fe_sdk_sync_log` 是否生成新记录
3. 检查外部公司映射页面是否正常打开
4. 检查设备表 `dept_id` 是否仍正确
5. 检查普通用户是否仍按本地单位树可见
6. 检查首页地图是否能正确展示多个单位及其消防点
7. 检查定时任务是否已在正式库重建并能执行

## 风险提醒

- 不要先改数据源再补库结构，否则应用启动就会报表不存在或字段不存在
- 不要把开发联调日志、测试会话默认搬进正式库
- 如果正式库里已经存在同名业务数据，执行迁移脚本前先和 DBA 确认是否覆盖
- 如果数据库账号没有跨 schema `SELECT` 权限，不要强行执行跨库迁移 SQL，直接改用 Navicat `数据传输`
