package com.attendance.mapper;

import com.attendance.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问入口，只提供登录所需的按用户名查询。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("""
            SELECT id, username, password, real_name, role, status
            FROM `user`
            WHERE username = #{username}
            LIMIT 1
            """)
    @Results(id = "UserResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "real_name", property = "realName"),
            @Result(column = "role", property = "role"),
            @Result(column = "status", property = "status")
    })
    User selectByUsername(@Param("username") String username);
}
