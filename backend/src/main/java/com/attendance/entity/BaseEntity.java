package com.attendance.entity;

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

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("created_by_user_id")
    private Long createdByUserId;

    @TableField("created_by_user_name")
    private String createdByUserName;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_by_user_id")
    private Long updatedByUserId;

    @TableField("updated_by_user_name")
    private String updatedByUserName;

    @TableField("updated_time")
    private LocalDateTime updatedTime;

    @TableField("enabled_flag")
    private Boolean enabledFlag;

    @TableLogic(value = "0", delval = "1")
    @TableField("deleted_flag")
    private Boolean deletedFlag;
}
