package com.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户持久化实体，当前阶段聚焦登录链路所需的最小用户字段集合。
 */
@Getter
@Setter
@TableName("user")
public class User extends BaseEntity {

    private String username;

    private String password;

    @TableField("real_name")
    private String realName;

    private String role;
}
