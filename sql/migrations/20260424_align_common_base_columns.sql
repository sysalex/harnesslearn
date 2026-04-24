-- 2026-04-24
-- 对齐通用基类字段命名：
-- id, createdByUserId, createdByUserName, createdTime,
-- updatedByUserId, updatedByUserName, updatedTime, enabledFlag, deletedFlag
-- 数据库列统一使用下划线命名。

ALTER TABLE `department`
    CHANGE COLUMN `created_at` `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN `created_by_user_id` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `parent_id`,
    ADD COLUMN `created_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `created_by_user_id`,
    ADD COLUMN `updated_by_user_id` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `created_time`,
    ADD COLUMN `updated_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updated_by_user_id`,
    ADD COLUMN `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `updated_by_user_name`,
    ADD COLUMN `enabled_flag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updated_time`,
    ADD COLUMN `deleted_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabled_flag`;

ALTER TABLE `user`
    CHANGE COLUMN `status` `enabled_flag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    CHANGE COLUMN `created_at` `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `created_by_user_id` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `role`,
    ADD COLUMN `created_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `created_by_user_id`,
    ADD COLUMN `updated_by_user_id` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `created_time`,
    ADD COLUMN `updated_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updated_by_user_id`,
    ADD COLUMN `deleted_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabled_flag`;

ALTER TABLE `attendance_rule`
    CHANGE COLUMN `created_at` `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `created_by_user_id` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `early_leave_threshold`,
    ADD COLUMN `created_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `created_by_user_id`,
    ADD COLUMN `updated_by_user_id` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `created_time`,
    ADD COLUMN `updated_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updated_by_user_id`,
    ADD COLUMN `enabled_flag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updated_time`,
    ADD COLUMN `deleted_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabled_flag`;

ALTER TABLE `attendance_record`
    CHANGE COLUMN `created_at` `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `created_by_user_id` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `work_hours`,
    ADD COLUMN `created_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `created_by_user_id`,
    ADD COLUMN `updated_by_user_id` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `created_time`,
    ADD COLUMN `updated_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updated_by_user_id`,
    ADD COLUMN `enabled_flag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updated_time`,
    ADD COLUMN `deleted_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabled_flag`;

ALTER TABLE `leave_application`
    CHANGE COLUMN `created_at` `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `created_by_user_id` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `approved_at`,
    ADD COLUMN `created_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `created_by_user_id`,
    ADD COLUMN `updated_by_user_id` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `created_time`,
    ADD COLUMN `updated_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updated_by_user_id`,
    ADD COLUMN `enabled_flag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updated_time`,
    ADD COLUMN `deleted_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabled_flag`;

ALTER TABLE `makeup_application`
    CHANGE COLUMN `created_at` `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `created_by_user_id` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `approved_at`,
    ADD COLUMN `created_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `created_by_user_id`,
    ADD COLUMN `updated_by_user_id` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `created_time`,
    ADD COLUMN `updated_by_user_name` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updated_by_user_id`,
    ADD COLUMN `enabled_flag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updated_time`,
    ADD COLUMN `deleted_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabled_flag`;

UPDATE `department`
SET `enabled_flag` = 1, `deleted_flag` = 0
WHERE `enabled_flag` IS NULL OR `deleted_flag` IS NULL;

UPDATE `user`
SET `created_by_user_id` = COALESCE(`created_by_user_id`, 1),
    `created_by_user_name` = COALESCE(`created_by_user_name`, '系统初始化'),
    `updated_by_user_id` = COALESCE(`updated_by_user_id`, 1),
    `updated_by_user_name` = COALESCE(`updated_by_user_name`, '系统初始化'),
    `deleted_flag` = COALESCE(`deleted_flag`, 0)
WHERE `created_by_user_id` IS NULL
   OR `created_by_user_name` IS NULL
   OR `updated_by_user_id` IS NULL
   OR `updated_by_user_name` IS NULL
   OR `deleted_flag` IS NULL;

UPDATE `attendance_rule`
SET `enabled_flag` = 1, `deleted_flag` = 0
WHERE `enabled_flag` IS NULL OR `deleted_flag` IS NULL;

UPDATE `attendance_record`
SET `enabled_flag` = 1, `deleted_flag` = 0
WHERE `enabled_flag` IS NULL OR `deleted_flag` IS NULL;

UPDATE `leave_application`
SET `enabled_flag` = 1, `deleted_flag` = 0
WHERE `enabled_flag` IS NULL OR `deleted_flag` IS NULL;

UPDATE `makeup_application`
SET `enabled_flag` = 1, `deleted_flag` = 0
WHERE `enabled_flag` IS NULL OR `deleted_flag` IS NULL;
