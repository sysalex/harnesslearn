package com.attendance.service;

import com.attendance.model.dto.auth.LoginRequest;
import com.attendance.model.dto.auth.LoginResponse;

/**
 * 认证服务接口，只暴露登录能力。
 */
public interface AuthService {

    LoginResponse login(LoginRequest request);
}
