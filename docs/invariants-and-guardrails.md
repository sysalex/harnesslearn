# Invariants And Guardrails

本文件记录项目中的硬约束。与普通建议不同，这些规则默认不能破坏。

## 架构不变量

### 后端

必须遵守：

```text
Controller -> Service -> Mapper -> Database
```

禁止：
- Controller 直接访问 Mapper
- Service 直接绕过分层去拼装响应
- 在 Mapper 中写业务逻辑

### 前端

必须遵守：

```text
View -> Store -> API Layer -> Backend
```

禁止：
- View 直接发请求
- 业务状态绕过 Pinia
- API 调用散落在页面里

## 质量护栏

禁止：
- 跳过测试后再补
- 跳过 Code Review
- 跳过 DoD
- 通过删测试来让构建通过

## 安全护栏

禁止：
- 硬编码密码、密钥、Token
- 拼接 SQL
- 绕过权限校验
- 在日志中打印敏感信息

## 日志与异常

必须：
- 关键操作有日志
- 异常有统一处理
- 日志尽可能带 `request_id`

禁止：
- `System.out.println`
- `console.log`
- 空 `catch`

## 变更护栏

以下事项默认需要确认：
- 删除文件
- 改 schema
- 改 API 契约
- 改认证授权
- 改生产相关配置

## 使用方式

- 如果某项改动与本文件冲突，先停下，不要继续写。
- 如果确实需要突破护栏，必须在任务记录中注明原因和回滚方式。
