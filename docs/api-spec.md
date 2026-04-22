# API 接口规范

> **版本**: v1  
> **基础路径**: `/api/v1`  
> **认证方式**: JWT Bearer Token

---

## 1. 通用规范

### 1.1 请求格式

- **Content-Type**: `application/json`
- **字符编码**: UTF-8
- **日期格式**: ISO 8601 (`YYYY-MM-DDTHH:mm:ss`)

### 1.2 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1713772800000
}
```

### 1.3 分页格式

**请求参数**：
- `page`: 页码（默认 1）
- `size`: 每页大小（默认 20）

**响应格式**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "size": 20,
    "pages": 5
  },
  "timestamp": 1713772800000
}
```

### 1.4 错误响应

```json
{
  "code": 401,
  "message": "未授权，请先登录",
  "data": null,
  "timestamp": 1713772800000
}
```

---

## 2. 认证授权 API

### 2.1 用户登录

**POST** `/api/auth/login`

**请求体**：
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "role": "ADMIN"
    }
  },
  "timestamp": 1713772800000
}
```

### 2.2 用户登出

**POST** `/api/auth/logout`

**请求头**：
```
Authorization: Bearer <token>
```

**响应**：
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1713772800000
}
```

### 2.3 获取当前用户信息

**GET** `/api/auth/profile`

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "email": "admin@example.com",
    "phone": "13800138000",
    "departmentId": 1,
    "departmentName": "技术部",
    "role": "ADMIN",
    "status": 1
  },
  "timestamp": 1713772800000
}
```

### 2.4 更新个人信息

**PUT** `/api/auth/profile`

**请求体**：
```json
{
  "realName": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138001"
}
```

### 2.5 修改密码

**PUT** `/api/auth/password`

**请求体**：
```json
{
  "oldPassword": "old123",
  "newPassword": "new456"
}
```

---

## 3. 考勤打卡 API

### 3.1 上班打卡

**POST** `/api/attendance/clock-in`

**权限**: EMPLOYEE+

**响应**：
```json
{
  "code": 200,
  "message": "打卡成功",
  "data": {
    "id": 1,
    "userId": 1,
    "attendanceDate": "2026-04-22",
    "clockInTime": "2026-04-22T09:00:00",
    "status": "NORMAL"
  },
  "timestamp": 1713772800000
}
```

### 3.2 下班打卡

**POST** `/api/attendance/clock-out`

**权限**: EMPLOYEE+

**响应**：
```json
{
  "code": 200,
  "message": "打卡成功",
  "data": {
    "id": 1,
    "userId": 1,
    "attendanceDate": "2026-04-22",
    "clockInTime": "2026-04-22T09:00:00",
    "clockOutTime": "2026-04-22T18:00:00",
    "status": "NORMAL",
    "workHours": 8.0
  },
  "timestamp": 1713772800000
}
```

### 3.3 查询今日考勤

**GET** `/api/attendance/today`

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "attendanceDate": "2026-04-22",
    "clockInTime": "2026-04-22T09:00:00",
    "clockOutTime": null,
    "status": "LATE"
  },
  "timestamp": 1713772800000
}
```

### 3.4 查询考勤记录（分页）

**GET** `/api/attendance/records`

**查询参数**：
- `userId` (可选，HR/MANAGER 可查他人)
- `startDate` (必填，格式: YYYY-MM-DD)
- `endDate` (必填，格式: YYYY-MM-DD)
- `status` (可选: NORMAL/LATE/EARLY_LEAVE/ABSENT)
- `page` (默认 1)
- `size` (默认 20)

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "realName": "张三",
        "attendanceDate": "2026-04-22",
        "clockInTime": "2026-04-22T09:00:00",
        "clockOutTime": "2026-04-22T18:00:00",
        "status": "NORMAL",
        "workHours": 8.0
      }
    ],
    "total": 100,
    "page": 1,
    "size": 20,
    "pages": 5
  },
  "timestamp": 1713772800000
}
```

### 3.5 月度考勤统计

**GET** `/api/attendance/stats/monthly`

**查询参数**：
- `month` (格式: YYYY-MM)

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "month": "2026-04",
    "workDays": 22,
    "actualDays": 20,
    "normalDays": 18,
    "lateCount": 2,
    "earlyLeaveCount": 1,
    "absentCount": 1,
    "leaveDays": 1,
    "totalWorkHours": 160.0
  },
  "timestamp": 1713772800000
}
```

---

## 4. 请假管理 API

### 4.1 提交请假申请

**POST** `/api/leave/applications`

**请求体**：
```json
{
  "leaveType": "ANNUAL",
  "startDate": "2026-04-25T09:00:00",
  "endDate": "2026-04-26T18:00:00",
  "duration": 2.0,
  "reason": "个人事务"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "申请提交成功",
  "data": {
    "id": 1,
    "userId": 1,
    "leaveType": "ANNUAL",
    "startDate": "2026-04-25T09:00:00",
    "endDate": "2026-04-26T18:00:00",
    "duration": 2.0,
    "reason": "个人事务",
    "status": "PENDING"
  },
  "timestamp": 1713772800000
}
```

### 4.2 查询我的申请（分页）

**GET** `/api/leave/applications`

**查询参数**：
- `status` (可选: PENDING/APPROVED/REJECTED/CANCELLED)
- `page` (默认 1)
- `size` (默认 20)

### 4.3 查询申请详情

**GET** `/api/leave/applications/{id}`

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "realName": "张三",
    "leaveType": "ANNUAL",
    "startDate": "2026-04-25T09:00:00",
    "endDate": "2026-04-26T18:00:00",
    "duration": 2.0,
    "reason": "个人事务",
    "status": "APPROVED",
    "approverId": 2,
    "approverName": "李四",
    "approvalComment": "同意",
    "approvedAt": "2026-04-23T10:00:00"
  },
  "timestamp": 1713772800000
}
```

### 4.4 取消申请

**PUT** `/api/leave/applications/{id}/cancel`

**权限**: EMPLOYEE（仅 PENDING 状态）

### 4.5 待审批列表

**GET** `/api/leave/applications/pending`

**权限**: MANAGER+

### 4.6 审批通过

**PUT** `/api/leave/applications/{id}/approve`

**请求体**：
```json
{
  "comment": "同意"
}
```

### 4.7 审批拒绝

**PUT** `/api/leave/applications/{id}/reject`

**请求体**：
```json
{
  "comment": "请假时间冲突，请调整"
}
```

---

## 5. 补卡申请 API

### 5.1 提交补卡申请

**POST** `/api/makeup/applications`

**请求体**：
```json
{
  "attendanceDate": "2026-04-22",
  "clockType": "CLOCK_IN",
  "clockTime": "2026-04-22T09:00:00",
  "reason": "忘记打卡"
}
```

### 5.2 查询我的申请（分页）

**GET** `/api/makeup/applications`

### 5.3 查询申请详情

**GET** `/api/makeup/applications/{id}`

### 5.4 待审批列表

**GET** `/api/makeup/applications/pending`

**权限**: MANAGER+

### 5.5 审批通过

**PUT** `/api/makeup/applications/{id}/approve`

### 5.6 审批拒绝

**PUT** `/api/makeup/applications/{id}/reject`

---

## 6. 考勤报表 API

### 6.1 个人考勤报表

**GET** `/api/reports/personal`

**查询参数**：
- `month` (格式: YYYY-MM)

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "realName": "张三",
    "month": "2026-04",
    "statistics": {
      "workDays": 22,
      "actualDays": 20,
      "normalDays": 18,
      "lateCount": 2,
      "earlyLeaveCount": 1,
      "absentCount": 1,
      "leaveDays": 1
    },
    "records": [
      {
        "date": "2026-04-22",
        "clockInTime": "09:00",
        "clockOutTime": "18:00",
        "status": "NORMAL",
        "workHours": 8.0
      }
    ]
  },
  "timestamp": 1713772800000
}
```

### 6.2 部门考勤报表

**GET** `/api/reports/department`

**权限**: MANAGER+

**查询参数**：
- `departmentId` (可选)
- `month` (格式: YYYY-MM)

### 6.3 全公司考勤报表

**GET** `/api/reports/company`

**权限**: HR/ADMIN

### 6.4 导出 Excel 报表

**GET** `/api/reports/export`

**权限**: HR/ADMIN

**查询参数**：
- `departmentId` (可选)
- `month` (格式: YYYY-MM)
- `format` (xlsx/csv)

**响应**: Excel 文件下载

---

## 7. 用户管理 API（ADMIN）

### 7.1 查询用户列表（分页）

**GET** `/api/users`

**查询参数**：
- `username` (可选，模糊查询)
- `departmentId` (可选)
- `role` (可选)
- `status` (可选)
- `page` (默认 1)
- `size` (默认 20)

### 7.2 创建用户

**POST** `/api/users`

**请求体**：
```json
{
  "username": "zhangsan",
  "password": "zhangsan123",
  "realName": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "departmentId": 1,
  "role": "EMPLOYEE"
}
```

### 7.3 更新用户信息

**PUT** `/api/users/{id}`

### 7.4 启用/禁用用户

**PUT** `/api/users/{id}/status`

**请求体**：
```json
{
  "status": 0
}
```

---

## 8. 部门管理 API（ADMIN）

### 8.1 查询部门树

**GET** `/api/departments`

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "技术部",
      "managerId": 1,
      "managerName": "李四",
      "parentId": null,
      "children": [
        {
          "id": 2,
          "name": "前端组",
          "managerId": 3,
          "managerName": "王五",
          "parentId": 1,
          "children": []
        }
      ]
    }
  ],
  "timestamp": 1713772800000
}
```

### 8.2 创建部门

**POST** `/api/departments`

**请求体**：
```json
{
  "name": "后端组",
  "managerId": 4,
  "parentId": 1
}
```

### 8.3 更新部门信息

**PUT** `/api/departments/{id}`

---

## 9. 考勤规则 API（HR/ADMIN）

### 9.1 查询考勤规则

**GET** `/api/rules`

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "departmentId": null,
      "departmentName": "全局",
      "workStartTime": "09:00",
      "workEndTime": "18:00",
      "lateThreshold": 30,
      "earlyLeaveThreshold": 30
    }
  ],
  "timestamp": 1713772800000
}
```

### 9.2 创建考勤规则

**POST** `/api/rules`

**请求体**：
```json
{
  "departmentId": 1,
  "workStartTime": "09:30",
  "workEndTime": "18:30",
  "lateThreshold": 30,
  "earlyLeaveThreshold": 30
}
```

### 9.3 更新考勤规则

**PUT** `/api/rules/{id}`

---

## 10. 枚举值定义

### 10.1 用户角色 (UserRole)

| 值 | 说明 |
|----|------|
| EMPLOYEE | 员工 |
| MANAGER | 部门主管 |
| HR | 人事 |
| ADMIN | 管理员 |

### 10.2 考勤状态 (AttendanceStatus)

| 值 | 说明 |
|----|------|
| NORMAL | 正常 |
| LATE | 迟到 |
| EARLY_LEAVE | 早退 |
| ABSENT | 缺勤 |

### 10.3 请假类型 (LeaveType)

| 值 | 说明 |
|----|------|
| ANNUAL | 年假 |
| SICK | 病假 |
| PERSONAL | 事假 |
| MARRIAGE | 婚假 |
| MATERNITY | 产假 |

### 10.4 申请状态 (ApplicationStatus)

| 值 | 说明 |
|----|------|
| PENDING | 待审批 |
| APPROVED | 已通过 |
| REJECTED | 已拒绝 |
| CANCELLED | 已取消 |

### 10.5 打卡类型 (ClockType)

| 值 | 说明 |
|----|------|
| CLOCK_IN | 上班打卡 |
| CLOCK_OUT | 下班打卡 |

---

## 11. 错误码定义

| 错误码 | HTTP 状态码 | 说明 |
|--------|------------|------|
| 200 | 200 | 成功 |
| 400 | 400 | 参数错误 |
| 401 | 401 | 未授权 |
| 403 | 403 | 权限不足 |
| 404 | 404 | 资源不存在 |
| 4001 | 400 | 今日已打卡 |
| 4002 | 400 | 不在考勤时间范围 |
| 4003 | 400 | 请假时间冲突 |
| 4004 | 400 | 补卡超过限制次数 |
| 500 | 500 | 系统内部错误 |

---

**最后更新**: 2026-04-22  
**版本**: v1.0.0
