# 系统架构设计

## 1. 整体架构

### 1.1 架构风格

采用**分层架构 + 模块化设计**：

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
                          │ HTTP/REST API
┌─────────────────────────┴───────────────────────────────┐
│                  后端 (Spring Boot)                       │
│  ┌────────────────────────────────────────────────────┐  │
│  │     JWT Authentication Filter (认证 + 权限校验)     │  │
│  └────────────────────────────────────────────────────┘  │
│                          ↕                                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐  │
│  │ Attend.  │  │  Leave   │  │ MakeUp   │  │ Report  │  │
│  │ Ctrl     │  │  Ctrl    │  │  Ctrl    │  │  Ctrl   │  │
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
                          │ JDBC
┌─────────────────────────┴───────────────────────────────┐
│                   MySQL 8.0                              │
└─────────────────────────────────────────────────────────┘
```

### 1.2 模块划分

| 模块 | 职责 | 核心类 |
|------|------|--------|
| **认证授权** | 用户登录、JWT 生成/验证、权限校验 | `JwtTokenProvider`, `SecurityConfig` |
| **考勤打卡** | 打卡记录、考勤状态计算、查询统计 | `AttendanceService`, `AttendanceController` |
| **请假管理** | 请假申请、审批流程、状态同步 | `LeaveService`, `LeaveController` |
| **补卡管理** | 补卡申请、审批流程、考勤记录更新 | `MakeUpService`, `MakeUpController` |
| **报表统计** | 个人/部门/公司报表、Excel 导出 | `ReportService`, `ReportController` |
| **用户管理** | 用户 CRUD、角色权限管理 | `UserService`, `UserController` |
| **部门管理** | 部门 CRUD、部门树查询 | `DepartmentService`, `DepartmentController` |

---

## 2. 后端架构

### 2.1 分层架构

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
- ✅ Controller → Service → Mapper → Database（允许）
- ❌ Controller → Mapper（禁止，跨层调用）
- ❌ Service → Service（禁止，避免循环依赖）
- ❌ Mapper 含业务逻辑（禁止）

### 2.2 包结构设计

```
com.attendance
├── config/              # 配置类
│   ├── SecurityConfig.java
│   ├── MyBatisConfig.java
│   ├── WebConfig.java
│   └── SwaggerConfig.java
├── controller/          # Controller 层
│   ├── AuthController.java
│   ├── AttendanceController.java
│   ├── LeaveController.java
│   ├── MakeUpController.java
│   ├── ReportController.java
│   ├── UserController.java
│   └── DepartmentController.java
├── service/             # Service 层
│   ├── AuthService.java
│   ├── AttendanceService.java
│   ├── LeaveService.java
│   ├── MakeUpService.java
│   ├── ReportService.java
│   ├── UserService.java
│   └── DepartmentService.java
├── mapper/              # Mapper 层
│   ├── UserMapper.java
│   ├── AttendanceRecordMapper.java
│   ├── LeaveApplicationMapper.java
│   ├── MakeUpApplicationMapper.java
│   └── DepartmentMapper.java
├── entity/              # 实体类
│   ├── User.java
│   ├── AttendanceRecord.java
│   ├── LeaveApplication.java
│   ├── MakeUpApplication.java
│   ├── Department.java
│   └── AttendanceRule.java
├── dto/                 # DTO 类
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── ClockInRequest.java
│   │   ├── LeaveApplyRequest.java
│   │   └── MakeUpApplyRequest.java
│   └── response/
│       ├── LoginResponse.java
│       ├── AttendanceStatsResponse.java
│       └── ReportExportResponse.java
├── common/              # 公共类
│   ├── Result.java
│   ├── PageResult.java
│   ├── BusinessException.java
│   ├── GlobalExceptionHandler.java
│   └── ErrorCode.java
├── security/            # 安全相关
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
├── enums/               # 枚举类
│   ├── UserRole.java
│   ├── AttendanceStatus.java
│   ├── LeaveType.java
│   ├── ApplicationStatus.java
│   └── ClockType.java
└── util/                # 工具类
    ├── DateUtil.java
    ├── ExcelUtil.java
    └── RequestIdUtil.java
```

### 2.3 统一响应格式

```java
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    // 成功响应
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    
    // 错误响应
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "realName": "张三"
  },
  "timestamp": 1713772800000
}
```

### 2.4 异常处理机制

**全局异常处理器**：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(e.getCode())
            .body(Result.error(e.getCode(), e.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(Result.error(400, message));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(500)
            .body(Result.error(500, "系统异常，请联系管理员"));
    }
}
```

**业务异常码**：

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

## 3. 前端架构

### 3.1 组件架构

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

### 3.2 目录结构

```
frontend/src/
├── api/                 # API 请求层
│   ├── auth.ts
│   ├── attendance.ts
│   ├── leave.ts
│   ├── makeup.ts
│   ├── report.ts
│   ├── user.ts
│   └── department.ts
├── components/          # 通用组件
│   ├── Layout.vue
│   ├── Header.vue
│   ├── Sidebar.vue
│   └── common/
│       ├── Pagination.vue
│       ├── DatePicker.vue
│       └── StatusTag.vue
├── views/               # 页面视图
│   ├── Login.vue
│   ├── Dashboard.vue
│   ├── attendance/
│   │   ├── ClockIn.vue
│   │   └── Records.vue
│   ├── leave/
│   │   ├── Apply.vue
│   │   └── MyApplications.vue
│   ├── makeup/
│   │   ├── Apply.vue
│   │   └── MyApplications.vue
│   ├── report/
│   │   ├── Personal.vue
│   │   └── Department.vue
│   └── admin/
│       ├── Users.vue
│       └── Departments.vue
├── stores/              # Pinia 状态管理
│   ├── user.ts
│   ├── attendance.ts
│   └── index.ts
├── router/              # 路由配置
│   ├── index.ts
│   └── guards.ts        # 路由守卫
├── types/               # TypeScript 类型定义
│   ├── user.ts
│   ├── attendance.ts
│   ├── leave.ts
│   ├── api.ts
│   └── index.ts
├── utils/               # 工具函数
│   ├── request.ts       # Axios 封装
│   ├── auth.ts          # 认证工具
│   ├── date.ts          # 日期工具
│   └── storage.ts       # 本地存储
├── assets/              # 静态资源
│   ├── images/
│   └── styles/
│       └── global.css
├── App.vue
└── main.ts
```

### 3.3 API 请求封装

```typescript
// utils/request.ts
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default request
```

---

## 4. 数据库架构

### 4.1 表关系图

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

### 4.2 索引设计原则

1. **主键索引**：所有表使用自增 BIGINT 主键
2. **唯一索引**：业务唯一键（如 username, email）
3. **复合索引**：高频查询字段组合（如 user_id + status）
4. **外键索引**：关联字段自动创建索引

---

## 5. 安全架构

### 5.1 认证流程

```
客户端登录请求
  ↓
验证用户名密码
  ↓
生成 JWT Token (Access + Refresh)
  ↓
返回 Token 给客户端
  ↓
客户端后续请求携带 Token
  ↓
JWT 过滤器验证 Token
  ↓
放行或拒绝请求
```

### 5.2 权限控制

**RBAC 模型**：

```
User → Role → Permission
```

**角色定义**：

| 角色 | 权限 |
|------|------|
| EMPLOYEE | 打卡、查看个人考勤、申请请假/补卡 |
| MANAGER | EMPLOYEE 权限 + 审批下属申请、查看部门考勤 |
| HR | 查看全公司报表、管理考勤规则 |
| ADMIN | 所有权限 + 用户/部门管理 |

**接口权限注解**：

```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/api/users")
public Result createUser(@RequestBody UserRequest request) {
    // ...
}
```

---

## 6. 部署架构

### 6.1 Docker 容器编排

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql

  backend:
    build: ./backend
    ports:
      - "8080:8080"
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

### 6.2 端口规划

| 服务 | 端口 | 说明 |
|------|------|------|
| MySQL | 3306 | 数据库 |
| Backend | 8080 | Spring Boot API |
| Frontend | 80 | Nginx 静态资源 |
| Frontend Dev | 5173 | Vite 开发服务器 |

---

## 7. 性能优化

### 7.1 数据库优化

1. **索引优化**：高频查询字段建立索引
2. **分页查询**：大列表使用 LIMIT/OFFSET
3. **连接池**：HikariCP 配置（最大连接数 20）
4. **慢查询日志**：记录 > 1s 的查询

### 7.2 前端优化

1. **路由懒加载**：按模块拆分 chunk
2. **组件按需加载**：Element Plus 按需引入
3. **图片优化**：使用 WebP 格式
4. **缓存策略**：静态资源缓存 1 年

---

## 8. 可观测性

### 8.1 日志规范

- **请求日志**：记录 request_id、user_id、接口路径、响应时间
- **业务日志**：关键操作记录（打卡、审批等）
- **错误日志**：异常堆栈、上下文信息

### 8.2 监控指标

| 指标 | 说明 |
|------|------|
| QPS | 每秒请求数 |
| 响应时间 | P50/P95/P99 |
| 错误率 | 4xx/5xx 比例 |
| 数据库连接数 | 活跃/空闲连接 |

---

**最后更新**: 2026-04-22  
**版本**: v1.0.0
