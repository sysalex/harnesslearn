# 反馈循环机制（Feedback Loop）

> Harness Engineering 的核心：通过多层反馈循环确保代码质量和开发效率

---

## 1. 四层反馈循环

```
L4 阶段级（小时/天）
  ↑
L3 会话级（分钟）
  ↑
L2 任务级（分钟）
  ↑
L1 工具级（秒）
```

---

## 2. L1 - 工具级反馈（自动触发）

**触发时机**：文件保存后立即执行  
**反馈速度**：秒级  
**目标**：即时发现格式和语法错误

### 2.1 后端（Java）

**配置**：`.claude/settings.json`

```json
{
  "hooks": {
    "PostToolUse": {
      "Write": [
        "cd backend && mvn compile -q"
      ],
      "Edit": [
        "cd backend && google-java-format --replace {{file_path}}"
      ]
    }
  }
}
```

**检查项**：
- ✅ Java 文件保存后自动编译
- ✅ 代码自动格式化（google-java-format）
- ✅ 语法错误即时提示

### 2.2 前端（Vue/TypeScript）

**配置**：`.claude/settings.json`

```json
{
  "hooks": {
    "PostToolUse": {
      "Write": [
        "cd frontend && npm run type-check"
      ],
      "Edit": [
        "cd frontend && npx prettier --write {{file_path}}",
        "cd frontend && npx eslint --fix {{file_path}}"
      ]
    }
  }
}
```

**检查项**：
- ✅ TS 文件保存后自动类型检查
- ✅ 代码自动格式化（Prettier）
- ✅ Lint 错误自动修复（ESLint）

### 2.3 危险操作拦截

**配置**：PreToolUse Hook

```json
{
  "hooks": {
    "PreToolUse": {
      "Bash": [
        "if command contains 'rm -rf /' or 'DROP DATABASE' then deny"
      ]
    }
  }
}
```

**拦截规则**：
- ❌ `rm -rf /` - 禁止删除根目录
- ❌ `DROP DATABASE` - 禁止删除数据库
- ❌ `git push --force` - 禁止强制推送
- ❌ `chmod 777` - 禁止开放所有权限

---

## 3. L2 - 任务级反馈（手动触发）

**触发时机**：每个任务完成前  
**反馈速度**：分钟级  
**目标**：确保任务质量符合 DoD 标准

### 3.1 TDD 工作流

```
1. [RED]   写测试 → 运行测试（必须失败）
   ↓
2. [GREEN] 写实现 → 运行测试（必须通过）
   ↓
3. [REFACTOR] 重构 → 运行测试（必须通过）
   ↓
4. [REVIEW] Code Review → 修复问题
   ↓
5. [DOD] 完成标准核查 → 标记完成
```

### 3.2 质量门禁命令

**后端**：
```bash
cd backend

# 1. 运行所有测试
mvn clean test

# 2. 代码规范检查
mvn checkstyle:check

# 3. 生成覆盖率报告
mvn jacoco:report

# 4. 静态代码分析
mvn sonar:sonar
```

**前端**：
```bash
cd frontend

# 1. ESLint 检查
npm run lint

# 2. TypeScript 类型检查
npm run type-check

# 3. 运行测试
npm run test

# 4. 生成覆盖率报告
npm run test:coverage
```

### 3.3 检查清单

在标记任务完成前，必须确认：

**代码质量**：
- [ ] 所有测试通过
- [ ] 测试覆盖率 ≥ 80%
- [ ] Lint 零错误
- [ ] 类型检查通过

**代码审查**：
- [ ] Code Review 已执行
- [ ] CRITICAL/HIGH 问题已修复

**文档更新**：
- [ ] `docs/task-list.md` 状态已更新
- [ ] `CHANGELOG.md` 已更新

---

## 4. L3 - 会话级反馈（自动触发）

**触发时机**：AI 会话结束时  
**反馈速度**：分钟级  
**目标**：确保会话产出的代码质量

### 4.1 Stop Hook 配置

**配置**：`.claude/settings.json`

```json
{
  "hooks": {
    "Stop": [
      "cd backend && mvn clean test -q",
      "cd frontend && npm run lint",
      "echo '请核对 docs/definition-of-done.md 清单'",
      "echo '请更新 docs/task-list.md 状态'"
    ]
  }
}
```

### 4.2 自动检查项

会话结束时自动运行：
1. ✅ 后端测试
2. ✅ 前端 lint 检查
3. ✅ 提醒核对 DoD 清单
4. ✅ 提醒更新任务状态
5. ✅ 提醒核对是否已完成默认 `commit + push`

### 4.3 会话交接

如果会话中断，更新以下文件：
- `session-handoff.md` - 当前状态和下一步
- `docs/task-list.md` - 任务进度
- `.harness/learnings.md` - 本次学习

---

## 5. L4 - 阶段级反馈（手动触发）

**触发时机**：每个阶段完成后  
**反馈速度**：小时/天级  
**目标**：总结经验教训，持续改进流程

### 5.1 阶段回顾

**创建回顾文档**：`docs/retrospectives/YYYY-MM-DD-<phase>-retrospective.md`

**模板**：
```markdown
# 阶段回顾 - <阶段名称>

**日期**: YYYY-MM-DD  
**阶段**: Phase X

## 做得好的

- 

## 需要改进的

- 

## 经验教训

- 

## 下一步行动

- 
```

### 5.2 质量指标追踪

| 指标 | 目标 | 当前 | 趋势 |
|------|------|------|------|
| 测试覆盖率 | ≥ 80% | 85% | ↑ |
| 首次通过率 | > 80% | 75% | → |
| 代码规范错误 | 0 | 0 | ↑ |
| Bug 数 | < 5 | 3 | ↓ |

### 5.3 更新 CHANGELOG

每个阶段完成后，更新 `CHANGELOG.md`：

```markdown
## [0.2.0] - 2026-04-29

### 添加
- 考勤打卡模块
- 请假管理模块

### 修复
- 修复打卡时间计算错误

### 改进
- 优化数据库查询性能
```

---

## 6. 自动化配置示例

### 6.1 Claude Code Hooks 配置（参考）

> **注意**：此配置适用于 Claude Code，其他 AI 助手可参考其中的质量门禁理念手动执行。

**文件**：`.claude/settings.json`（如果使用 Claude Code）

```json
{
  "permissions": {
    "allow": ["Bash(mvn *)", "Bash(npm *)", "Bash(git *)"],
    "deny": ["Bash(rm -rf /)", "Bash(DROP DATABASE)", "Bash(git push --force)"]
  },
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [{
          "type": "command",
          "command": "bash -c 'CMD=$(echo \"$CLAUDE_TOOL_INPUT_COMMAND\" | tr -d \"\\n\"); BLOCKED=\"\"; echo \"$CMD\" | grep -qiE \"rm -rf /|DROP DATABASE|git push --force\" && BLOCKED=\"危险命令被拦截: $CMD\"; if [ -n \"$BLOCKED\" ]; then echo \"[PreToolUse BLOCK] $BLOCKED\" >&2; exit 1; fi'"
        }]
      }
    ],
    "PostToolUse": [
      {
        "matcher": "Write|Edit",
        "hooks": [
          {
            "type": "command",
            "command": "bash -c 'if [[ \"$CLAUDE_TOOL_INPUT_FILE_PATH\" == *.java ]]; then cd backend && mvn compile -q 2>/dev/null && echo \"[OK] Java 编译成功\" || echo \"[FAIL] Java 编译失败\"; fi'"
          },
          {
            "type": "command",
            "command": "bash -c 'if [[ \"$CLAUDE_TOOL_INPUT_FILE_PATH\" =~ \\.(vue|ts|tsx|js)$ ]]; then cd frontend && npm run type-check 2>/dev/null && echo \"[OK] TypeScript 检查通过\" || echo \"[WARN] TypeScript 检查失败\"; fi'"
          }
        ]
      }
    ],
    "Stop": [
      {
        "hooks": [{
          "type": "command",
          "command": "bash -c 'echo \"\\n========================================\"; echo \"[质量门禁] 会话结束检查\"; echo \"========================================\"; echo \"请确认已核对 docs/definition-of-done.md 清单\"; echo \"请确认已更新 docs/task-list.md 状态\"; echo \"请确认已更新 CHANGELOG.md\"; echo \"========================================\"'"
        }]
      }
    ]
  }
}
```

### 6.2 手动执行质量门禁（通用）

如果不使用 Claude Code，可以在每个任务完成后手动运行：

**后端质量检查**：
```bash
cd backend

# 1. 编译检查
mvn compile -q

# 2. 运行测试
mvn clean test

# 3. 代码规范检查
mvn checkstyle:check

# 4. 生成覆盖率报告
mvn jacoco:report
```

**前端质量检查**：
```bash
cd frontend

# 1. TypeScript 类型检查
npm run type-check

# 2. ESLint 检查
npm run lint

# 3. 运行测试
npm run test
```

### 6.3 VS Code 配置（可选）

**文件**：`.vscode/settings.json`

```json
{
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "java.format.settings.url": "google-java-format.xml",
  "[java]": {
    "editor.defaultFormatter": "Google Java Format"
  },
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[vue]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  }
}
```

---

## 7. 反馈循环最佳实践

### 7.1 快速失败

- ❌ 不要等所有代码写完再测试
- ✅ 写一个函数就测试一个函数
- ✅ 测试失败立即修复

### 7.2 自动化优先

- ❌ 不要手动运行 lint 和测试
- ✅ 配置 Hooks 自动执行
- ✅ 让工具发现问题，而不是人工审查

### 7.3 持续改进

- ❌ 不要忽略重复出现的错误
- ✅ 记录常见错误模式
- ✅ 更新规范文档避免重复

### 7.4 质量门禁不可绕过

- ❌ 禁止使用 `--no-verify` 跳过 Hooks
- ❌ 禁止删除测试以通过构建
- ✅ 质量问题是红线，必须修复

---

## 8. 问题排查

### 8.1 Hooks 未触发

**问题**：文件保存后未自动格式化  
**解决**：
1. 检查 `.claude/settings.json` 配置
2. 检查文件路径是否正确
3. 查看日志确认 Hook 执行状态

### 8.2 测试失败但不知道原因

**排查步骤**：
1. 查看测试输出日志
2. 运行单个测试：`mvn test -Dtest=ClassName#methodName`
3. 使用调试模式：`mvn test -Dmaven.surefire.debug`

### 8.3 覆盖率不达标

**优化策略**：
1. 检查未覆盖的代码路径
2. 补充边界条件测试
3. 补充异常场景测试
4. 排除无需测试的代码（如配置类）

---

**最后更新**: 2026-04-22  
**版本**: v1.0.0
## 9. 任务后规范巡检

每个任务完成后，除代码/文档验证外，再补一轮轻量规范巡检：

- 检查本次任务是否暴露出新的规则缺口
- 检查现有规则是否重复、冲突或职责不清
- 检查新增文件是否需要加入 `docs/README.md` 导航
- 低风险规范修正直接落文档
- 高风险规范调整先确认
- 暂不处理的问题登记到 `docs/tech-debt.md`

---
