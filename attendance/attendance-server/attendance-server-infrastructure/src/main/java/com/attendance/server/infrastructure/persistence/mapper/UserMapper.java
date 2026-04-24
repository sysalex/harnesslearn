package com.attendance.server.infrastructure.persistence.mapper;

import com.attendance.server.domain.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户持久化 Mapper，当前保留单表查询所需的基础能力。
 */
public interface UserMapper extends BaseMapper<User> {
}
