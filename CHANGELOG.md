# 变更日志

所有重要变更将记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
项目遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

---

## [Unreleased]

### 添加

- 初始项目结构
- Harness Engineering 规范文档（CLAUDE.md, AGENTS.md）
- 任务清单（docs/task-list.md）
- 完成标准（docs/definition-of-done.md）
- 数据库设计文档
- 最小 Spring Boot 后端骨架（启动类、基础配置、上下文启动测试）
- Vue 3 前端骨架（Vite + TypeScript + Vue Router + Pinia + Element Plus + 登录页占位）
- 前端测试分层约定：组件测试默认 Vitest，E2E 默认 Playwright

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
## 文档补充说明（2026-04-22）

### 新增

- Harness 规范补充说明文档
- 跨会话产物模板（`session-handoff.md`, `.harness/learnings.md`）
- 阶段回顾目录说明（`docs/retrospectives/README.md`）

### 调整

- 任务状态统一为 `[ ] / [~] / [x] / [!]`
- 主规范补充会话交接与经验沉淀要求
- 主规范补充 `/health`、metrics、`X-Request-ID` 可观测性约定
- 新增 `docs/autonomy-levels.md`
- 新增 `docs/checklists/change-preflight.md`
- 新增 `docs/invariants-and-guardrails.md`
- 新增 `docs/README.md`
- 新增 `docs/tech-debt.md`
- 优化 `AGENTS.md` 与 `CLAUDE.md` 顶部结构，改为入口式阅读
- 新增最小 Spring Boot 后端骨架（启动类、基础配置、上下文启动测试）

---
