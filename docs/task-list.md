# 考勤系统开发任务清单

> **项目**: harness-engineering-attendance-system  
> **创建日期**: 2026-04-22  
> **当前阶段**: Phase 1 - 基础设施搭建

---

## 阶段 1: 基础设施搭建（第 1 周）

### 1.1 项目初始化

- [x] 1.1.1 创建 Spring Boot 项目结构
  - 优先级: REQUIRED
  - 依赖: 无
  - 验收标准:
    - [ ] Maven 项目结构完整
    - [ ] pom.xml 配置正确（Spring Boot 3.2 + MyBatis-Plus）
    - [ ] 应用可以启动
  - 完成时间：2026-04-22
  - 说明：补齐最小 Spring Boot 启动骨架；当前验证命令需显式使用 IDEA 的 JDK 21（`%USERPROFILE%\\.jdks\\ms-21.0.10`）

- [x] 1.1.2 创建 Vue 3 项目结构
  - 优先级: REQUIRED
  - 依赖: 无
  - 验收标准:
    - [x] Vite + Vue 3 + TypeScript 项目结构
    - [x] Element Plus 集成
    - [x] 前端开发服务器可启动
  - 完成时间：2026-04-22
  - 说明：已补齐 Vite + Vue 3 + TypeScript + Vue Router + Pinia + Element Plus 骨架，并提供登录页占位与基础测试

- [!] 1.1.3 配置 Docker 环境
  - 优先级: REQUIRED
  - 依赖: 1.1.1, 1.1.2
  - 验收标准:
    - [ ] docker-compose.yml 配置完整
    - [ ] MySQL 容器可启动
    - [ ] 一键启动所有服务
  - 阻塞原因：当前本地开发环境未安装 Docker，暂不具备容器验证条件
  - 影响范围：Phase 1 的容器化相关任务顺延，不影响数据库脚本与后端基础集成工作
  - 下一步处理：先继续 1.2.1 数据库初始化脚本，待具备 Docker 环境后再回补 1.1.3

### 1.2 数据库设计

- [x] 1.2.1 创建数据库初始化脚本
  - 优先级: REQUIRED
  - 依赖: 无
  - 验收标准:
    - [x] sql/init.sql 包含所有表结构
    - [x] 包含初始数据（管理员账号）
    - [x] 脚本可执行无错误
  - 完成时间：2026-04-23
  - 说明：补齐初始化脚本结构校验测试，覆盖核心表存在性、管理员种子数据、全局考勤规则，以及建表阶段禁止前向外键引用
  - 说明：已修复 `department` 表在建表阶段前向引用 `user` 表的问题，改为在 `user` 表创建后再追加 `manager_id` 外键
  - 说明：已在本地 MySQL 8.0.16 实例中真实执行 `sql/init.sql`，验证通过

- [x] 1.2.2 配置 MyBatis-Plus
  - 优先级: REQUIRED
  - 依赖: 1.1.1
  - 验收标准:
    - [x] 数据源配置正确
    - [x] Mapper 扫描配置
    - [x] 分页插件配置
  - 完成时间：2026-04-23
  - 说明：在 `application.yml` 中补齐 MySQL 数据源配置，支持通过 `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` 覆盖
  - 说明：启动类已开启 `@MapperScan("com.attendance.mapper")`
  - 说明：新增 `MyBatisPlusConfig`，注册 MySQL 分页拦截器
  - 说明：已通过 `MyBatisPlusConfigTest` 验证上述三项配置

### 1.3 认证授权模块

- [x] 1.3.1 实现 JWT 工具类
  - 优先级: REQUIRED
  - 依赖: 1.1.1
  - 验收标准:
    - [x] Token 生成/验证/刷新
    - [x] 单元测试覆盖率 ≥ 80%
  - 完成时间：2026-04-23
  - 说明：新增 `JwtTokenProvider`，支持 access token 与 refresh token 生成、校验、解析和刷新
  - 说明：JWT 密钥与过期时间通过 `jwt.*` 配置读取，支持环境变量覆盖
  - 说明：已通过 `JwtTokenProviderTest` 验证生成、校验、刷新、篡改拒绝和 Claims 解析

- [x] 1.3.2 配置 Spring Security
  - 优先级: REQUIRED
  - 依赖: 1.3.1
  - 验收标准:
    - [x] JWT 认证过滤器
    - [x] 权限校验拦截器
    - [x] 公开接口配置（登录）
  - 完成时间：2026-04-23
  - 说明：新增 `SecurityConfig`，启用无状态安全链，放行 `/api/auth/login`，其余请求默认要求认证
  - 说明：新增 `JwtAuthenticationFilter`，从 Bearer Token 恢复用户名和角色权限到 `SecurityContext`
  - 说明：已通过 `SecurityConfigTest` 与 `JwtAuthenticationFilterTest` 验证公开接口、受保护接口和过滤器行为

- [x] 1.3.3 实现用户登录接口
  - 优先级: REQUIRED
  - 依赖: 1.3.2
  - 验收标准:
    - [x] POST /api/auth/login 接口
    - [x] 返回 JWT Token
    - [x] 错误处理完整
  - 完成时间：2026-04-24
  - 说明：新增 `AuthController`、`AuthService`、`UserMapper` 和最小登录链路实体/DTO，打通用户名查询、密码校验、JWT access/refresh token 返回
  - 说明：补齐统一响应、`BusinessException`、全局异常处理和登录参数校验，覆盖 200/400/401/403 响应语义
  - 说明：后端回归通过 `mvn test` 与 `mvn checkstyle:check`

- [x] 1.3.3.1 后端多模块分层重构（参照 bh-im-server）
  - 优先级: REQUIRED
  - 依赖: 1.3.3
  - 验收标准:
    - [x] `backend` 已拆为多模块 Maven Reactor（后续在 1.3.3.2 纠偏为 `attendance-common + attendance-server/*` 结构）
    - [x] Java 包根统一为 `com.attendance.server`
    - [x] 现有认证链路行为不变，后端 `mvn test` 与 `mvn checkstyle:check` 全部通过
    - [x] `docs/task-list.md`、`CHANGELOG.md`、`session-handoff.md` 已同步更新
  - 完成时间：2026-04-24
  - 说明：参照 `bh-im-server` 的职责分层，将 `backend` 从单模块重构为多模块 Reactor，保留当前认证能力不变
  - 说明：测试已按模块归位到 `starter / interfaces / application / infrastructure`，旧 `backend/src` 单模块残留已移除

- [x] 1.3.3.2 后端分层架构纠偏（对齐 bh-im / seckill）
  - 优先级: REQUIRED
  - 依赖: 1.3.3.1
  - 验收标准:
    - [x] `interfaces` 只依赖 `application` 和公共模块，不直接依赖 `infrastructure`
    - [x] `application` 不依赖 `infrastructure` 模块且不直接 import `infrastructure` 包
    - [x] 用户查询按 `domain.repository` 契约和 `infrastructure.repository` 实现分层
    - [x] 现有登录链路行为不变，后端 `mvn test` 与 `mvn checkstyle:check` 通过
  - 完成时间：2026-04-24
  - 说明：当前任务只纠偏登录链路和模块依赖，不新增 `1.3.4` 用户信息接口
  - 说明：后端结构已调整为 `attendance-common` 与 `attendance-server` 聚合服务，`attendance-server` 内保留 `starter / interfaces / application / domain / infrastructure` 五层模块。
  - 说明：登录链路已改为 `AuthController -> AuthApplicationService -> UserService/UserRepository -> UserRepositoryImpl -> UserMapper`。
  - 说明：已严格归位 REST 入站适配层，`AuthController` 与 `GlobalExceptionHandler` 统一位于 `interfaces.rest.*` 包。
  - 说明：已严格归位安全链装配，`SecurityConfig` 位于 `starter.config`，`JwtAuthenticationFilter` 保留在 `infrastructure.security`。

- [ ] 1.3.4 实现用户信息管理
  - 优先级: REQUIRED
  - 依赖: 1.3.3
  - 验收标准:
    - [ ] GET /api/auth/profile 接口
    - [ ] PUT /api/auth/profile 接口
    - [ ] PUT /api/auth/password 接口

### 1.4 用户管理模块（ADMIN）

- [ ] 1.4.1 实现用户 CRUD 接口
  - 优先级: REQUIRED
  - 依赖: 1.3.2
  - 验收标准:
    - [ ] 用户列表（分页）
    - [ ] 创建用户
    - [ ] 更新用户
    - [ ] 启用/禁用用户

### 1.5 部门管理模块（ADMIN）

- [ ] 1.5.1 实现部门 CRUD 接口
  - 优先级: REQUIRED
  - 依赖: 1.3.2
  - 验收标准:
    - [ ] 部门树查询
    - [ ] 创建部门
    - [ ] 更新部门

---

## 阶段 2: 核心功能开发（第 2-3 周）

### 2.1 考勤打卡模块

- [ ] 2.1.1 实现打卡核心逻辑
  - 优先级: REQUIRED
  - 依赖: 1.3.2
  - 验收标准:
    - [ ] 上班打卡
    - [ ] 下班打卡
    - [ ] 考勤状态计算（NORMAL/LATE/EARLY_LEAVE）

- [ ] 2.1.2 实现打卡记录查询
  - 优先级: REQUIRED
  - 依赖: 2.1.1
  - 验收标准:
    - [ ] 今日考勤查询
    - [ ] 历史记录查询（分页）
    - [ ] 月度统计

### 2.2 请假管理模块

- [ ] 2.2.1 实现请假申请
  - 优先级: REQUIRED
  - 依赖: 1.3.2
  - 验收标准:
    - [ ] 提交请假申请
    - [ ] 请假类型验证
    - [ ] 时间冲突检测

- [ ] 2.2.2 实现请假审批
  - 优先级: REQUIRED
  - 依赖: 2.2.1
  - 验收标准:
    - [ ] 待审批列表
    - [ ] 审批通过
    - [ ] 审批拒绝

### 2.3 补卡申请模块

- [ ] 2.3.1 实现补卡申请
  - 优先级: REQUIRED
  - 依赖: 1.3.2
  - 验收标准:
    - [ ] 提交补卡申请
    - [ ] 补卡类型（CLOCK_IN/CLOCK_OUT）

- [ ] 2.3.2 实现补卡审批
  - 优先级: REQUIRED
  - 依赖: 2.3.1
  - 验收标准:
    - [ ] 待审批列表
    - [ ] 审批通过（更新考勤记录）
    - [ ] 审批拒绝

---

## 阶段 3: 报表和增强（第 4 周）

### 3.1 考勤报表模块

- [ ] 3.1.1 实现个人报表
  - 优先级: REQUIRED
  - 依赖: 2.1.2
  - 验收标准:
    - [ ] 月度考勤统计
    - [ ] 图表展示

- [ ] 3.1.2 实现部门报表
  - 优先级: REQUIRED
  - 依赖: 2.1.2
  - 验收标准:
    - [ ] 部门考勤汇总
    - [ ] 数据导出（Excel）

### 3.2 E2E 测试

- [ ] 3.2.1 编写核心流程 E2E 测试
  - 优先级: REQUIRED
  - 依赖: 2.1.1, 2.2.1, 2.3.1
  - 验收标准:
    - [ ] 登录 → 打卡 → 查看记录
    - [ ] 请假申请 → 审批 → 查看状态

---

## 阶段 4: 部署和文档（第 5 周）

### 4.1 Docker 容器化

- [ ] 4.1.1 编写 Dockerfile
  - 优先级: REQUIRED
  - 依赖: 阶段 2 完成
  - 验收标准:
    - [ ] 后端镜像构建成功
    - [ ] 前端镜像构建成功

- [ ] 4.1.2 完善 docker-compose.yml
  - 优先级: REQUIRED
  - 依赖: 4.1.1
  - 验收标准:
    - [ ] 生产环境配置
    - [ ] 数据卷持久化

### 4.2 文档完善

- [ ] 4.2.1 编写部署文档
  - 优先级: REQUIRED
  - 依赖: 4.1.2
  - 验收标准:
    - [ ] 环境要求
    - [ ] 部署步骤
    - [ ] 常见问题

- [ ] 4.2.2 编写用户手册
  - 优先级: RECOMMENDED
  - 依赖: 阶段 3 完成
  - 验收标准:
    - [ ] 功能说明
    - [ ] 操作指南

---

## 任务状态说明

| 状态 | 说明 |
|------|------|
| [ ] | 未开始 |
| [🔄] | 进行中 |
| [x] | 已完成 |
| [⚠️] | 阻塞 |

## 完成标准（DoD）

每个任务标记为 `[x]` 前必须确认：
- [ ] 所有测试通过
- [ ] 测试覆盖率 ≥ 80%
- [ ] Lint 零错误
- [ ] Code Review 通过
- [ ] 文档已更新

---

**最后更新**: 2026-04-23  
**版本**: v1.0.0
# Harness 规范补充记录

> 本节用于补充任务状态模型、规范补充项和已知技术债，和下方原始任务清单并行生效。

## 状态模型补充

自 2026-04-22 起，任务状态统一解释为：

| 状态 | 说明 |
|------|------|
| [ ] | 未开始 |
| [~] | 进行中 |
| [x] | 已完成 |
| [!] | 阻塞中（需注明原因） |

## 已完成补充项

- [x] 补充任务状态模型（`[~]` / `[!]`）
  - 完成时间：2026-04-22
  - 说明：统一进行中与阻塞中表达

- [x] 新增跨会话产物（`session-handoff.md`、`.harness/learnings.md`）
  - 完成时间：2026-04-22
  - 说明：补齐会话交接与经验沉淀载体

- [x] 新增阶段回顾目录说明（`docs/retrospectives/README.md`）
  - 完成时间：2026-04-22
  - 说明：为 L4 阶段级反馈提供模板入口

- [x] 补充可观测性端点约定（`/health`、metrics、`X-Request-ID`）
  - 完成时间：2026-04-22
  - 说明：先落规范，不在本次任务中实现端点

## 已知技术债

技术债已迁移到 `docs/tech-debt.md` 统一维护，避免继续把任务清单和技术债混在一起。

## 规范结构优化记录

- [x] 新增 `docs/autonomy-levels.md`
  - 完成时间：2026-04-22
  - 说明：定义 Agent 的默认自主级别和确认边界

- [x] 新增 `docs/checklists/change-preflight.md`
  - 完成时间：2026-04-22
  - 说明：把动手前检查从大文档中抽成独立 checklist

- [x] 新增 `docs/invariants-and-guardrails.md`
  - 完成时间：2026-04-22
  - 说明：集中记录不可破坏的工程约束

- [x] 为 `AGENTS.md` 和 `CLAUDE.md` 增加入口型读法
  - 完成时间：2026-04-22
  - 说明：优化为“入口 + 专项文档”的结构

- [x] 新增 `docs/README.md` 与 `docs/tech-debt.md`
  - 完成时间：2026-04-22
  - 说明：补齐文档导航并将技术债从任务清单中拆出

---
