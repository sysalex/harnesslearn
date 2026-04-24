package com.attendance.service.impl;

import com.attendance.entity.User;
import com.attendance.mapper.UserMapper;
import com.attendance.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户实体服务实现，统一承接 MyBatis-Plus 的通用 CRUD 和用户定制查询。
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User findByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }
}
