package com.attendance.mapper;

import com.attendance.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户数据访问入口，仅保留登录链路所需的按用户名查询。
 */
public interface UserMapper extends BaseMapper<User> {

    User selectByUsername(String username);
}
