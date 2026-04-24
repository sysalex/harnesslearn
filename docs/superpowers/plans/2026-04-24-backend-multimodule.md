# 已废弃：后端多模块实施计划

本文档是早期临时实施计划，已被 `docs/task-list.md` 中的 `1.3.3.2 后端分层架构纠偏` 取代。

当前有效实施结果：

- 顶层模块：`attendance-common`、`attendance-server`
- `attendance-server` 内部五层：`starter / interfaces / application / domain / infrastructure`
- 应用层不直接依赖基础设施层
- 领域层通过 `UserRepository` 契约表达用户查询能力
- 基础设施层通过 `UserRepositoryImpl` 适配 `UserMapper`

后续任务不要继续执行本计划；以项目根目录的 `AGENTS.md`、`docs/task-list.md` 和 `docs/architecture.md` 为准。
