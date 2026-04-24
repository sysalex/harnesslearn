package com.attendance.server.domain.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.attendance.server.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 鐢ㄦ埛鎸佷箙鍖栧疄浣擄紝瀵归綈褰撳墠 user 琛ㄥ瓧娈靛畾涔夛紝渚涜璇佸拰鍚庣画鐢ㄦ埛绠＄悊澶嶇敤銆? */
@Getter
@Setter
@TableName("user")
public class User extends BaseEntity {

    /** 鐢ㄦ埛鍚嶃€?*/
    private String username;

    /** BCrypt 鍔犲瘑瀵嗙爜銆?*/
    private String password;

    /** 鐪熷疄濮撳悕銆?*/
    @TableField("real_name")
    private String realName;

    /** 閭銆?*/
    private String email;

    /** 鎵嬫満鍙枫€?*/
    private String phone;

    /** 鎵€灞為儴闂?ID銆?*/
    @TableField("department_id")
    private Long departmentId;

    /** 瑙掕壊銆?*/
    private String role;
}
