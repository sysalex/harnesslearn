# 会话交接

**最后更新**: 2026-04-24  
**当前阶段**: Phase 1 - 基础设施搭建  
**当前任务**: 1.3.4 实现用户信息管理

## 当前状态

- `1.1.1` 已完成：最小 Spring Boot 后端骨架可启动，命令行运行 Maven 时仍需显式切到 JDK 21。
- `1.1.2` 已完成：Vue 3 前端骨架已落地，具备 Vite + Vue Router + Pinia + Element Plus 基础能力。
- `1.1.3` 阻塞中：本地未安装 Docker，暂不做容器化验证。
- `1.2.1` 已完成：`sql/init.sql` 已在本地 MySQL 真实执行验证通过，核心表、管理员种子数据和全局考勤规则均已初始化。
- `1.2.2` 已完成：MyBatis-Plus 数据源、`@MapperScan` 和分页拦截器已配置并通过测试。
- `1.3.1` 已完成：`JwtTokenProvider` 已支持 access/refresh token 的生成、校验、刷新和 claims 解析。
- `1.3.2` 已完成：`SecurityConfig` 与 `JwtAuthenticationFilter` 已接入，放行 `/api/auth/login`，其余接口默认要求认证。
- `1.3.3` 已完成：登录接口、统一响应、参数校验和全局异常处理已打通。
- `1.3.3` 已补充修正：登录链路已按项目约定切回 MyBatis-Plus 风格，新增 `UserService extends IService<User>` 与 `UserServiceImpl extends ServiceImpl<UserMapper, User>`，`UserMapper` 自定义查询迁移到 XML，`AuthServiceImpl` 改为依赖 `UserService`。

## 已完成

- 后端登录链路当前通过 `mvn test` 与 `mvn checkstyle:check`。
- `AuthServiceImplTest` 已改为基于 `UserService` 进行隔离测试。
- 新增 `UserServiceArchitectureTest`，锁定 `IService` / `ServiceImpl` 继承结构与 `findByUsername` 契约。

## 进行中

- 准备进入 `1.3.4 实现用户信息管理`。

## 阻塞项

- 命令行默认仍指向 JDK 8；后端 Maven 验证需显式切到 `%USERPROFILE%\\.jdks\\ms-21.0.10`。
- `1.1.3 配置 Docker 环境` 仍因本地缺少 Docker 而阻塞。

## 下一步建议

1. 开始 `1.3.4`，补齐 `GET /api/auth/profile`、`PUT /api/auth/profile`、`PUT /api/auth/password`。
2. 复用当前 `UserService` 与 `AuthService` 链路，避免再次出现 Mapper 直连业务层的问题。
3. 如果后续新增自定义查询，优先走 XML Mapper，而不是注解 SQL。

## 验证结果

- `backend`: `mvn -Dtest=AuthServiceImplTest,UserServiceArchitectureTest test`
- `backend`: `mvn test`
- `backend`: `mvn checkstyle:check`
- `mysql`: 使用 `cmd` 文件重定向执行 `sql/init.sql`，已验证本地 MySQL 初始化通过
- `frontend`: `npm run test`
- `frontend`: `npm run lint`
- `frontend`: `npm run build`
