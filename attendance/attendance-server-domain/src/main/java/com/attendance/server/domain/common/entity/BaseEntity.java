package com.attendance.server.domain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * 公共实体基类，统一承接审计字段、启用状态和逻辑删除字段。
 * Java 字段保留领域语义命名，数据库列统一走下划线风格。
 */
@Getter
@Setter
public class BaseEntity {

    /** 主键，统一使用数据库自增 ID。 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 审计字段，记录创建人用户 ID，允许为空以兼容初始化数据。 */
    @TableField("created_by_user_id")
    private Long createdByUserId;

    /** 审计字段，冗余创建人名称，便于排查和展示。 */
    @TableField("created_by_user_name")
    private String createdByUserName;

    /** 创建时间由数据库维护，实体侧只做映射。 */
    @TableField("created_time")
    private LocalDateTime createdTime;

    /** 审计字段，记录最后更新人用户 ID。 */
    @TableField("updated_by_user_id")
    private Long updatedByUserId;

    /** 审计字段，冗余最后更新人名称，减少联表读取成本。 */
    @TableField("updated_by_user_name")
    private String updatedByUserName;

    /** 更新时间由数据库自动更新，避免业务层重复赋值。 */
    @TableField("updated_time")
    private LocalDateTime updatedTime;

    /** 业务启用标记，用于控制记录是否可被正常使用。 */
    @TableField("enabled_flag")
    private Boolean enabledFlag;

    /** 逻辑删除标记，配合 MyBatis-Plus 统一过滤已删除数据。 */
    @TableLogic(value = "0", delval = "1")
    @TableField("deleted_flag")
    private Boolean deletedFlag;
}
