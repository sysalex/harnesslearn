# 文档导航

本文件是项目规范与设计文档的总入口。

## 1. 先看什么

第一次进入项目，建议按这个顺序阅读：

1. `../CLAUDE.md`
2. `../AGENTS.md`
3. `task-list.md`
4. `definition-of-done.md`
5. `feedback-loop.md`

## 2. 核心规范

- `task-list.md`
  当前任务与阶段推进
- `definition-of-done.md`
  任务完成标准
- `feedback-loop.md`
  多层质量反馈机制
- `security-checklist.md`
  安全检查规则
- `harness-checklist.md`
  Harness 自检清单

## 3. 治理规范

- `autonomy-levels.md`
  Agent 自治边界
- `checklists/change-preflight.md`
  动手前检查表
- `invariants-and-guardrails.md`
  不可破坏的工程约束

## 4. 设计文档

- `architecture.md`
- `api-spec.md`
- `domain-model.md`
- `adr/001-choose-mybatis-plus.md`
- `superpowers/specs/`
  历史临时设计草案，当前任务不再以此目录为有效规范来源
- `superpowers/plans/`
  历史临时实施计划，当前任务不再以此目录为有效规范来源

## 5. 运行辅助

- `../session-handoff.md`
  当前会话状态和下一步
- `../.harness/learnings.md`
  经验与踩坑记录
- `retrospectives/README.md`
  阶段回顾入口
- `tech-debt.md`
  技术债清单

## 6. 使用原则

- 入口文档只负责导航和摘要。
- 细则尽量放在专项文档里。
- 有新增规则时，优先更新对应专项文档，再回到入口文档补导航。

## 7. 规范巡检机制

每完成一个任务，默认执行一次轻量规范巡检：

- 看是否需要补规则
- 看是否需要删重复
- 看是否需要补导航
- 暂不处理的问题记录到 `tech-debt.md`

---
