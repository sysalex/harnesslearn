package com.attendance.server.infrastructure.persistence.service;

import com.attendance.server.domain.user.entity.User;
import com.attendance.server.domain.user.service.UserService;
import com.attendance.server.infrastructure.persistence.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户持久化服务实现，统一承接 MyBatis-Plus 的通用 CRUD 和用户名查询。
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return lambdaQuery().eq(User::getUsername, username).one();
    }
}
