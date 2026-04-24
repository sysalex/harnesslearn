package com.attendance.server.domain.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.attendance.server.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户持久化实体，对齐当前 user 表字段定义，供认证和后续用户管理复用。
 */
@Getter
@Setter
@TableName("user")
public class User extends BaseEntity {

    /** 用户名。 */
    private String username;

    /** BCrypt 加密密码。 */
    private String password;

    /** 真实姓名。 */
    @TableField("real_name")
    private String realName;

    /** 邮箱。 */
    private String email;

    /** 手机号。 */
    private String phone;

    /** 所属部门 ID。 */
    @TableField("department_id")
    private Long departmentId;

    /** 角色。 */
    private String role;
}
