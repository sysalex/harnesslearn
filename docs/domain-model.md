# 领域模型

> 定义考勤系统的核心业务实体及其关系

---

## 1. 核心实体

### 1.1 User（用户）

**职责**：系统使用者，包含员工、主管、HR、管理员

**属性**：
```java
public class User {
    private Long id;                    // 用户 ID
    private String username;            // 用户名
    private String password;            // BCrypt 加密密码
    private String realName;            // 真实姓名
    private String email;               // 邮箱
    private String phone;               // 手机号
    private Long departmentId;          // 所属部门 ID
    private Department department;      // 所属部门对象
    private UserRole role;              // 角色（EMPLOYEE/MANAGER/HR/ADMIN）
    private Integer status;             // 状态（1=启用，0=禁用）
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}
```

**业务规则**：
- 用户名唯一
- 邮箱唯一
- 密码必须 BCrypt 加密
- 禁用用户无法登录

---

### 1.2 Department（部门）

**职责**：组织架构单元，支持多级部门树

**属性**：
```java
public class Department {
    private Long id;                    // 部门 ID
    private String name;                // 部门名称
    private Long managerId;             // 部门主管 ID
    private User manager;               // 部门主管对象
    private Long parentId;              // 父部门 ID
    private List<Department> children;  // 子部门列表
    private LocalDateTime createdAt;    // 创建时间
}
```

**业务规则**：
- 部门名称在同级下唯一
- 主管必须是该部门员工
- 支持无限级部门树
- 删除部门前需转移员工

---

### 1.3 AttendanceRecord（考勤记录）

**职责**：记录员工每日打卡情况

**属性**：
```java
public class AttendanceRecord {
    private Long id;                    // 记录 ID
    private Long userId;                // 用户 ID
    private User user;                  // 用户对象
    private LocalDate attendanceDate;   // 考勤日期
    private LocalDateTime clockInTime;  // 上班打卡时间
    private LocalDateTime clockOutTime; // 下班打卡时间
    private AttendanceStatus status;    // 状态（NORMAL/LATE/EARLY_LEAVE/ABSENT）
    private BigDecimal workHours;       // 工作时长（小时）
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}
```

**业务规则**：
- 用户每天最多一条记录
- 上班打卡后才能下班打卡
- 考勤状态根据打卡时间自动计算
- 工作时长 = 下班时间 - 上班时间 - 休息时间（1小时）

**状态计算逻辑**：
```
上班时间 <= workStartTime + lateThreshold → NORMAL
上班时间 > workStartTime + lateThreshold → LATE
下班时间 < workEndTime - earlyLeaveThreshold → EARLY_LEAVE
无打卡记录 → ABSENT
```

---

### 1.4 LeaveApplication（请假申请）

**职责**：员工请假申请及审批流程

**属性**：
```java
public class LeaveApplication {
    private Long id;                    // 申请 ID
    private Long userId;                // 申请人 ID
    private User user;                  // 申请人对象
    private LeaveType leaveType;        // 请假类型
    private LocalDateTime startDate;    // 开始时间
    private LocalDateTime endDate;      // 结束时间
    private BigDecimal duration;        // 请假时长（天）
    private String reason;              // 请假原因
    private ApplicationStatus status;   // 状态（PENDING/APPROVED/REJECTED/CANCELLED）
    private Long approverId;            // 审批人 ID
    private User approver;              // 审批人对象
    private String approvalComment;     // 审批意见
    private LocalDateTime approvedAt;   // 审批时间
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}
```

**业务规则**：
- 请假时长 = (结束时间 - 开始时间) / 8小时（工作日）
- 请假时间不能重叠
- 只有 PENDING 状态可取消
- 审批通过后同步更新考勤记录

**请假类型说明**：

| 类型 | 说明 | 是否需要证明 |
|------|------|-------------|
| ANNUAL | 年假 | 否 |
| SICK | 病假 | 是（医院证明） |
| PERSONAL | 事假 | 否 |
| MARRIAGE | 婚假 | 是（结婚证） |
| MATERNITY | 产假 | 是（准生证） |

---

### 1.5 MakeUpApplication（补卡申请）

**职责**：忘记打卡时申请补卡

**属性**：
```java
public class MakeUpApplication {
    private Long id;                    // 申请 ID
    private Long userId;                // 申请人 ID
    private User user;                  // 申请人对象
    private LocalDate attendanceDate;   // 需补卡的日期
    private ClockType clockType;        // 打卡类型（CLOCK_IN/CLOCK_OUT）
    private LocalDateTime clockTime;    // 补卡时间
    private String reason;              // 补卡原因
    private ApplicationStatus status;   // 状态（PENDING/APPROVED/REJECTED）
    private Long approverId;            // 审批人 ID
    private User approver;              // 审批人对象
    private String approvalComment;     // 审批意见
    private LocalDateTime approvedAt;   // 审批时间
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}
```

**业务规则**：
- 每月最多补卡 3 次
- 补卡日期不能是未来日期
- 审批通过后更新对应考勤记录
- 重新计算考勤状态和工作时长

---

### 1.6 AttendanceRule（考勤规则）

**职责**：定义部门或全局的考勤时间规则

**属性**：
```java
public class AttendanceRule {
    private Long id;                    // 规则 ID
    private Long departmentId;          // 适用部门 ID（NULL=全局）
    private Department department;      // 适用部门对象
    private LocalTime workStartTime;    // 上班时间
    private LocalTime workEndTime;      // 下班时间
    private Integer lateThreshold;      // 迟到阈值（分钟）
    private Integer earlyLeaveThreshold; // 早退阈值（分钟）
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}
```

**业务规则**：
- 全局规则只能有一条（departmentId = NULL）
- 部门规则优先于全局规则
- 迟到阈值默认 30 分钟
- 早退阈值默认 30 分钟

**规则匹配逻辑**：
```
1. 查询用户所属部门的规则
2. 如果存在，使用部门规则
3. 如果不存在，使用全局规则
4. 如果都不存在，使用默认规则（09:00-18:00，阈值 30 分钟）
```

---

## 2. 枚举类型

### 2.1 UserRole（用户角色）

```java
public enum UserRole {
    EMPLOYEE("员工"),
    MANAGER("部门主管"),
    HR("人事"),
    ADMIN("管理员");
    
    private final String description;
}
```

### 2.2 AttendanceStatus（考勤状态）

```java
public enum AttendanceStatus {
    NORMAL("正常"),
    LATE("迟到"),
    EARLY_LEAVE("早退"),
    ABSENT("缺勤");
    
    private final String description;
}
```

### 2.3 LeaveType（请假类型）

```java
public enum LeaveType {
    ANNUAL("年假"),
    SICK("病假"),
    PERSONAL("事假"),
    MARRIAGE("婚假"),
    MATERNITY("产假");
    
    private final String description;
}
```

### 2.4 ApplicationStatus（申请状态）

```java
public enum ApplicationStatus {
    PENDING("待审批"),
    APPROVED("已通过"),
    REJECTED("已拒绝"),
    CANCELLED("已取消");
    
    private final String description;
}
```

### 2.5 ClockType（打卡类型）

```java
public enum ClockType {
    CLOCK_IN("上班打卡"),
    CLOCK_OUT("下班打卡");
    
    private final String description;
}
```

---

## 3. 实体关系图

```
User (用户)
  ├─ 1:N → AttendanceRecord (考勤记录)
  ├─ 1:N → LeaveApplication (请假申请)
  ├─ 1:N → MakeUpApplication (补卡申请)
  └─ N:1 → Department (部门)

Department (部门)
  ├─ 1:N → User (用户)
  ├─ 1:1 → User (部门主管)
  ├─ 1:N → Department (子部门)
  └─ 0..1 → AttendanceRule (考勤规则)

AttendanceRule (考勤规则)
  └─ N:1 → Department (部门，可为 NULL 表示全局)
```

---

## 4. 领域服务

### 4.1 AttendanceDomainService

**职责**：考勤相关业务逻辑

**方法**：
- `calculateStatus(clockInTime, clockOutTime, rule)` - 计算考勤状态
- `calculateWorkHours(clockInTime, clockOutTime)` - 计算工作时长
- `checkTimeConflict(userId, startDate, endDate)` - 检查时间冲突

### 4.2 LeaveDomainService

**职责**：请假相关业务逻辑

**方法**：
- `calculateDuration(startDate, endDate)` - 计算请假时长
- `checkOverlap(userId, startDate, endDate)` - 检查请假重叠
- `syncToAttendance(application)` - 同步到考勤记录

### 4.3 MakeUpDomainService

**职责**：补卡相关业务逻辑

**方法**：
- `checkMonthlyLimit(userId, month)` - 检查月度补卡次数
- `updateAttendanceRecord(application)` - 更新考勤记录
- `recalculateStatus(recordId)` - 重新计算考勤状态

---

## 5. 值对象

### 5.1 AttendanceStats（考勤统计）

```java
public class AttendanceStats {
    private Integer workDays;           // 应出勤天数
    private Integer actualDays;         // 实际出勤天数
    private Integer normalDays;         // 正常天数
    private Integer lateCount;          // 迟到次数
    private Integer earlyLeaveCount;    // 早退次数
    private Integer absentCount;        // 缺勤次数
    private Integer leaveDays;          // 请假天数
    private BigDecimal totalWorkHours;  // 总工作时长
}
```

### 5.2 ApprovalInfo（审批信息）

```java
public class ApprovalInfo {
    private Long approverId;            // 审批人 ID
    private String approverName;        // 审批人姓名
    private String comment;             // 审批意见
    private LocalDateTime approvedAt;   // 审批时间
}
```

---

## 6. 聚合根

### 6.1 LeaveApplicationAggregate

**聚合根**：LeaveApplication

**包含实体**：
- LeaveApplication（请假申请）
- ApprovalInfo（审批信息）

**不变量**：
- 请假时间不能重叠
- 只有 PENDING 状态可取消
- 审批后状态不可逆

### 6.2 MakeUpApplicationAggregate

**聚合根**：MakeUpApplication

**包含实体**：
- MakeUpApplication（补卡申请）
- ApprovalInfo（审批信息）
- AttendanceRecord（考勤记录）

**不变量**：
- 每月补卡次数 ≤ 3
- 补卡日期 ≤ 今天
- 审批后更新考勤记录

---

**最后更新**: 2026-04-22  
**版本**: v1.0.0
