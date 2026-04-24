package com.attendance.server.domain.user.service;

import com.attendance.server.domain.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户领域服务契约，承接用户实体的 MyBatis-Plus 基础能力和认证所需查询。
 */
public interface UserService extends IService<User> {

    User findByUsername(String username);
}
