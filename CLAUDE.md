# 考勤系统 — Harness Engineering 规范

## 项目概述

企业级在线考勤系统，支持打卡管理、请假审批、补卡申请、考勤报表统计。

## 技术栈

| 层次 | 技术 |
|------|------|
| 前端 | Vue 3 + TypeScript + Vite + Pinia + Vue Router |
| UI 组件 | Element Plus |
| 后端 | Java 17 + Spring Boot 3.2 + MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 认证 | JWT (jjwt) |
| 测试 - 前端 | Vitest + Vue Test Utils |
| 测试 - 后端 | JUnit 5 + Mockito |
| 包管理 - 前端 | npm |
| 包管理 - 后端 | Maven |

## 本地开发环境配置

### 端口锁定规则

**前端端口**: `5173`（固定）  
**后端端口**: `8080`（固定）  
**数据库端口**: `3306`（固定）

> **重要**: 端口被占用时，必须先杀掉占用端口的进程，再重新启动服务。禁止自动切换到其他端口。

```bash
# Windows - 查找占用端口的进程
netstat -ano | findstr ":5173"
netstat -ano | findstr ":8080"
netstat -ano | findstr ":3306"

# Windows - 杀掉进程（替换 PID 为实际进程 ID）
taskkill /F /PID <PID>
```

### 环境变量配置

`backend/.env`（不提交到 Git）:
```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/attendance_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root123
JWT_SECRET=your-256-bit-secret-key-here
JWT_EXPIRATION=86400000
```

`frontend/.env`:
```env
VITE_API_BASE_URL=http://localhost:8080
```

## 目录结构

```
harness-engineering-attendance-system/
├── CLAUDE.md                    # 本文件：Harness 核心规范
├── AGENTS.md                    # Agent 行为规范和工作流程
├── .claude/
│   └── settings.json            # Hooks 和权限配置
├── docs/
│   ├── architecture.md          # 系统架构设计
│   ├── api-spec.md              # API 接口规范
│   ├── domain-model.md          # 领域模型
│   ├── task-list.md             # 任务清单和进度追踪
│   ├── definition-of-done.md    # 完成标准（DoD）
│   ├── feedback-loop.md         # 反馈循环机制
│   └── adr/                     # 架构决策记录
├── frontend/                    # Vue 3 前端
│   ├── src/
│   │   ├── api/                 # API 请求层
│   │   ├── components/          # 通用组件
│   │   ├── views/               # 页面视图
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── router/              # 路由配置
│   │   ├── types/               # TypeScript 类型定义
│   │   └── utils/               # 工具函数
│   ├── tests/                   # 前端测试
│   └── vite.config.ts
├── backend/                     # Java Spring Boot 后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/attendance/
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # Controller 层
│   │   │   │   ├── service/     # Service 层
│   │   │   │   ├── mapper/      # Mapper 层
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── dto/         # DTO 类
│   │   │   │   ├── common/      # 公共类
│   │   │   │   ├── security/    # 安全相关
│   │   │   │   └── util/        # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── mapper/      # MyBatis XML
│   │   └── test/
│   │       └── java/com/attendance/
│   ├── pom.xml
│   └── Dockerfile
├── sql/
│   └── init.sql                 # 数据库初始化脚本
├── docker-compose.yml           # 本地开发环境
├── CHANGELOG.md                 # 变更日志
└── README.md                    # 项目说明
```

## 架构约束（Agent 必须遵守）

### 分层架构（后端）

```
Controller → Service → Mapper → Database
```

- Controller 只做参数校验和响应格式化，不含业务逻辑
- Service 包含所有业务逻辑，不直接操作数据库
- Mapper 封装所有数据库操作，返回实体对象
- 禁止跨层调用（Controller 不得直接调用 Mapper）

### 前端架构

```
View → Store (Pinia) → API Layer → Backend
```

- View 只负责渲染和用户交互
- 所有状态通过 Pinia Store 管理
- API 调用统一封装在 `src/api/` 目录

### API 响应格式（统一信封）

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1713772800000
}
```

## 编码规范

### Java

- 命名：camelCase，类名 PascalCase，常量 UPPER_SNAKE_CASE
- 类型注解：所有方法必须有完整类型注解
- 异常处理：使用自定义异常类，统一在全局异常处理器中处理
- 日志：使用 SLF4J + Logback，禁止使用 System.out.println

### TypeScript / Vue

- 严格模式：`strict: true`
- Composition API + `<script setup>` 语法
- 组件命名：PascalCase
- 文件组织：按功能模块，不按文件类型

## 安全规范

- 所有 API 端点（除登录）必须 JWT 认证
- 密码使用 BCrypt 哈希，禁止明文存储
- SQL 操作全部通过 MyBatis-Plus，禁止原生 SQL 拼接
- 敏感配置通过环境变量，禁止硬编码
- 接口限流：登录接口 5 次/分钟

## 测试要求

- 后端覆盖率 ≥ 80%（JaCoCo）
- 前端覆盖率 ≥ 80%（Vitest --coverage）
- E2E 测试覆盖核心用户流程（Playwright）
- 新功能必须先写测试（TDD）

### 测试框架

| 测试类型 | 框架 | 配置 |
|---------|------|------|
| 后端单元测试 | JUnit 5 + Mockito | `backend/pom.xml` |
| 后端集成测试 | Spring Boot Test + TestContainers | `backend/pom.xml` |
| 前端组件测试 | Vitest + Vue Test Utils | `frontend/vite.config.ts` |
| E2E 测试 | Playwright | `frontend/playwright.config.ts` |

## 领域模型（核心实体）

- **User**：用户（员工/主管/HR/管理员）
- **Department**：部门
- **AttendanceRecord**：考勤记录
- **LeaveApplication**：请假申请
- **MakeUpApplication**：补卡申请
- **AttendanceRule**：考勤规则

## 日志规范

### 后端（Java）

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);
    
    public void clockIn(Long userId) {
        log.info("用户打卡", userId);
    }
}
```

- 禁止使用 `System.out.println()`，统一使用 SLF4J
- 生产环境级别：INFO；开发环境：DEBUG

### 前端（TypeScript）

```ts
import { logger } from '@/utils/logger'
logger.info('操作描述', { userId: 1 })
```

- 禁止直接使用 `console.log()`，统一使用 `logger`
- 生产环境只输出 warn/error

## 异常处理规范

- 所有业务异常继承自 `BusinessException`
- 在 Service 层抛出具体异常（如 `AttendanceException`、`LeaveException`）
- 禁止在 Controller 层捕获异常后重新包装，由全局处理器统一处理
- 禁止吞掉异常（空 catch 块）

## 完成标准（DoD）

**任何任务在标记 `[x]` 之前，必须逐项核对 `docs/definition-of-done.md` 对应清单。**

核心要求：
- 测试覆盖率 ≥ 80%，所有测试通过
- lint + 类型检查零错误
- 关键操作有日志
- `docs/task-list.md` 状态已更新，`CHANGELOG.md` 已更新

## 可观测性

- 每个请求自动注入 `request_id`（拦截器），响应头返回 `X-Request-ID`
- 慢请求（> 1s）自动记录 warning 日志
- 前端全局错误捕获并上报
- 日志中必须携带 `request_id`、`user_id` 等上下文字段

## 循环机制

四层反馈循环，详见 `docs/feedback-loop.md`：
- **L1 工具级**：保存后自动 lint/format；危险命令拦截
- **L2 任务级**：写测试 → 实现 → 测试通过 → DoD 核查 → 标记完成
- **L3 会话级**：Stop Hook 自动运行 lint + 类型检查 + DoD 提醒
- **L4 阶段级**：阶段完成后创建回顾文档

## 任务追踪

- 所有开发任务在 `docs/task-list.md` 中登记
- Agent 开始工作前必须查阅任务清单，确认当前任务
- 完成任务后更新状态为 `[x]`，同步更新 `CHANGELOG.md`

## Hooks 自动化

详见 `.claude/settings.json`：
- 保存 Java 文件后自动运行 google-java-format
- 保存 Vue/TS 文件后自动运行 prettier + eslint
- 会话结束时运行 lint + 类型检查质量门禁
