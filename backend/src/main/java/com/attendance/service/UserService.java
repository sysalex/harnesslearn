package com.attendance.service;

import com.attendance.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户领域服务，承接用户实体的 MyBatis-Plus 基础能力和登录所需的定制查询。
 */
public interface UserService extends IService<User> {

    User findByUsername(String username);
}
