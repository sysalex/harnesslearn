# 企业考勤系统（Attendance System）

> 基于 Harness Engineering 规范开发的多角色企业考勤管理系统

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4+-brightgreen.svg)](https://vuejs.org/)

---

## 📋 项目简介

企业级在线考勤系统，支持多角色（员工/主管/HR/管理员）、打卡管理、请假审批、补卡申请和考勤报表统计。

### 核心功能

- ✅ **考勤打卡** - 上班/下班打卡、自动计算考勤状态
- ✅ **请假管理** - 在线申请、审批流程、多种请假类型
- ✅ **补卡申请** - 忘记打卡时申请补卡、审批后自动更新
- ✅ **考勤报表** - 个人/部门/公司报表、Excel 导出
- ✅ **权限管理** - RBAC 角色权限控制
- ✅ **部门管理** - 多级部门树、灵活配置

---

## 🚀 快速开始

### 前置要求

- **Java**: 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Docker**: 20.10+（可选）
- **Maven**: 3.9+

### 方式一：Docker 一键启动（推荐）

```bash
# 1. 克隆项目
git clone <repository-url>
cd harnesslearn

# 2. 启动所有服务
docker-compose up -d

# 3. 查看日志
docker-compose logs -f

# 4. 访问系统
# 前端: http://localhost
# 后端 API: http://localhost:8080
# Swagger 文档: http://localhost:8080/swagger-ui.html
```

### 方式二：本地开发

#### 1. 启动数据库

```bash
# 使用 Docker 启动 MySQL
docker run -d \
  --name attendance-mysql \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=attendance_db \
  -p 3306:3306 \
  -v $(pwd)/sql/init.sql:/docker-entrypoint-initdb.d/init.sql \
  mysql:8.0
```

#### 2. 启动后端

```bash
cd backend

# 安装依赖
mvn clean install

# 启动应用
mvn spring-boot:run

# 访问 Swagger 文档
# http://localhost:8080/swagger-ui.html
```

#### 3. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问前端
# http://localhost:5173
```

---

## 📖 文档导航

### Harness Engineering 规范

| 文档 | 说明 |
|------|------|
| [CLAUDE.md](CLAUDE.md) | 项目核心规范（做什么）- 技术栈、架构约束、编码规范 |
| [AGENTS.md](AGENTS.md) | Agent 行为规范（怎么做）- TDD 流程、质量门禁、协作模式 |
| [docs/task-list.md](docs/task-list.md) | 任务清单 - Phase 1-4 完整任务分解 |
| [docs/definition-of-done.md](docs/definition-of-done.md) | 完成标准 - 代码质量/架构/安全/文档检查项 |
| [docs/feedback-loop.md](docs/feedback-loop.md) | 反馈循环机制 - L1-L4 四层质量门禁 |
| [CHANGELOG.md](CHANGELOG.md) | 变更日志 |

### 技术文档

| 文档 | 说明 |
|------|------|
| [docs/architecture.md](docs/architecture.md) | 系统架构设计 - 分层架构、包结构、部署架构 |
| [docs/api-spec.md](docs/api-spec.md) | API 接口规范 - 40+ 个接口详细定义 |
| [docs/domain-model.md](docs/domain-model.md) | 领域模型 - 核心实体、枚举、聚合根 |
| [docs/adr/](docs/adr/) | 架构决策记录 |

### 数据库

| 文件 | 说明 |
|------|------|
| [sql/init.sql](sql/init.sql) | 数据库初始化脚本（包含示例数据） |

---

## 🏗️ 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2+ | 后端框架 |
| Java | 17+ | 编程语言 |
| MyBatis-Plus | 3.5+ | 持久层框架 |
| Spring Security | - | 安全框架 |
| JWT (jjwt) | 0.12+ | Token 认证 |
| MySQL | 8.0 | 关系数据库 |
| Swagger (SpringDoc) | 2.3+ | API 文档 |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.4+ | 前端框架 |
| TypeScript | 5.3+ | 类型安全 |
| Vite | 5.0+ | 构建工具 |
| Element Plus | 2.5+ | UI 组件库 |
| Pinia | 2.1+ | 状态管理 |
| Axios | 1.6+ | HTTP 客户端 |
| ECharts | 5.4+ | 图表库 |

---

## 👥 用户角色

| 角色 | 权限 |
|------|------|
| **员工 (EMPLOYEE)** | 打卡、查看个人考勤、申请请假/补卡 |
| **部门主管 (MANAGER)** | 审批下属申请、查看部门考勤 |
| **HR** | 管理考勤规则、查看全公司报表、导出报表 |
| **管理员 (ADMIN)** | 用户管理、部门管理、系统配置 |

### 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| zhangsan | admin123 | 部门主管（前端组） |
| lisi | admin123 | 部门主管（后端组） |
| wangwu | admin123 | 员工 |
| hr01 | admin123 | HR |

---

## 📊 项目结构

```
harnesslearn/
├── CLAUDE.md                    # 项目核心规范
├── AGENTS.md                    # Agent 行为规范
├── CHANGELOG.md                 # 变更日志
├── docker-compose.yml           # Docker 编排
├── .gitignore                   # Git 忽略配置
│
├── backend/                     # 后端（Spring Boot）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
├── frontend/                    # 前端（Vue 3）
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── views/
│   │   ├── stores/
│   │   └── router/
│   └── package.json
│
├── sql/                         # 数据库脚本
│   └── init.sql
│
└── docs/                        # 文档
    ├── task-list.md             # 任务清单
    ├── definition-of-done.md    # 完成标准
    ├── architecture.md          # 架构设计
    ├── api-spec.md              # API 规范
    ├── domain-model.md          # 领域模型
    ├── feedback-loop.md         # 反馈循环
    └── adr/                     # 架构决策
```

---

## 🛠️ 开发指南

### TDD 工作流（强制）

所有涉及代码变更的任务必须遵循 TDD：

```
1. [RED]   写测试 → 运行测试（必须失败）
2. [GREEN] 写实现 → 运行测试（必须通过）
3. [REFACTOR] 重构 → 运行测试（必须通过）
4. [REVIEW] Code Review → 修复问题
5. [DOD] 完成标准核查 → 标记完成
```

详见 [AGENTS.md](AGENTS.md)

### 质量门禁

**L1 - 工具级**：文件保存后自动 lint/format  
**L2 - 任务级**：每个任务完成前运行测试 + 代码检查  
**L3 - 会话级**：会话结束时自动运行质量检查  
**L4 - 阶段级**：阶段完成后创建回顾文档

详见 [docs/feedback-loop.md](docs/feedback-loop.md)

### 常用命令

```bash
# 后端
cd backend
mvn clean test                    # 运行测试
mvn checkstyle:check              # 代码规范检查
mvn jacoco:report                 # 生成覆盖率报告
mvn spring-boot:run               # 启动应用

# 前端
cd frontend
npm run dev                       # 启动开发服务器
npm run lint                      # ESLint 检查
npm run type-check                # TypeScript 类型检查
npm run test                      # 运行测试
```

---

## 📈 开发进度

| 阶段 | 状态 | 进度 |
|------|------|------|
| Phase 1: 基础设施搭建 | 🔄 进行中 | 30% |
| Phase 2: 核心功能开发 | ⏳ 未开始 | 0% |
| Phase 3: 报表和增强 | ⏳ 未开始 | 0% |
| Phase 4: 部署和文档 | ⏳ 未开始 | 0% |

详见 [docs/task-list.md](docs/task-list.md)

---

## 🤝 贡献指南

本项目遵循 Harness Engineering 规范，所有贡献必须符合规范要求。

### 提交 PR 前检查清单

- [ ] 所有测试通过
- [ ] 测试覆盖率 ≥ 80%
- [ ] Lint 零错误
- [ ] Code Review 通过
- [ ] 文档已更新
- [ ] CHANGELOG.md 已更新

---

## 📄 许可证

MIT License

---

## 📞 联系方式

如有问题或建议，欢迎提 Issue。

---

**基于 Harness Engineering 规范开发** | [查看规范文档](CLAUDE.md)
