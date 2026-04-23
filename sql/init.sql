-- ========================================
-- 考勤系统数据库初始化脚本
-- 数据库: attendance_db
-- 版本: v1.0.0
-- 日期: 2026-04-22
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS attendance_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE attendance_db;

-- ========================================
-- 1. 部门表
-- ========================================
CREATE TABLE `department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门 ID',
  `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
  `manager_id` BIGINT DEFAULT NULL COMMENT '部门主管 ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父部门 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_manager` (`manager_id`),
  KEY `idx_parent` (`parent_id`),
  CONSTRAINT `fk_department_parent` FOREIGN KEY (`parent_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ========================================
-- 2. 用户表
-- ========================================
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

-- 更新部门表的外键约束（因为 user 表已创建）
ALTER TABLE `department`
  ADD CONSTRAINT `fk_department_manager` FOREIGN KEY (`manager_id`) REFERENCES `user` (`id`);

-- ========================================
-- 3. 考勤规则表
-- ========================================
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

-- ========================================
-- 4. 考勤记录表
-- ========================================
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

-- ========================================
-- 5. 请假申请表
-- ========================================
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

-- ========================================
-- 6. 补卡申请表
-- ========================================
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

-- ========================================
-- 初始化数据
-- ========================================

-- 1. 插入全局考勤规则
INSERT INTO `attendance_rule` (`department_id`, `work_start_time`, `work_end_time`, `late_threshold`, `early_leave_threshold`)
VALUES (NULL, '09:00:00', '18:00:00', 30, 30);

-- 2. 插入部门数据
INSERT INTO `department` (`id`, `name`, `manager_id`, `parent_id`) VALUES
(1, '技术部', NULL, NULL),
(2, '前端组', NULL, 1),
(3, '后端组', NULL, 1),
(4, '人事部', NULL, NULL),
(5, '财务部', NULL, NULL);

-- 3. 插入用户数据（密码均为 BCrypt 加密后的 "admin123"）
-- BCrypt 密码: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYILp91S.0i
INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `email`, `phone`, `department_id`, `role`, `status`) VALUES
(1, 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYILp91S.0i', '系统管理员', 'admin@example.com', '13800138000', 1, 'ADMIN', 1),
(2, 'zhangsan', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYILp91S.0i', '张三', 'zhangsan@example.com', '13800138001', 2, 'MANAGER', 1),
(3, 'lisi', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYILp91S.0i', '李四', 'lisi@example.com', '13800138002', 3, 'MANAGER', 1),
(4, 'wangwu', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYILp91S.0i', '王五', 'wangwu@example.com', '13800138003', 2, 'EMPLOYEE', 1),
(5, 'zhaoliu', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYILp91S.0i', '赵六', 'zhaoliu@example.com', '13800138004', 3, 'EMPLOYEE', 1),
(6, 'hr01', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYILp91S.0i', '人事专员', 'hr@example.com', '13800138005', 4, 'HR', 1);

-- 4. 更新部门主管
UPDATE `department` SET `manager_id` = 2 WHERE `id` = 2;
UPDATE `department` SET `manager_id` = 3 WHERE `id` = 3;

-- ========================================
-- 验证数据
-- ========================================
SELECT '数据库初始化完成！' AS message;
SELECT COUNT(*) AS department_count FROM department;
SELECT COUNT(*) AS user_count FROM user;
SELECT COUNT(*) AS rule_count FROM attendance_rule;
