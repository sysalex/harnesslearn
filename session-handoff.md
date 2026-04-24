# 会话交接

**最后更新**: 2026-04-24  
**当前阶段**: Phase 1 - 基础设施搭建  
**当前任务**: 1.3.4 实现用户信息管理

## 当前状态

- `1.1.1` 已完成：最小 Spring Boot 后端骨架可启动，命令行运行 Maven 时仍需显式切到 JDK 21。
- `1.1.2` 已完成：Vue 3 前端骨架已落地，具备 Vite + Vue Router + Pinia + Element Plus 基础能力。
- `1.1.3` 阻塞中：本地未安装 Docker，暂不做容器化验证。
- `1.2.1` 已完成：`sql/init.sql` 已通过结构测试，历史上也完成过本地 MySQL 真实执行验证。
- `1.2.2` 已完成：MyBatis-Plus 数据源、`@MapperScan` 和分页拦截器已配置并通过测试。
- `1.3.1` 已完成：`JwtTokenProvider` 已支持 access/refresh token 的生成、校验、刷新和 claims 解析。
- `1.3.2` 已完成：`SecurityConfig` 与 `JwtAuthenticationFilter` 已接入，放行 `/api/auth/login`，其余接口默认要求认证。
- `1.3.3` 已完成：登录接口、统一响应、参数校验和全局异常处理已打通。
- `1.3.3` 已补充修正：`UserServiceImpl` 的单表用户名查询已改回 MyBatis-Plus `lambdaQuery()`，`UserMapper` 不再声明自定义单表方法，`UserMapper.xml` 已删除。
- `1.3.3` 已补充修正：新增公共 `BaseEntity`，统一承接 `createdById`、`createdByName`、`updatedById`、`updatedByName`、`enabled`、`deleted`、`createdAt`、`updatedAt` 字段；`User` 已继承该基类。
- `1.3.3` 已补充修正：`sql/init.sql` 的 `user` 表已对齐公共字段约定，`status` 已替换为 `is_enabled` / `is_deleted`，并补齐创建人和更新人字段。

## 已完成

- 后端通过 `mvn test`，当前共 27 个测试全绿。
- 后端通过 `mvn checkstyle:check`。
- `AuthServiceImplTest` 已切到 `enabled/deleted` 语义。
- `UserServiceArchitectureTest` 已锁定以下约束：
  - `UserService` 继续继承 `IService<User>`
  - `UserServiceImpl` 继续继承 `ServiceImpl<UserMapper, User>`
  - `UserMapper` 不再声明单表自定义方法
  - `User` 必须继承 `BaseEntity`

## 进行中

- 准备进入 `1.3.4 实现用户信息管理`。

## 阻塞项

- 命令行默认仍指向 JDK 8；后端 Maven 验证需显式切到 `%USERPROFILE%\\.jdks\\ms-21.0.10`。
- `1.1.3 配置 Docker 环境` 仍因本地缺少 Docker 而阻塞。
- 本次未复跑 `sql/init.sql` 的本地真实导入：当前环境未在 PATH 中找到 `mysql.exe`，需要补充客户端路径或连接脚本。

## 下一步建议

1. 开始 `1.3.4`，补齐 `GET /api/auth/profile`、`PUT /api/auth/profile`、`PUT /api/auth/password`。
2. 后续单表查询默认优先走 MyBatis-Plus 通用能力，只有多表查询或复杂结果映射再引入 XML。
3. 后续新增实体默认继承 `BaseEntity`，避免再单独散落启用、删除和审计字段。
4. 如需再次做本地 SQL 真执行验证，先补 `mysql.exe` 的固定路径或脚本入口。

## 验证结果

- `backend`: `mvn -Dtest=AuthServiceImplTest,UserServiceArchitectureTest test`
- `backend`: `mvn test`
- `backend`: `mvn checkstyle:check`
