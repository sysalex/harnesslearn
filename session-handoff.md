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
- `1.3.3` 已补充修正：`BaseEntity` 和 `User` 都已切到 Lombok；`entity` 包下不再手写机械 getter/setter。
- `1.3.3` 已补充修正：Java 字段保留 camelCase，数据库列统一改为 snake_case；`sql/init.sql` 与 `sql/migrations/20260424_align_common_base_columns.sql` 已对齐这一规范。
- `1.3.3` 已补充修正：新增 `sql/migrations/20260424_fix_common_column_comments.sql`，用于补齐本地已更新表上的公共列注释；本地 `attendance_db` 已实际执行并校验通过。

## 已完成

- 后端通过 `mvn test`，当前共 29 个测试全绿。
- 后端通过 `mvn checkstyle:check`。
- `UserServiceArchitectureTest` 已锁定以下约束：
  - `UserService` 继续继承 `IService<User>`
  - `UserServiceImpl` 继续继承 `ServiceImpl<UserMapper, User>`
  - `UserMapper` 不再声明单表自定义方法
  - `User` 必须继承 `BaseEntity`
  - `BaseEntity` 与 `User` 都必须使用 Lombok
  - `init.sql` 必须使用 snake_case 公共列名
- 本地数据库已通过 JDBC 方式完成两类真实校验：
  - 6 张核心表都已具备 snake_case 公共字段
  - 6 张核心表的公共列都已有非空注释

## 进行中

- 准备进入 `1.3.4 实现用户信息管理`。

## 阻塞项

- 命令行默认仍指向 JDK 8；后端 Maven 验证需显式切到 `%USERPROFILE%\\.jdks\\ms-21.0.10`。
- `1.1.3 配置 Docker 环境` 仍因本地缺少 Docker 而阻塞。

## 下一步建议

1. 开始 `1.3.4`，补齐 `GET /api/auth/profile`、`PUT /api/auth/profile`、`PUT /api/auth/password`。
2. 后续 `entity` 包下的简单实体默认统一用 Lombok，避免再回退到手写访问器。
3. 后续结构迁移默认同时校验三件事：字段存在、命名正确、注释存在。
4. 如需在别的环境同步表结构，优先执行：
   - `sql/migrations/20260424_align_common_base_columns.sql`
   - `sql/migrations/20260424_fix_common_column_comments.sql`

## 验证结果

- `backend`: `mvn -Dtest=UserServiceArchitectureTest test`
- `backend`: `mvn test`
- `backend`: `mvn checkstyle:check`
- `backend`: JDBC 执行 `sql/migrations/20260424_fix_common_column_comments.sql`
- `backend`: JDBC 查询 `information_schema.COLUMNS`，确认 6 张表公共列注释均存在
