# 变更日志

所有重要变更将记录在此文件中。
格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，项目遵循[语义化版本](https://semver.org/lang/zh-CN/)。

---

## [Unreleased]

### 添加

- 初始项目结构
- Harness Engineering 规范文档：`CLAUDE.md`、`AGENTS.md`
- 任务清单：`docs/task-list.md`
- 完成标准：`docs/definition-of-done.md`
- 最小 Spring Boot 后端骨架
- Vue 3 前端骨架（Vite + TypeScript + Vue Router + Pinia + Element Plus）
- 后端 `InitSqlScriptTest`，用于校验初始化脚本结构和种子数据
- 后端 `MyBatisPlusConfigTest`，用于校验数据源、Mapper 扫描和分页插件配置
- 后端 `JwtTokenProviderTest`，用于校验 JWT 生成、校验、刷新和 claims 解析
- 后端 `SecurityConfigTest` 与 `JwtAuthenticationFilterTest`，用于校验登录接口放行和受保护接口鉴权
- 后端登录链路：`AuthController`、`AuthService`、`LoginRequest`、`LoginResponse`、统一响应与全局异常处理
- 后端 `UserService` 与 `UserServiceImpl`，用于承接 `IService` / `ServiceImpl` 风格的用户领域能力
- 后端 `UserServiceArchitectureTest`，用于锁定 MyBatis-Plus 服务层继承结构与公共实体约束
- 后端公共 `BaseEntity`，统一承接 `id`、`createdByUserId`、`createdByUserName`、`createdTime`、`updatedByUserId`、`updatedByUserName`、`updatedTime`、`enabledFlag`、`deletedFlag`
- 数据库迁移脚本 `sql/migrations/20260424_align_common_base_columns.sql`，用于把既有表结构对齐到 snake_case 公共列命名
- 数据库迁移脚本 `sql/migrations/20260424_fix_common_column_comments.sql`，用于补齐已更新公共列的列注释

### 修复

- 修正 `sql/init.sql` 中 `department` 表对 `user` 表的前向外键引用，改为在 `user` 表创建后追加 `manager_id` 外键
- 在本地 MySQL 8.0.16 实例中完成 `sql/init.sql` 的真实执行验证，确认核心表、管理员种子数据和全局考勤规则初始化成功
- 修正登录链路的 MyBatis-Plus 用法，服务层保留 `IService` / `ServiceImpl` 结构，但单表用户名查询改为 `lambdaQuery()`，不再保留 `UserMapper.xml`
- 修正公共实体实现，`BaseEntity` 与 `User` 均改用 Lombok 的 `@Getter` / `@Setter`，不再手写 getter/setter
- 修正公共字段列命名，Java 字段保留 camelCase，数据库列统一改为 `created_by_user_id`、`created_by_user_name`、`created_time`、`updated_by_user_id`、`updated_by_user_name`、`updated_time`、`enabled_flag`、`deleted_flag`
- 已通过 JDBC 在本地 `attendance_db` 实际执行 snake_case 重命名，并确认 6 张核心表都已完成字段同步
- 已通过 JDBC 在本地 `attendance_db` 实际补齐公共列注释，并确认 6 张核心表公共列注释均存在

### 变更

- 启用 MyBatis-Plus 数据源配置、Mapper 扫描和 MySQL 分页拦截器
- 新增 `JwtTokenProvider` 和 `jwt.*` 配置项，启用 JWT access/refresh token 基础能力
- 新增 `SecurityConfig` 与 `JwtAuthenticationFilter`，启用 Spring Security 无状态鉴权主链路
- 新增 `POST /api/auth/login` 登录接口，支持用户名密码校验、JWT access/refresh token 返回与 400/401/403 错误响应
- 强化规范中的任务收尾要求：任务完成后默认执行 `commit + push`
- 强化多 Agent 协作规则：满足无共享写入且无顺序依赖时默认并行，主线程负责集成、验证与提交

### 技术栈

- 后端：Spring Boot 3.2 + Java 17 + MyBatis-Plus
- 前端：Vue 3 + TypeScript + Vite + Element Plus
- 数据库：MySQL 8.0
- 认证：JWT

### 核心功能（规划中）

- 考勤打卡（上班/下班）
- 请假管理（申请/审批）
- 补卡申请（申请/审批）
- 考勤报表（个人/部门/公司）

---

## 版本说明

### [0.1.0] - 2026-04-22

**项目初始化**

- 创建项目结构
- 配置 Harness Engineering 规范
- 编写设计文档
- 制定开发计划

---

[Unreleased]: https://github.com/your-repo/attendance-system/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/your-repo/attendance-system/releases/tag/v0.1.0
