package com.attendance.server.domain.user.service;

import com.attendance.server.domain.user.entity.User;

/**
 * 用户领域服务契约，承接认证和后续用户管理所需的领域查询。
 */
public interface UserService {

    User findByUsername(String username);
}
