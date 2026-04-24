# 会话交接

**最后更新**: 2026-04-24  
**当前阶段**: Phase 1 - 基础设施搭建  
**当前任务**: 1.3.4 实现用户信息管理

## 当前状态

- `1.1.1` 已完成：最小 Spring Boot 后端骨架可启动，命令行运行 Maven 时仍需显式切到 JDK 21。
- `1.1.2` 已完成：Vue 3 前端骨架已落地，具备 Vite + Vue Router + Pinia + Element Plus 基础能力。
- `1.1.3` 阻塞中：本地未安装 Docker，暂不做容器化验证。
- `1.2.1` 已完成：`sql/init.sql` 已完成结构测试，历史上也做过真实 MySQL 导入验证。
- `1.2.2` 已完成：MyBatis-Plus 数据源、`@MapperScan` 和分页拦截器已配置并通过测试。
- `1.3.1` 已完成：`JwtTokenProvider` 已支持 access/refresh token 的生成、校验、刷新和 claims 解析。
- `1.3.2` 已完成：`SecurityConfig` 与 `JwtAuthenticationFilter` 已接入，放行 `/api/auth/login`，其余接口默认要求认证。
- `1.3.3` 已完成：登录接口、统一响应、参数校验和全局异常处理已打通。
- `1.3.3` 已补充修正：单表用户名查询已改回 MyBatis-Plus `lambdaQuery()`，`UserMapper` 不再声明自定义单表方法。
- `1.3.3` 已补充修正：公共基类字段已统一为 `id`、`createdByUserId`、`createdByUserName`、`createdTime`、`updatedByUserId`、`updatedByUserName`、`updatedTime`、`enabledFlag`、`deletedFlag`。
- `1.3.3` 已补充修正：`sql/init.sql` 和本地 `attendance_db` 均已对齐上述字段命名；`sql/migrations/20260424_align_common_base_columns.sql` 已执行完成。

## 已完成

- 后端通过 `mvn test`，当前共 27 个测试全绿。
- 后端通过 `mvn checkstyle:check`。
- `AuthServiceImplTest` 已切换到 `enabledFlag` / `deletedFlag` 语义。
- `UserServiceArchitectureTest` 已锁定以下约束：
  - `UserService` 继续继承 `IService<User>`
  - `UserServiceImpl` 继续继承 `ServiceImpl<UserMapper, User>`
  - `UserMapper` 不再声明单表自定义方法
  - `User` 必须继承 `BaseEntity`
  - `BaseEntity` 必须使用最新约定字段名
- 本地数据库已通过 JDBC 方式实际执行 migration，并查询确认以下 6 张表都已具备公共字段：
  - `department`
  - `user`
  - `attendance_rule`
  - `attendance_record`
  - `leave_application`
  - `makeup_application`

## 进行中

- 准备进入 `1.3.4 实现用户信息管理`。

## 阻塞项

- 命令行默认仍指向 JDK 8；后端 Maven 验证需显式切到 `%USERPROFILE%\\.jdks\\ms-21.0.10`。
- `1.1.3 配置 Docker 环境` 仍因本地缺少 Docker 而阻塞。

## 下一步建议

1. 开始 `1.3.4`，补齐 `GET /api/auth/profile`、`PUT /api/auth/profile`、`PUT /api/auth/password`。
2. 后续新增实体默认继承 `BaseEntity`，不要再各自散落审计和逻辑删除字段。
3. 后续单表查询默认优先走 MyBatis-Plus 通用能力，只有多表查询或复杂结果映射再引入 XML。
4. 如需在别的环境同步表结构，直接复用 `sql/migrations/20260424_align_common_base_columns.sql`。

## 验证结果

- `backend`: `mvn -Dtest=AuthServiceImplTest,UserServiceArchitectureTest,InitSqlScriptTest test`
- `backend`: `mvn test`
- `backend`: `mvn checkstyle:check`
- `backend`: JDBC 执行 `sql/migrations/20260424_align_common_base_columns.sql`
- `backend`: JDBC 查询 `information_schema.COLUMNS`，确认 6 张表均具备公共字段
