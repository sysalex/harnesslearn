package com.attendance.server.domain.user.service.impl;

import com.attendance.server.domain.user.entity.User;
import com.attendance.server.domain.user.repository.UserRepository;
import com.attendance.server.domain.user.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户领域服务实现，只编排领域查询语义，持久化细节交给仓储实现。
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
