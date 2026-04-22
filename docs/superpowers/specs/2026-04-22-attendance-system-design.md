# 考勤系统设计文档 (Attendance System Design)

> **项目名称**: harness-engineering-attendance-system  
> **创建日期**: 2026-04-22  
> **状态**: 设计完成，待实现  
> **版本**: v1.0.0

---

## 1. 项目概述

### 1.1 项目目标

基于 Harness Engineering 规范，构建一个多角色、模块化的企业考勤系统，支持打卡管理、请假审批、补卡申请和考勤报表统计。

### 1.2 核心用户角色

| 角色 | 权限 |
|------|------|
| **员工 (EMPLOYEE)** | 打卡、查看个人考勤、申请请假/补卡 |
| **部门主管 (MANAGER)** | 审批下属的请假/补卡申请、查看部门考勤 |
| **HR** | 管理考勤规则、查看全公司报表、导出报表 |
| **管理员 (ADMIN)** | 用户管理、部门管理、系统配置 |

### 1.3 核心功能模块（MVP）

1. **基础打卡** - 上班/下班打卡、打卡记录查询
2. **请假管理** - 请假申请、审批流程、请假类型管理
3. **补卡申请** - 忘记打卡时申请补卡、审批流程
4. **考勤报表** - 个人/部门考勤统计、月度报表导出

---

## 2. 技术栈

### 2.1 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.4+ | 前端框架 |
| TypeScript | 5.3+ | 类型安全 |
| Vite | 5.0+ | 构建工具 |
| Element Plus | 2.5+ | UI 组件库 |
| Pinia | 2.1+ | 状态管理 |
| Vue Router | 4.2+ | 路由管理 |
| Axios | 1.6+ | HTTP 客户端 |
| ECharts | 5.4+ | 图表库（报表） |

### 2.2 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2+ | 后端框架 |
| Java | 17+ | 编程语言 |
| Spring Security | - | 安全框架 |
| JWT (jjwt) | 0.12+ | Token 认证 |
| MyBatis-Plus | 3.5+ | 持久层框架 |
| MySQL | 8.0 | 关系数据库 |
| Maven | 3.9+ | 构建工具 |
| Lombok | 1.18+ | 代码简化 |
| Swagger (SpringDoc) | 2.3+ | API 文档 |

### 2.3 基础设施

| 技术 | 用途 |
|------|------|
| Docker | 容器化 |
| docker-compose | 多容器编排 |
| Nginx | 前端静态资源服务 + 反向代理 |

---

## 3. 系统架构

### 3.1 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                      前端 (Vue 3)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │ 打卡页面  │  │ 请假申请  │  │ 补卡申请  │  │ 报表页面 │ │
│  └──────────┘  └──────────┘  └──────────┘  └─────────┘ │
│         ↕            ↕            ↕             ↕       │
│  ┌──────────────────────────────────────────────────┐   │
│  │        API 请求层 (Axios) + Pinia 状态管理        │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTP/REST API (端口 80)
┌─────────────────────────┴───────────────────────────────┐
│                   Nginx (反向代理)                        │
│         前端静态资源 + API 路由转发                        │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTP (端口 8080)
┌─────────────────────────┴───────────────────────────────┐
│                  后端 (Spring Boot)                       │
│  ┌────────────────────────────────────────────────────┐  │
│  │     JWT Authentication Filter (认证 + 权限校验)     │  │
│  └────────────────────────────────────────────────────┘  │
│                          ↕                                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐  │
│  │ Attendance│  │  Leave   │  │ MakeUp   │  │ Report  │  │
│  │ Controller│  │Controller│  │Controller│  │Controller│  │
│  └──────────┘  └──────────┘  └──────────┘  └─────────┘  │
│         ↕            ↕            ↕             ↕        │
│  ┌──────────────────────────────────────────────────┐    │
│  │           Service Layer (业务逻辑层)               │    │
│  └──────────────────────────────────────────────────┘    │
│                          ↕                                │
│  ┌──────────────────────────────────────────────────┐    │
│  │     Mapper Layer (数据访问层 - MyBatis-Plus)       │    │
│  └──────────────────────────────────────────────────┘    │
└─────────────────────────┬───────────────────────────────┘
                          │ JDBC (端口 3306)
┌─────────────────────────┴───────────────────────────────┐
│                   MySQL 8.0                              │
└─────────────────────────────────────────────────────────┘
```

### 3.2 后端分层架构

```
Controller 层
  ↓ (接收请求、参数校验、返回响应)
Service 层
  ↓ (业务逻辑、事务管理)
Mapper 层 (MyBatis-Plus)
  ↓ (SQL 执行)
Database (MySQL)
```

**分层约束**：
- Controller 只能调用 Service，禁止直接调用 Mapper
- Service 可以调用多个 Mapper，但不能调用其他 Service（避免循环依赖）
- Mapper 只负责数据访问，不含业务逻辑

### 3.3 前端架构

```
Views (页面组件)
  ↓
Components (通用组件)
  ↓
Stores (Pinia 状态管理)
  ↓
API (Axios 封装)
  ↓
Backend API
```

---

## 4. 数据模型设计

### 4.1 实体关系图

```
User (用户)
  ├─ 1:N → AttendanceRecord (考勤记录)
  ├─ 1:N → LeaveApplication (请假申请)
  ├─ 1:N → MakeUpApplication (补卡申请)
  └─ N:1 → Department (部门)

Department (部门)
  └─ 1:N → User (用户)

AttendanceRule (考勤规则)
  └─ N:1 → Department (部门，可为 NULL 表示全局规则)
```

### 4.2 数据表详细设计

#### 4.2.1 User（用户表）

```sql
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `department_id` BIGINT DEFAULT NULL COMMENT '所属部门 ID',
  `role` ENUM('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN') NOT NULL DEFAULT 'EMPLOYEE' COMMENT '角色',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_department` (`department_id`),
  CONSTRAINT `fk_user_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

#### 4.2.2 Department（部门表）

```sql
CREATE TABLE `department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门 ID',
  `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
  `manager_id` BIGINT DEFAULT NULL COMMENT '部门主管 ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父部门 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_manager` (`manager_id`),
  KEY `idx_parent` (`parent_id`),
  CONSTRAINT `fk_department_manager` FOREIGN KEY (`manager_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_department_parent` FOREIGN KEY (`parent_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';
```

#### 4.2.3 AttendanceRecord（考勤记录表）

```sql
CREATE TABLE `attendance_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `attendance_date` DATE NOT NULL COMMENT '考勤日期',
  `clock_in_time` DATETIME DEFAULT NULL COMMENT '上班打卡时间',
  `clock_out_time` DATETIME DEFAULT NULL COMMENT '下班打卡时间',
  `status` ENUM('NORMAL', 'LATE', 'EARLY_LEAVE', 'ABSENT') NOT NULL DEFAULT 'NORMAL' COMMENT '状态',
  `work_hours` DECIMAL(4,2) DEFAULT NULL COMMENT '工作时长（小时）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `attendance_date`),
  KEY `idx_date_status` (`attendance_date`, `status`),
  CONSTRAINT `fk_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';
```

**索引设计说明**：
- `uk_user_date` 唯一索引：确保用户每天只有一条考勤记录
- `idx_date_status` 复合索引：优化按日期统计考勤状态的查询（WHERE 条件下推）

#### 4.2.4 LeaveApplication（请假申请表）

```sql
CREATE TABLE `leave_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请 ID',
  `user_id` BIGINT NOT NULL COMMENT '申请人 ID',
  `leave_type` ENUM('ANNUAL', 'SICK', 'PERSONAL', 'MARRIAGE', 'MATERNITY') NOT NULL COMMENT '请假类型',
  `start_date` DATETIME NOT NULL COMMENT '开始时间',
  `end_date` DATETIME NOT NULL COMMENT '结束时间',
  `duration` DECIMAL(5,2) NOT NULL COMMENT '请假时长（天）',
  `reason` TEXT NOT NULL COMMENT '请假原因',
  `status` ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `approver_id` BIGINT DEFAULT NULL COMMENT '审批人 ID',
  `approval_comment` TEXT DEFAULT NULL COMMENT '审批意见',
  `approved_at` DATETIME DEFAULT NULL COMMENT '审批时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`, `status`),
  KEY `idx_approver_status` (`approver_id`, `status`),
  CONSTRAINT `fk_leave_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_leave_approver` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';
```

#### 4.2.5 MakeUpApplication（补卡申请表）

```sql
CREATE TABLE `makeup_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请 ID',
  `user_id` BIGINT NOT NULL COMMENT '申请人 ID',
  `attendance_date` DATE NOT NULL COMMENT '需补卡的日期',
  `clock_type` ENUM('CLOCK_IN', 'CLOCK_OUT') NOT NULL COMMENT '打卡类型',
  `clock_time` DATETIME NOT NULL COMMENT '补卡时间',
  `reason` TEXT NOT NULL COMMENT '补卡原因',
  `status` ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `approver_id` BIGINT DEFAULT NULL COMMENT '审批人 ID',
  `approval_comment` TEXT DEFAULT NULL COMMENT '审批意见',
  `approved_at` DATETIME DEFAULT NULL COMMENT '审批时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`, `status`),
  KEY `idx_approver_status` (`approver_id`, `status`),
  CONSTRAINT `fk_makeup_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_makeup_approver` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补卡申请表';
```

#### 4.2.6 AttendanceRule（考勤规则表）

```sql
CREATE TABLE `attendance_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则 ID',
  `department_id` BIGINT DEFAULT NULL COMMENT '适用部门 ID（NULL=全局）',
  `work_start_time` TIME NOT NULL COMMENT '上班时间',
  `work_end_time` TIME NOT NULL COMMENT '下班时间',
  `late_threshold` INT NOT NULL DEFAULT 30 COMMENT '迟到阈值（分钟）',
  `early_leave_threshold` INT NOT NULL DEFAULT 30 COMMENT '早退阈值（分钟）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_department` (`department_id`),
  CONSTRAINT `fk_rule_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤规则表';
```

### 4.3 设计决策

1. **枚举类型使用 VARCHAR**：提高可读性，便于调试
2. **时间字段统一使用 DATETIME**：避免时区问题，应用层统一处理
3. **软删除策略**：通过 `status` 字段控制，不物理删除数据
4. **审计字段**：所有表包含 `created_at` 和 `updated_at`，自动维护
5. **外键约束**：确保数据一致性，应用层也做校验

---

## 5. API 设计

### 5.1 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1713772800000
}
```

**错误响应**：

```json
{
  "code": 401,
  "message": "未授权，请先登录",
  "data": null,
  "timestamp": 1713772800000
}
```

### 5.2 认证授权 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/login` | 用户登录 | 公开 |
| POST | `/api/auth/logout` | 用户登出 | 已认证 |
| GET | `/api/auth/profile` | 获取当前用户信息 | 已认证 |
| PUT | `/api/auth/profile` | 更新个人信息 | 已认证 |
| PUT | `/api/auth/password` | 修改密码 | 已认证 |

### 5.3 考勤打卡 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/attendance/clock-in` | 上班打卡 | EMPLOYEE+ |
| POST | `/api/attendance/clock-out` | 下班打卡 | EMPLOYEE+ |
| GET | `/api/attendance/today` | 查询今日考勤 | EMPLOYEE+ |
| GET | `/api/attendance/records` | 查询考勤记录（分页） | EMPLOYEE+ |
| GET | `/api/attendance/records/{id}` | 查询单条记录详情 | EMPLOYEE+ |
| GET | `/api/attendance/stats/monthly` | 月度考勤统计 | EMPLOYEE+ |

**查询参数**（`/api/attendance/records`）：
- `userId` (可选，HR/MANAGER 可查他人)
- `startDate` (必填)
- `endDate` (必填)
- `status` (可选)
- `page` (默认 1)
- `size` (默认 20)

### 5.4 请假管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/leave/applications` | 提交请假申请 | EMPLOYEE+ |
| GET | `/api/leave/applications` | 查询我的申请（分页） | EMPLOYEE+ |
| GET | `/api/leave/applications/{id}` | 查询申请详情 | EMPLOYEE+ |
| PUT | `/api/leave/applications/{id}/cancel` | 取消申请 | EMPLOYEE (仅 PENDING 状态) |
| GET | `/api/leave/applications/pending` | 待审批列表 | MANAGER+ |
| PUT | `/api/leave/applications/{id}/approve` | 审批通过 | MANAGER+ |
| PUT | `/api/leave/applications/{id}/reject` | 审批拒绝 | MANAGER+ |

### 5.5 补卡申请 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/makeup/applications` | 提交补卡申请 | EMPLOYEE+ |
| GET | `/api/makeup/applications` | 查询我的申请（分页） | EMPLOYEE+ |
| GET | `/api/makeup/applications/{id}` | 查询申请详情 | EMPLOYEE+ |
| GET | `/api/makeup/applications/pending` | 待审批列表 | MANAGER+ |
| PUT | `/api/makeup/applications/{id}/approve` | 审批通过 | MANAGER+ |
| PUT | `/api/makeup/applications/{id}/reject` | 审批拒绝 | MANAGER+ |

### 5.6 考勤报表 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/reports/personal` | 个人考勤报表 | EMPLOYEE+ |
| GET | `/api/reports/department` | 部门考勤报表 | MANAGER+ |
| GET | `/api/reports/company` | 全公司考勤报表 | HR/ADMIN |
| GET | `/api/reports/export` | 导出 Excel 报表 | HR/ADMIN |

**查询参数**：
- `departmentId` (可选)
- `month` (格式: YYYY-MM)
- `format` (导出格式: xlsx/csv)

### 5.7 用户管理 API（ADMIN）

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/users` | 查询用户列表（分页） | ADMIN |
| POST | `/api/users` | 创建用户 | ADMIN |
| PUT | `/api/users/{id}` | 更新用户信息 | ADMIN |
| PUT | `/api/users/{id}/status` | 启用/禁用用户 | ADMIN |

### 5.8 部门管理 API（ADMIN）

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/departments` | 查询部门树 | ADMIN |
| POST | `/api/departments` | 创建部门 | ADMIN |
| PUT | `/api/departments/{id}` | 更新部门信息 | ADMIN |

### 5.9 考勤规则 API（HR/ADMIN）

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/rules` | 查询考勤规则 | HR/ADMIN |
| POST | `/api/rules` | 创建考勤规则 | ADMIN |
| PUT | `/api/rules/{id}` | 更新考勤规则 | ADMIN |

---

## 6. 核心业务流程

### 6.1 打卡流程

```
用户发起打卡请求
  ↓
JWT 认证 + 权限校验
  ↓
检查今日是否已打卡（根据 clock_type）
  ↓
查询考勤规则（部门规则 or 全局规则）
  ↓
计算打卡状态（NORMAL / LATE / EARLY_LEAVE）
  ↓
写入考勤记录（或更新已有记录）
  ↓
返回打卡结果
```

**关键逻辑**：
- 上班打卡：超过 `work_start_time + late_threshold` 标记为 LATE
- 下班打卡：早于 `work_end_time - early_leave_threshold` 标记为 EARLY_LEAVE
- 工作时长自动计算：`work_hours = (clock_out_time - clock_in_time) - 休息时间`

### 6.2 请假审批流程

```
员工提交请假申请
  ↓
状态设为 PENDING
  ↓
通知部门主管（站内消息或邮件）
  ↓
主管查看待审批列表
  ↓
主管审批（APPROVED / REJECTED）
  ↓
更新申请状态，记录审批人和时间
  ↓
通知申请人审批结果
  ↓
如果通过，同步更新考勤记录（标记为请假）
```

### 6.3 补卡审批流程

```
员工提交补卡申请
  ↓
状态设为 PENDING
  ↓
通知部门主管
  ↓
主管审批
  ↓
如果通过，更新对应日期的考勤记录
  ↓
重新计算考勤状态和工作时长
```

### 6.4 报表统计流程

```
接收报表查询请求（部门、月份）
  ↓
查询该月所有考勤记录
  ↓
关联请假记录（排除请假天数）
  ↓
计算统计数据：
  - 应出勤天数
  - 实际出勤天数
  - 迟到次数
  - 早退次数
  - 缺勤次数
  - 请假天数
  ↓
返回统计结果（或生成 Excel 文件）
```

---

## 7. 安全设计

### 7.1 认证机制

- **JWT Token 认证**：登录后颁发 Token，有效期 24 小时
- **Refresh Token**：有效期 7 天，用于刷新 Access Token
- **Token 存储**：前端存储在 localStorage，后端每次请求校验

### 7.2 权限控制

- **RBAC（基于角色的访问控制）**：通过 `@PreAuthorize` 注解控制接口权限
- **数据权限**：
  - 员工只能查看自己的数据
  - 主管可查看本部门数据
  - HR/ADMIN 可查看全公司数据

### 7.3 安全防护

- **密码加密**：BCrypt 算法，强度因子 12
- **SQL 注入防护**：MyBatis-Plus 参数化查询
- **XSS 防护**：前端输入过滤 + 后端校验
- **CSRF 防护**：JWT 认证天然免疫 CSRF
- **接口限流**：Spring Boot Rate Limiter（登录接口 5 次/分钟）

---

## 8. 错误处理

### 8.1 全局异常处理器

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(e.getCode())
            .body(Result.error(e.getCode(), e.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handleValidationException(...) {
        // 参数校验错误
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleException(Exception e) {
        // 未知错误，记录日志
        log.error("系统异常", e);
        return ResponseEntity.status(500)
            .body(Result.error(500, "系统异常，请联系管理员"));
    }
}
```

### 8.2 业务异常码

| 错误码 | 说明 |
|--------|------|
| 400 | 参数错误 |
| 401 | 未授权（Token 无效或过期） |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 4001 | 今日已打卡，无需重复 |
| 4002 | 不在考勤时间范围内 |
| 4003 | 请假申请时间冲突 |
| 4004 | 补卡申请超过限制次数 |
| 500 | 系统内部错误 |

---

## 9. 测试策略

### 9.1 测试金字塔

```
        /\
       /  \
      / E2E \          端到端测试 (10%)
     /______\
    /        \
   / Integration\     集成测试 (20%)
  /______________\
 /                \
/    Unit Tests    \  单元测试 (70%)
--------------------
```

### 9.2 单元测试

- **框架**：JUnit 5 + Mockito
- **覆盖率目标**：≥ 80%
- **测试范围**：Service 层业务逻辑

**示例**：

```java
@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {
    
    @Mock
    private AttendanceRecordMapper recordMapper;
    
    @InjectMocks
    private AttendanceService attendanceService;
    
    @Test
    void clockIn_shouldCreateRecord_whenFirstClockToday() {
        // Given
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        when(recordMapper.selectByUserIdAndDate(userId, today)).thenReturn(null);
        
        // When
        attendanceService.clockIn(userId);
        
        // Then
        verify(recordMapper).insert(any(AttendanceRecord.class));
    }
}
```

### 9.3 集成测试

- **框架**：Spring Boot Test + TestContainers
- **测试范围**：Controller → Service → Mapper → Database

**示例**：

```java
@SpringBootTest
@AutoConfigureMockMvc
class AttendanceControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void clockIn_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/attendance/clock-in")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
```

### 9.4 前端测试

- **单元测试**：Vitest + Vue Test Utils
- **E2E 测试**：Playwright（核心流程：登录 → 打卡 → 查看报表）

---

## 10. 部署方案

### 10.1 Docker 容器化

**docker-compose.yml**：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: attendance_db
      MYSQL_USER: attendance
      MYSQL_PASSWORD: attendance123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    command: --default-authentication-plugin=mysql_native_password

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/attendance_db
      SPRING_DATASOURCE_USERNAME: attendance
      SPRING_DATASOURCE_PASSWORD: attendance123
      JWT_SECRET: your-secret-key-here
    depends_on:
      - mysql

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

### 10.2 启动命令

```bash
# 一键启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 10.3 环境变量

**后端 (.env)**：

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/attendance_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root123
JWT_SECRET=your-256-bit-secret-key-here
JWT_EXPIRATION=86400000
```

**前端 (.env)**：

```env
VITE_API_BASE_URL=http://localhost:8080
```

---

## 11. Harness Engineering 规范应用

### 11.1 核心规范文件

| 文件 | 用途 |
|------|------|
| `CLAUDE.md` | 项目上下文和技术规范（做什么） |
| `AGENTS.md` | Agent 行为规范和工作流程（怎么做） |
| `docs/task-list.md` | 任务清单和进度追踪 |
| `docs/definition-of-done.md` | 完成标准（DoD） |
| `CHANGELOG.md` | 变更日志 |

### 11.2 质量门禁

**L1 - 工具级（自动）**：
- Java 文件保存后自动格式化（google-java-format）
- Vue/TS 文件保存后自动 lint（ESLint + Prettier）

**L2 - 任务级（手动触发）**：
```bash
# 后端
mvn clean test                    # 运行所有测试
mvn checkstyle:check              # 代码规范检查
mvn jacoco:report                 # 生成覆盖率报告

# 前端
npm run lint                      # ESLint 检查
npm run type-check                # TypeScript 类型检查
npm run test                      # 运行测试
```

**L3 - 会话级（自动）**：
- 会话结束时自动运行 lint + 类型检查
- 提醒核对 DoD 清单

**L4 - 阶段级（手动）**：
- 每个阶段完成后创建回顾文档
- 更新 CHANGELOG.md

### 11.3 TDD 工作流（强制）

所有涉及代码变更的任务必须遵循 TDD：

```
1. [RED]   写测试 → 运行测试（必须失败）
2. [GREEN] 写实现 → 运行测试（必须通过）
3. [REFACTOR] 重构 → 运行测试（必须通过）
4. [REVIEW] Code Review → 修复问题
5. [DOD] 完成标准核查 → 标记完成
```

### 11.4 禁止操作清单

- ❌ 跳过测试（"先实现，后补测试"）
- ❌ 跳过 Code Review
- ❌ 跳过 DoD 核查
- ❌ 修改测试以通过构建（应该修复实现）
- ❌ 硬编码敏感信息（密码、密钥等）
- ❌ Controller 直接调用 Mapper（违反分层架构）
- ❌ 直接使用 System.out.println（应该用 Logger）

---

## 12. 项目结构

### 12.1 后端目录结构

```
harness-engineering-attendance-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/attendance/
│   │   │   │   ├── AttendanceApplication.java
│   │   │   │   ├── config/              # 配置类
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── MyBatisConfig.java
│   │   │   │   │   └── WebConfig.java
│   │   │   │   ├── controller/          # Controller 层
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── AttendanceController.java
│   │   │   │   │   ├── LeaveController.java
│   │   │   │   │   ├── MakeUpController.java
│   │   │   │   │   ├── ReportController.java
│   │   │   │   │   ├── UserController.java
│   │   │   │   │   └── DepartmentController.java
│   │   │   │   ├── service/             # Service 层
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── AttendanceService.java
│   │   │   │   │   ├── LeaveService.java
│   │   │   │   │   ├── MakeUpService.java
│   │   │   │   │   ├── ReportService.java
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   └── DepartmentService.java
│   │   │   │   ├── mapper/              # Mapper 层
│   │   │   │   │   ├── UserMapper.java
│   │   │   │   │   ├── AttendanceRecordMapper.java
│   │   │   │   │   ├── LeaveApplicationMapper.java
│   │   │   │   │   ├── MakeUpApplicationMapper.java
│   │   │   │   │   └── DepartmentMapper.java
│   │   │   │   ├── entity/              # 实体类
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── AttendanceRecord.java
│   │   │   │   │   ├── LeaveApplication.java
│   │   │   │   │   ├── MakeUpApplication.java
│   │   │   │   │   └── Department.java
│   │   │   │   ├── dto/                 # DTO 类
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   ├── common/              # 公共类
│   │   │   │   │   ├── Result.java
│   │   │   │   │   ├── BusinessException.java
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── security/            # 安全相关
│   │   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   │   └── util/                # 工具类
│   │   │   │       └── DateUtil.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       ├── application-prod.yml
│   │   │       └── mapper/              # MyBatis XML
│   │   │           ├── AttendanceRecordMapper.xml
│   │   │           └── ...
│   │   └── test/
│   │       └── java/com/attendance/
│   │           ├── service/
│   │           └── controller/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   │   ├── api/                 # API 请求层
│   │   │   ├── auth.ts
│   │   │   ├── attendance.ts
│   │   │   ├── leave.ts
│   │   │   ├── makeup.ts
│   │   │   └── report.ts
│   │   ├── components/          # 通用组件
│   │   │   ├── Layout.vue
│   │   │   ├── Header.vue
│   │   │   └── Sidebar.vue
│   │   ├── views/               # 页面视图
│   │   │   ├── Login.vue
│   │   │   ├── Dashboard.vue
│   │   │   ├── attendance/
│   │   │   │   ├── ClockIn.vue
│   │   │   │   └── Records.vue
│   │   │   ├── leave/
│   │   │   │   ├── Apply.vue
│   │   │   │   └── MyApplications.vue
│   │   │   ├── makeup/
│   │   │   │   ├── Apply.vue
│   │   │   │   └── MyApplications.vue
│   │   │   ├── report/
│   │   │   │   ├── Personal.vue
│   │   │   │   └── Department.vue
│   │   │   └── admin/
│   │   │       ├── Users.vue
│   │   │       └── Departments.vue
│   │   ├── stores/              # Pinia 状态管理
│   │   │   ├── user.ts
│   │   │   └── attendance.ts
│   │   ├── router/              # 路由配置
│   │   │   └── index.ts
│   │   ├── types/               # TypeScript 类型定义
│   │   │   ├── user.ts
│   │   │   ├── attendance.ts
│   │   │   └── api.ts
│   │   ├── utils/               # 工具函数
│   │   │   ├── request.ts
│   │   │   └── auth.ts
│   │   ├── App.vue
│   │   └── main.ts
│   ├── public/
│   ├── index.html
│   ├── vite.config.ts
│   ├── package.json
│   └── Dockerfile
├── sql/
│   └── init.sql                 # 数据库初始化脚本
├── docs/
│   ├── superpowers/
│   │   └── specs/
│   │       └── 2026-04-22-attendance-system-design.md
│   ├── task-list.md
│   └── definition-of-done.md
├── docker-compose.yml
├── CLAUDE.md
├── AGENTS.md
└── README.md
```

---

## 13. 开发计划

### Phase 1: 基础设施搭建（第 1 周）
- [ ] 项目初始化（Spring Boot + Vue 3）
- [ ] 数据库设计和建表
- [ ] 认证授权模块（JWT + RBAC）
- [ ] 用户管理模块
- [ ] 部门管理模块

### Phase 2: 核心功能开发（第 2-3 周）
- [ ] 考勤打卡模块
- [ ] 请假管理模块
- [ ] 补卡申请模块

### Phase 3: 报表和增强（第 4 周）
- [ ] 考勤报表模块
- [ ] Excel 导出功能
- [ ] E2E 测试
- [ ] 性能优化

### Phase 4: 部署和文档（第 5 周）
- [ ] Docker 容器化
- [ ] 部署文档
- [ ] 用户手册
- [ ] API 文档完善

---

## 14. 关键设计决策记录

### 14.1 为什么选择 MyBatis-Plus 而非 JPA？

**决策**：选择 MyBatis-Plus

**理由**：
1. 考勤系统有复杂的多表查询（报表统计），MyBatis 的 SQL 控制更灵活
2. 支持 WHERE 条件下推、UNION 查询优化等高级 SQL 特性
3. 团队对 MyBatis 更熟悉

### 14.2 为什么选择单体架构而非微服务？

**决策**：选择模块化单体架构

**理由**：
1. MVP 阶段避免过度工程（YAGNI 原则）
2. 模块化设计足够支撑当前需求，未来可演进为微服务
3. 降低运维复杂度

### 14.3 为什么前后端分离？

**决策**：前后端完全分离

**理由**：
1. 独立部署、独立扩展
2. 符合 Harness Engineering 的模块化原则
3. 前端可独立开发 Mock 数据

---

## 15. 附录

### 15.1 术语表

| 术语 | 说明 |
|------|------|
| 打卡 | 员工上班/下班时记录考勤时间 |
| 迟到 | 超过规定上班时间 + 迟到阈值 |
| 早退 | 早于规定下班时间 - 早退阈值 |
| 缺勤 | 当日无打卡记录且无请假 |
| 补卡 | 忘记打卡后申请补充记录 |

### 15.2 参考资料

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [Vue 3 官方文档](https://vuejs.org/)
- [Harness Engineering 规范](https://github.com/deusyu/harness-engineering)

---

**文档版本**: v1.0.0  
**创建日期**: 2026-04-22  
**最后更新**: 2026-04-22  
**状态**: 设计完成，待实现
