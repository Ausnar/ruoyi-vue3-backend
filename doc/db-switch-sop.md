# dev 到 ruoyi 迁库 SOP

> 文档状态：当前有效 / SOP
>
> 使用说明：
> - 本文用于指导后续从开发联调库 `ruoyi_ai_dev` 切换或迁移到正式库 `ruoyi`。
> - 当前推荐路径以本次实操结论为准：优先使用 Navicat `数据传输`，不要默认跨库 SQL 可执行。
> - 如需回看 `2026-04-13` 那次实际执行记录，请再读归档清单。

## 1. 适用场景

适用于以下场景：

- 已在 `ruoyi_ai_dev` 验证通过的新功能需要迁入 `ruoyi`
- 正式环境重建后，需要重新补齐业务表和数据
- 需要复刻一次已经验证过的 dev -> prod 迁移流程

## 2. 默认原则

1. 结构变更先落 `ruoyi/sql/dev` 和 `ruoyi/sql/prod`
2. 业务能力先在 `ruoyi_ai_dev` 验证通过
3. 正式库结构先补齐，再迁业务数据
4. 迁数据优先使用 Navicat `数据传输`
5. 切换后必须重新做同步、权限、地图和定时任务验收

## 3. 推荐执行顺序

1. 备份 `ruoyi_ai_dev`
2. 备份 `ruoyi`
3. 在 `ruoyi` 执行正式结构脚本
4. 用 Navicat `数据传输` 迁入业务数据
5. 校验正式库关键表数量和归属分布
6. 检查或重建 Quartz 任务
7. 最后再修改 `application-druid.yml` 指向 `ruoyi`
8. 重启后端并做完整验收

## 4. 结构脚本执行

迁库前，先在 `ruoyi` 执行本次需求对应的 `sql/prod/` 脚本。

例如本轮设备侧相关脚本包括：

- `sql/prod/20260410_device_sdk_sync_prod.sql`
- `sql/prod/20260411_company_dept_mapping_menu_prod.sql`

原则：

- 先补结构
- 再迁数据
- 不要先改数据源再补表结构

## 5. 数据迁移方式

当前推荐方式：

- Navicat `数据传输`

不推荐默认方式：

- 直接执行跨库迁移 SQL

原因：

- 当前数据库账号可能没有跨 schema `SELECT` 权限
- 即使脚本存在，也不能假设一定可执行

如果后续数据库账号明确具备跨 schema 权限，再单独评估是否启用跨库 SQL。

## 6. 建议迁移的数据

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
  - 如果正式库需要延续当前历史趋势数据，则迁移
  - 如果希望从切换时刻开始重新积累，也可以不迁

不建议默认迁移：

- `fe_sdk_sync_log`
- `fe_ai_conversation`
- `fe_ai_message`
- `sys_job_log`

## 7. 定时任务注意点

Quartz 任务配置在 `sys_job` 中。

默认不要指望 dev 库里的任务自动跟到正式库。推荐做法是：

1. 不迁移 `sys_job`
2. 在正式库页面手工重建需要的任务

当前设备同步推荐确认：

- 调用目标：`deviceSyncTask.syncAllActiveConfigs()`
- `cron`：`0 0/10 * * * ?`
- 并发：`禁止`

## 8. 执行后校验

### 8.1 核心业务表数量

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

### 8.2 设备归属分布

```sql
select dept_id, count(*) cnt from fe_fire_point group by dept_id order by dept_id;
select dept_id, count(*) cnt from fe_gateway group by dept_id order by dept_id;
select dept_id, count(*) cnt from fe_sensor group by dept_id order by dept_id;
select dept_id, count(*) cnt from fe_extinguisher group by dept_id order by dept_id;
```

### 8.3 映射分布

```sql
select dept_id, count(*) cnt
from fe_company_dept_mapping
group by dept_id
order by dept_id;
```

### 8.4 SDK 配置状态

```sql
select config_id, dept_id, contract_no, status, expire_date
from sys_dept_api_config
order by dept_id, config_id;
```

### 8.5 功能验收项

1. 手工执行一次设备同步
2. 检查 `fe_sdk_sync_log` 是否生成新记录
3. 检查外部公司映射页面是否正常打开
4. 检查设备表 `dept_id` 是否仍正确
5. 检查普通用户是否仍按本地单位树可见
6. 检查首页地图是否能正确展示多个单位及其消防点
7. 检查正式库定时任务是否已重建并能执行

## 9. 风险提醒

- 不要先改数据源再补库结构
- 不要把开发联调日志、测试会话默认搬进正式库
- 如果正式库已存在同名业务数据，迁入前先确认是否覆盖
- 如果数据库账号没有跨 schema `SELECT` 权限，直接改用 Navicat `数据传输`

## 10. 关联文档

1. [db-change-workflow.md](/D:/ai工具/qclaw_workspace/ruoyi_frontend/ruoyi/doc/db-change-workflow.md)
2. [ruoyi-db-switch-checklist-20260413.md](/D:/ai工具/qclaw_workspace/ruoyi_frontend/ruoyi/doc/archive/20260413/ruoyi-db-switch-checklist-20260413.md)
3. [当前项目状态与开发交接-20260412.md](/D:/ai工具/qclaw_workspace/ruoyi_frontend/docs/当前项目状态与开发交接-20260412.md)
