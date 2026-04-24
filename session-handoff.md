# 会话交接

**最后更新**: 2026-04-24  
**当前阶段**: Phase 1 - 基础设施搭建  
**当前任务**: 1.3.4 实现用户信息管理

## 当前状态

- `1.1.1` 已完成：后端已从单模块改为 `attendance-server-*` 六模块 Maven Reactor。
- `1.1.2` 已完成：前端骨架已落地，状态未受本次后端重构影响。
- `1.1.3` 阻塞中：本地仍未安装 Docker，容器化验证暂未继续。
- `1.2.1` 已完成：`sql/init.sql` 结构测试仍通过。
- `1.2.2` 已完成：MyBatis-Plus 配置迁移到 `attendance-server-infrastructure`，分页拦截器与 Mapper 扫描验证通过。
- `1.3.1` 已完成：`JwtTokenProvider` 迁移到基础设施模块，原有 token 行为保持不变。
- `1.3.2` 已完成：`SecurityConfig` 与 `JwtAuthenticationFilter` 迁移到基础设施模块，登录接口继续放行。
- `1.3.3` 已完成：登录控制器、应用服务、统一响应和异常处理已迁到新模块结构。
- `1.3.3.1` 已完成：后端按 `bh-im-server` 风格完成六模块分层重构，旧 `backend/src` 单模块残留已移除。

## 本次完成内容

- `backend/pom.xml` 改为父级 Reactor，新增：
  - `attendance-server-shared`
  - `attendance-server-domain`
  - `attendance-server-infrastructure`
  - `attendance-server-application`
  - `attendance-server-interfaces`
  - `attendance-server-starter`
- Java 包根统一切换为 `com.attendance.server.*`
- 登录 DTO 调整到 `application.auth.dto`，避免 `application -> interfaces` 反向依赖
- `UserService` 契约放到 `domain.user.service`，`UserServiceImpl` 放到 `infrastructure.persistence.service`
- `GlobalExceptionHandler` 移到 `interfaces.rest.error`
- 测试按模块归位：
  - `starter`: 结构、上下文、配置、安全、SQL 测试
  - `interfaces`: 控制器 HTTP 契约测试
  - `application`: 登录应用服务测试
  - `infrastructure`: JWT 工具与过滤器测试

## 已完成验证

- `backend`: `mvn compile`
- `backend`: `mvn test`
- `backend`: `mvn checkstyle:check`

## 阻塞项

- 命令行默认 JDK 仍指向旧版本；后端 Maven 验证需要显式切到 `%USERPROFILE%\\.jdks\\ms-21.0.10`
- Docker 相关任务仍因本地环境缺失而阻塞

## 下一步建议

1. 开始 `1.3.4`，在新多模块结构上补齐 `GET /api/auth/profile`、`PUT /api/auth/profile`、`PUT /api/auth/password`
2. 后续新增领域能力时，默认沿用当前模块边界：`interfaces -> application -> infrastructure -> domain -> shared`
3. 若继续扩展认证链路，优先在模块内补测试，不要再回到聚合式单模块测试布局
