# Agent 行为指令 — Harness Engineering

> 本文件定义 Agent 的工作流程、决策树、协作模式和自动化触发规则。
> 与 CLAUDE.md 配合使用：CLAUDE.md 定义"做什么"，AGENTS.md 定义"怎么做"。

## 核心原则

1. **任务驱动**：所有工作围绕 `docs/task-list.md` 展开
2. **质量优先**：每个阶段必须通过质量门禁
3. **自动化优先**：能自动化的绝不手动
4. **并行优先**：独立任务必须并行执行
   - 当任务可拆为 2 个及以上无共享写入、无顺序依赖的子任务时，默认使用多个 Agent 并行执行
5. **TDD 强制**：新功能/修复必须先写测试
6. **注释语言统一**：代码需要注释时默认使用中文，优先写“为什么/边界/约束”，避免无信息量的翻译式注释
7. **任务收尾默认提交**：任务完成且验证通过后，默认自动 `commit + push`；除非用户明确要求不要提交、不要推送或暂缓合并

## 任务启动流程

### 1. 接收任务

```
用户请求 → 理解需求 → 查阅 task-list.md → 确认当前任务
```

**强制检查项**：
- [ ] 查阅 `docs/task-list.md`，确认任务状态
- [ ] 如果任务未登记，先在 task-list.md 中创建任务条目
- [ ] 确认任务的前置依赖已完成
- [ ] 理解任务的验收标准

### 2. 选择工作模式

| 任务类型 | 工作模式 | 触发条件 |
|---------|---------|---------|
| 新功能实现 | TDD + 多 Agent | 需要新增代码 |
| Bug 修复 | TDD + Code Review | 修改现有代码 |
| 重构 | Planner + TDD | 影响多个文件 |
| 文档更新 | 单 Agent | 仅修改文档 |
| 配置修改 | 单 Agent + 验证 | 修改配置文件 |

### 3. 任务分解

**复杂任务（>3 步骤）**：
1. 自动调用 **planner** agent
2. 生成实施计划（包含子任务、依赖关系、风险点）
3. 在 task-list.md 中登记所有子任务
4. 按依赖顺序执行

**简单任务（≤3 步骤）**：
1. 直接执行
2. 在 task-list.md 中标记进度

## Agent 自动触发规则

### 强制触发（MUST）

| 场景 | Agent | 触发时机 | 跳过条件 |
|------|-------|---------|---------|
| 新功能/Bug 修复 | tdd-guide | 开始编码前 | 仅修改文档/配置 |
| 代码已写入/修改 | code-reviewer | 写入后立即 | 无 |
| 涉及认证/授权/用户输入 | security-reviewer | 写入后立即 | 无 |
| 构建失败 | build-error-resolver | 构建失败时 | 无 |
| 复杂功能（>5 文件） | planner | 开始前 | 用户明确拒绝 |

### 推荐触发（SHOULD）

| 场景 | Agent | 触发时机 |
|------|-------|---------|
| 架构决策 | architect | 设计阶段 |
| 关键用户流程 | e2e-runner | 功能完成后 |
| 代码维护 | refactor-cleaner | 阶段完成后 |
| 文档更新 | doc-updater | 代码变更后 |

## TDD 工作流（强制）

所有涉及代码变更的任务必须遵循 TDD：

```
1. [RED]   写测试 → 运行测试（必须失败）
2. [GREEN] 写实现 → 运行测试（必须通过）
3. [REFACTOR] 重构 → 运行测试（必须通过）
4. [REVIEW] Code Review → 修复问题
5. [DOD] 完成标准核查 → 标记完成
```

### 前端测试工具默认规则

- 前端单元/组件测试：默认使用 `Vitest + Vue Test Utils`
- 前端端到端测试：默认使用 `Playwright`
- 当前任务如果只涉及骨架、占位页、静态页面或局部组件，不强制新增 Playwright
- 当前任务如果涉及关键用户流程联动，必须补 Playwright 用例

### 详细步骤

#### 阶段 1: RED（写测试）

```bash
# 后端测试
cd backend
mvn test -Dtest=AttendanceServiceTest

# 前端测试
cd frontend
npm run test
```

**检查项**：
- [ ] 测试文件已创建
- [ ] 测试用例覆盖核心场景
- [ ] 运行测试，确认失败（因为实现不存在）

#### 阶段 2: GREEN（写实现）

```bash
# 实现代码
# 后端：Controller → Service → Mapper → Entity
# 前端：View → Store → API Layer

# 运行测试
mvn test -Dtest=AttendanceServiceTest  # 必须通过
```

**检查项**：
- [ ] 实现代码已写入
- [ ] 遵循分层架构（不跨层调用）
- [ ] 运行测试，确认通过

#### 阶段 3: REFACTOR（重构）

```bash
# 重构代码（提取函数、优化逻辑、消除重复）
# 运行测试
mvn test -Dtest=AttendanceServiceTest  # 必须通过
```

**检查项**：
- [ ] 代码可读性提升
- [ ] 无重复代码
- [ ] 测试仍然通过

#### 阶段 4: REVIEW（代码审查）

**自动触发 code-reviewer agent**：
- 检查代码质量
- 检查安全问题
- 检查性能问题

**处理审查结果**：
- CRITICAL/HIGH 问题：必须修复
- MEDIUM 问题：尽量修复
- LOW 问题：记录到 TODO

#### 阶段 5: DOD（完成标准核查）

参考 `docs/definition-of-done.md`，逐项核对：

**代码质量**：
- [ ] 测试覆盖率 ≥ 80%
- [ ] 所有测试通过
- [ ] Lint 零错误
- [ ] 类型检查通过

**日志和异常**：
- [ ] 关键操作有日志
- [ ] 异常处理完整（继承 BusinessException）

**文档**：
- [ ] API 变更已更新到 docs/api-spec.md
- [ ] task-list.md 状态已更新
- [ ] CHANGELOG.md 已更新

## 多 Agent 协作模式

### 模式 1: 串行协作（有依赖）

```
planner → tdd-guide → code-reviewer → security-reviewer → DoD 核查
```

**适用场景**：单一功能开发

### 模式 2: 并行协作（无依赖）

```
┌─ Agent 1: 实现打卡模块 ─┐
├─ Agent 2: 实现请假模块 ─┤ → 汇总 → Code Review → DoD 核查
└─ Agent 3: 实现补卡模块 ─┘
```

**适用场景**：多个独立模块同时开发

**强制要求**：
- 独立任务必须并行执行
- 当任务可拆为 2 个及以上无共享写入、无顺序依赖的子任务时，默认使用多个 Agent 并行执行
- 主线程负责拆分任务、定义写入边界、汇总结果、最终验证、文档更新和 `commit + push`
- 子 Agent 只负责各自明确范围内的实现或分析，不得越界修改未分配文件
- 如果子任务共享同一写入文件、强依赖上一步结果，或拆分协调成本高于收益，则禁止为了并行而并行
- 每个 Agent 完成后立即触发 code-reviewer
- 所有 Agent 完成后统一 DoD 核查

### 模式 3: 多视角分析（复杂问题）

```
┌─ Agent 1: 安全视角 ─┐
├─ Agent 2: 性能视角 ─┤ → 综合评估 → 决策
└─ Agent 3: 可维护性视角 ─┘
```

**适用场景**：架构决策、技术选型

## 质量门禁检查点

### L1: 工具级（自动）

**自动化配置**（详见 docs/feedback-loop.md）：
- Java 文件保存后 → 自动编译检查
- Vue/TS 文件保存后 → 自动类型检查
- 危险命令 → 自动拦截

### L2: 任务级（手动触发）

**每个任务完成前**：

**方式 1：运行质量门禁脚本（推荐）**
```bash
scripts\check.bat
```

**方式 2：手动执行**
```bash
# 后端
cd backend
mvn clean test                    # 运行所有测试
mvn checkstyle:check              # 代码规范检查
mvn jacoco:report                 # 生成覆盖率报告

# 前端
cd frontend
npm run lint                      # ESLint 检查
npm run type-check                # TypeScript 类型检查
npm run test                      # 运行测试
```

**检查项**：
- [ ] Lint 零错误
- [ ] 类型检查通过
- [ ] 测试覆盖率 ≥ 80%
- [ ] 所有测试通过

### L3: 会话级（自动）

**会话结束时**（详见 docs/feedback-loop.md）：
- 运行 lint + 类型检查
- 提醒核对 DoD
- 提醒更新 task-list.md
- 提醒更新 CHANGELOG.md

### L4: 阶段级（手动）

**每个阶段完成后**：
- 在 `docs/retrospectives/` 创建回顾文档
- 总结经验教训
- 更新 CHANGELOG.md

## 异常处理策略

### 构建失败

```
构建失败 → 自动触发 build-error-resolver → 分析错误 → 修复 → 重新构建
```

**禁止操作**：
- 禁止跳过 hooks（--no-verify）
- 禁止强制推送（git push --force）
- 禁止删除测试以通过构建

### 测试失败

```
测试失败 → 分析原因 → 修复实现（不是修复测试） → 重新测试
```

**决策树**：
1. 测试逻辑错误？→ 修复测试
2. 实现逻辑错误？→ 修复实现
3. 测试环境问题？→ 修复环境

### 代码审查不通过

```
Code Review 失败 → 查看问题列表 → 修复 CRITICAL/HIGH → 重新审查
```

**优先级**：
- CRITICAL：必须立即修复
- HIGH：必须修复
- MEDIUM：尽量修复
- LOW：记录到 TODO

### 安全问题

```
发现安全问题 → 立即停止 → 触发 security-reviewer → 修复 → 重新审查
```

**强制要求**：
- 发现安全问题必须立即停止当前工作
- 修复后必须重新运行 security-reviewer
- 所有安全问题必须在提交前解决
- 参考 docs/security-checklist.md 进行全量检查

### Harness 审查

**何时运行 Harness 审查**：
- 重大功能完成后
- 阶段切换前
- 代码交接前

**执行方式**：
```bash
# 查阅 docs/harness-checklist.md
# 逐项检查：
# 1. Agent 指令（AGENTS.md）
# 2. 工具设计
# 3. 上下文传递
# 4. 规划产物
# 5. 权限和沙箱
# 6. 验证循环
```

## 决策树

### 何时使用 planner？

```
任务复杂度评估
├─ 影响文件数 > 5？ → YES → 使用 planner
├─ 涉及架构变更？ → YES → 使用 planner
├─ 需要多个阶段？ → YES → 使用 planner
└─ 简单修改？ → NO → 直接执行
```

### 何时并行执行？

```
任务依赖分析
├─ 任务之间有依赖？ → YES → 串行执行
├─ 任务完全独立？ → YES → 并行执行
└─ 部分依赖？ → 分组：独立的并行，有依赖的串行
```

### 何时触发 security-reviewer？

```
代码变更分析
├─ 涉及认证/授权？ → YES → 必须触发
├─ 处理用户输入？ → YES → 必须触发
├─ 涉及数据库操作？ → YES → 必须触发
├─ 涉及文件操作？ → YES → 必须触发
└─ 纯逻辑计算？ → NO → 可选
```

## 任务完成标准

### 标记任务完成前必须确认

1. **代码质量**
   - [ ] 所有测试通过
   - [ ] 测试覆盖率 ≥ 80%
   - [ ] Lint 零错误
   - [ ] 类型检查通过

2. **代码审查**
   - [ ] code-reviewer 已运行
   - [ ] CRITICAL/HIGH 问题已修复
   - [ ] security-reviewer 已运行（如适用）

3. **安全检查**（详见 docs/security-checklist.md）
   - [ ] OWASP Top 10 检查通过
   - [ ] 无敏感信息泄露
   - [ ] 权限校验到位
   - [ ] 输入验证完整

4. **文档更新**
   - [ ] task-list.md 状态已更新
   - [ ] CHANGELOG.md 已更新
   - [ ] API 文档已更新（如适用）

5. **Git 收尾**
   - [ ] 已完成 `commit`
   - [ ] 已完成 `push`（除非用户明确禁止或要求暂缓）

6. **日志和异常**
   - [ ] 关键操作有日志
   - [ ] 异常处理完整

### 默认收尾顺序

任务达到 DoD 后，默认按以下顺序收尾：

1. 运行验证命令
2. 更新 `docs/task-list.md`
3. 更新 `CHANGELOG.md`
4. 更新 `session-handoff.md`（以及必要时的 `.harness/learnings.md`）
5. `git status` 自检提交范围
6. `git commit`
7. `git push`

除非用户明确要求暂停在某一步，否则不要只做前半段。

### 更新任务状态

```markdown
# docs/task-list.md

## 阶段 1: 基础设施搭建

- [x] 1.1 项目初始化（Spring Boot + Vue 3）
  - 完成时间：2026-04-22
  - 测试覆盖率：85%
  - 审查状态：通过
```

## 工作流程示例

### 示例 1: 实现新功能（用户打卡）

```
1. 查阅 task-list.md → 确认任务 "实现用户打卡"
2. 任务复杂度评估 → 中等（3-5 文件）
3. 触发 planner agent → 生成实施计划
4. 触发 tdd-guide agent → 开始 TDD 流程
   4.1 [RED] 写测试 AttendanceServiceTest
   4.2 运行测试 → 失败（预期）
   4.3 [GREEN] 实现 clockIn/clockOut 方法
   4.4 运行测试 → 通过
   4.5 [REFACTOR] 优化代码
   4.6 运行测试 → 通过
5. 触发 code-reviewer agent → 审查代码
6. 触发 security-reviewer agent → 安全审查（涉及认证）
7. 修复审查问题
8. DoD 核查 → 逐项确认
9. 更新 task-list.md → 标记完成
10. 更新 CHANGELOG.md
```

### 示例 2: 修复 Bug

```
1. 查阅 task-list.md → 确认任务 "修复打卡时间计算错误"
2. 触发 tdd-guide agent
   2.1 [RED] 写失败的测试（复现 Bug）
   2.2 运行测试 → 失败（确认 Bug）
   2.3 [GREEN] 修复代码
   2.4 运行测试 → 通过
3. 触发 code-reviewer agent
4. DoD 核查
5. 更新 task-list.md
6. 更新 CHANGELOG.md
```

### 示例 3: 并行开发多个模块

```
1. 查阅 task-list.md → 确认任务 "实现打卡、请假、补卡三个模块"
2. 任务依赖分析 → 三个模块独立
3. 并行触发 3 个 Agent：
   ├─ Agent 1: 实现打卡模块（TDD + Code Review）
   ├─ Agent 2: 实现请假模块（TDD + Code Review）
   └─ Agent 3: 实现补卡模块（TDD + Code Review）
4. 等待所有 Agent 完成
5. 统一 DoD 核查
6. 更新 task-list.md
7. 更新 CHANGELOG.md
```

## 禁止操作清单

### 绝对禁止

- ❌ 跳过测试（"先实现，后补测试"）
- ❌ 跳过 Code Review
- ❌ 跳过 DoD 核查
- ❌ 修改测试以通过构建（应该修复实现）
- ❌ 硬编码敏感信息（密码、密钥等）
- ❌ 跨层调用（Controller 直接调用 Mapper）
- ❌ 直接使用 System.out.println（应该用 Logger）

### 需要确认

- ⚠️ 删除文件/分支
- ⚠️ 强制推送（git push --force）
- ⚠️ 修改数据库 schema（需要创建 migration）
- ⚠️ 修改 API 接口（需要更新文档）
- ⚠️ 修改配置文件（需要验证）

## 快速参考

### 常用命令

```bash
# 后端
cd backend
mvn clean test                      # 运行所有测试
mvn checkstyle:check                # 代码规范检查
mvn jacoco:report                   # 生成覆盖率报告
mvn spring-boot:run                 # 启动开发服务器

# 前端
cd frontend
npm run dev                         # 启动开发服务器
npm run lint                        # ESLint 检查
npm run type-check                  # TypeScript 类型检查
npm run test                        # 运行测试
```

### 关键文件路径

- 任务清单：`docs/task-list.md`
- 完成标准：`docs/definition-of-done.md`
- 变更日志：`CHANGELOG.md`
- API 规范：`docs/api-spec.md`
- 架构文档：`docs/architecture.md`
- 反馈循环：`docs/feedback-loop.md`
- 安全检查：`docs/security-checklist.md`
- Harness 审查：`docs/harness-checklist.md`
- 质量门禁脚本：`scripts/check.bat`
- CI/CD 配置：`.github/workflows/ci.yml`

### Agent 快速选择

| 需求 | Agent |
|------|-------|
| 规划复杂任务 | planner |
| 写测试 + 实现 | tdd-guide |
| 代码审查 | code-reviewer |
| 安全审查 | security-reviewer |
| 修复构建错误 | build-error-resolver |
| 架构决策 | architect |
| E2E 测试 | e2e-runner |
| 清理死代码 | refactor-cleaner |
| 更新文档 | doc-updater |

---

**记住**：Agent 的目标是高质量、可维护的代码，而不是快速完成任务。质量优先，速度其次。
# Harness 补充工作流

## 任务状态补充

- 任务状态统一使用 `[ ]`、`[~]`、`[x]`、`[!]`
- `[!]` 表示阻塞中，必须写明原因、影响范围和下一步处理方式

## 跨会话产物

- `session-handoff.md`
  - 记录当前状态、阻塞项、下一步和验证结果
- `.harness/learnings.md`
  - 记录本次任务学到的经验、踩坑和后续建议

## 执行要求

- 任务完成、中断或交接前必须更新 `session-handoff.md`
- 出现可复用经验、重复错误或重要决策时更新 `.harness/learnings.md`
- 这两个文件是补充上下文，不替代 `docs/task-list.md` 和 `CHANGELOG.md`

## 可观测性基线

- 规范层要求后端提供 `GET /health` 健康检查端点
- 规范层要求系统具备 metrics 入口，最少覆盖请求量、错误率、平均响应时间
- 所有请求应生成唯一 `request_id`，并通过 `X-Request-ID` 返回给客户端
- 关键日志、错误上报和慢请求记录应尽可能带上 `request_id`

---
# AGENTS Quick Start

首读顺序：
1. `docs/task-list.md`
2. `docs/checklists/change-preflight.md`
3. `docs/autonomy-levels.md`
4. `docs/invariants-and-guardrails.md`
5. `docs/README.md`
6. 本文件其余章节

使用原则：
- 本文件负责“怎么做”
- `CLAUDE.md` 负责“做什么”
- 专项规则优先看对应文档，不要把所有细节都回忆成本文件正文

专项文档：
- `docs/checklists/change-preflight.md`
- `docs/autonomy-levels.md`
- `docs/invariants-and-guardrails.md`
- `docs/README.md`
- `session-handoff.md`
- `.harness/learnings.md`

---
## 规范巡检机制

为避免规范与实际任务逐步偏离，自 2026-04-22 起，每完成一个任务后默认执行一次轻量规范巡检。

### 巡检内容

- 是否出现新的规则缺口
- 是否存在重复规则或职责冲突
- 是否需要把新增文件补进 `docs/README.md`
- 是否需要更新 `docs/tech-debt.md`

### 处理规则

- 低风险规范修正：可直接更新文档
- 高风险规范调整：先确认再改
- 暂不处理的问题：登记到 `docs/tech-debt.md`

---
