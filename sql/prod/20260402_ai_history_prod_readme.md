# 智能问答历史会话功能上库说明

## 变更目标
为智能问答模块增加历史会话与消息持久化能力，支持：

- 历史会话列表查询
- 打开历史会话继续追问
- 会话软删除
- 用户消息与助手消息落库

## 正式上库 SQL
执行文件：

- `sql/prod/20260402_ai_history_prod.sql`

该脚本只包含表结构，不包含任何测试数据。

## 本次新增表

### fe_ai_conversation
用途：保存会话主信息。

核心字段：

- `conversation_id`：Coze 会话 ID，主键
- `user_id`：所属用户 ID
- `title`：会话标题
- `last_message_preview`：最近一条消息摘要
- `message_count`：消息数量
- `last_chat_id`：最近一次 chat ID
- `status`：会话状态
- `del_flag`：软删除标志

### fe_ai_message
用途：保存会话内消息明细。

核心字段：

- `message_id`：主键
- `conversation_id`：所属会话 ID
- `user_id`：所属用户 ID
- `role`：`user` / `assistant`
- `content`：消息正文
- `answer_type`：回答类型
- `response_type`：响应类型
- `status`：消息状态
- `elapsed_ms`：接口耗时
- `sort_no`：会话内顺序号
- `del_flag`：软删除标志

## 推荐执行顺序
1. 在正式库 `ruoyi` 执行 `sql/prod/20260402_ai_history_prod.sql`
2. 部署后端代码
3. 部署前端代码
4. 登录系统验证智能问答页面

## 验证 SQL
```sql
SHOW TABLES LIKE 'fe_ai_%';

DESC fe_ai_conversation;
DESC fe_ai_message;
```

## 功能验证建议
1. 发送第一条智能问答消息，确认生成新会话
2. 在同一会话内继续追问，确认上下文复用正常
3. 刷新页面后确认左侧历史会话仍可查看
4. 删除会话后确认列表消失且不会再被查出

## 备注
- 本次正式上库不需要导入初始化测试数据
- 所有联调测试数据只留在 `ruoyi_ai_dev`
- 若后续表结构继续演进，请新增新的 `prod` SQL 文件，不覆盖本文件