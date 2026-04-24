# 已废弃：后端多模块设计草案

本文档是早期临时设计草案，已被 `docs/architecture.md` 和 `docs/task-list.md` 中的 `1.3.3.2` 纠偏结果取代。

当前有效后端结构：

```text
attendance
  attendance-common
  attendance-server
    attendance-server-starter
    attendance-server-interfaces
    attendance-server-application
    attendance-server-domain
    attendance-server-infrastructure
```

当前有效登录链路：

```text
AuthController
  -> AuthApplicationService
    -> UserService / UserRepository
      -> UserRepositoryImpl
        -> UserMapper
```

后续不要再按本草案扩展；以项目根目录的 `AGENTS.md`、`docs/task-list.md` 和 `docs/architecture.md` 为准。
