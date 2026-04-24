package com.attendance.server.infrastructure.persistence.repository;

import com.attendance.server.domain.user.entity.User;
import com.attendance.server.domain.user.repository.UserRepository;
import com.attendance.server.infrastructure.persistence.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用户仓储实现，负责把领域仓储契约适配到 MyBatis-Plus Mapper。
 */
@Component
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
}
