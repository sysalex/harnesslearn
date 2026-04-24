-- 2026-04-24
-- 对齐通用基类字段命名：
-- id, createdByUserId, createdByUserName, createdTime,
-- updatedByUserId, updatedByUserName, updatedTime, enabledFlag, deletedFlag

ALTER TABLE `department`
    CHANGE COLUMN `created_at` `createdTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN `createdByUserId` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `parent_id`,
    ADD COLUMN `createdByUserName` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `createdByUserId`,
    ADD COLUMN `updatedByUserId` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `createdTime`,
    ADD COLUMN `updatedByUserName` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updatedByUserId`,
    ADD COLUMN `updatedTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `updatedByUserName`,
    ADD COLUMN `enabledFlag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updatedTime`,
    ADD COLUMN `deletedFlag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabledFlag`;

ALTER TABLE `user`
    CHANGE COLUMN `status` `enabledFlag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    CHANGE COLUMN `created_at` `createdTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updatedTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `createdByUserId` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `role`,
    ADD COLUMN `createdByUserName` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `createdByUserId`,
    ADD COLUMN `updatedByUserId` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `createdTime`,
    ADD COLUMN `updatedByUserName` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updatedByUserId`,
    ADD COLUMN `deletedFlag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabledFlag`;

ALTER TABLE `attendance_rule`
    CHANGE COLUMN `created_at` `createdTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updatedTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `createdByUserId` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `early_leave_threshold`,
    ADD COLUMN `createdByUserName` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `createdByUserId`,
    ADD COLUMN `updatedByUserId` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `createdTime`,
    ADD COLUMN `updatedByUserName` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updatedByUserId`,
    ADD COLUMN `enabledFlag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updatedTime`,
    ADD COLUMN `deletedFlag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabledFlag`;

ALTER TABLE `attendance_record`
    CHANGE COLUMN `created_at` `createdTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updatedTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `createdByUserId` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `work_hours`,
    ADD COLUMN `createdByUserName` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `createdByUserId`,
    ADD COLUMN `updatedByUserId` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `createdTime`,
    ADD COLUMN `updatedByUserName` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updatedByUserId`,
    ADD COLUMN `enabledFlag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updatedTime`,
    ADD COLUMN `deletedFlag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabledFlag`;

ALTER TABLE `leave_application`
    CHANGE COLUMN `created_at` `createdTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updatedTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `createdByUserId` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `approved_at`,
    ADD COLUMN `createdByUserName` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `createdByUserId`,
    ADD COLUMN `updatedByUserId` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `createdTime`,
    ADD COLUMN `updatedByUserName` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updatedByUserId`,
    ADD COLUMN `enabledFlag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updatedTime`,
    ADD COLUMN `deletedFlag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabledFlag`;

ALTER TABLE `makeup_application`
    CHANGE COLUMN `created_at` `createdTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `updated_at` `updatedTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `createdByUserId` BIGINT DEFAULT NULL COMMENT '创建人 ID' AFTER `approved_at`,
    ADD COLUMN `createdByUserName` VARCHAR(50) DEFAULT NULL COMMENT '创建人名称' AFTER `createdByUserId`,
    ADD COLUMN `updatedByUserId` BIGINT DEFAULT NULL COMMENT '更新人 ID' AFTER `createdTime`,
    ADD COLUMN `updatedByUserName` VARCHAR(50) DEFAULT NULL COMMENT '更新人名称' AFTER `updatedByUserId`,
    ADD COLUMN `enabledFlag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER `updatedTime`,
    ADD COLUMN `deletedFlag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除' AFTER `enabledFlag`;

UPDATE `department`
SET `enabledFlag` = 1, `deletedFlag` = 0
WHERE `enabledFlag` IS NULL OR `deletedFlag` IS NULL;

UPDATE `user`
SET `createdByUserId` = COALESCE(`createdByUserId`, 1),
    `createdByUserName` = COALESCE(`createdByUserName`, '系统初始化'),
    `updatedByUserId` = COALESCE(`updatedByUserId`, 1),
    `updatedByUserName` = COALESCE(`updatedByUserName`, '系统初始化'),
    `deletedFlag` = COALESCE(`deletedFlag`, 0)
WHERE `createdByUserId` IS NULL
   OR `createdByUserName` IS NULL
   OR `updatedByUserId` IS NULL
   OR `updatedByUserName` IS NULL
   OR `deletedFlag` IS NULL;

UPDATE `attendance_rule`
SET `enabledFlag` = 1, `deletedFlag` = 0
WHERE `enabledFlag` IS NULL OR `deletedFlag` IS NULL;

UPDATE `attendance_record`
SET `enabledFlag` = 1, `deletedFlag` = 0
WHERE `enabledFlag` IS NULL OR `deletedFlag` IS NULL;

UPDATE `leave_application`
SET `enabledFlag` = 1, `deletedFlag` = 0
WHERE `enabledFlag` IS NULL OR `deletedFlag` IS NULL;

UPDATE `makeup_application`
SET `enabledFlag` = 1, `deletedFlag` = 0
WHERE `enabledFlag` IS NULL OR `deletedFlag` IS NULL;
