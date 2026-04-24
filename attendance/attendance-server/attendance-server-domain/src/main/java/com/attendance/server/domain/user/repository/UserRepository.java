package com.attendance.server.domain.user.repository;

import com.attendance.server.domain.user.entity.User;

/**
 * 用户仓储契约，领域层只关心用户查询能力，不感知 Mapper 或数据库细节。
 */
public interface UserRepository {

    User findByUsername(String username);
}
