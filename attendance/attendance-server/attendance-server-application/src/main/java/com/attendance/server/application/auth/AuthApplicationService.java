package com.attendance.server.application.auth;

import com.attendance.server.application.auth.dto.LoginRequest;
import com.attendance.server.application.auth.dto.LoginResponse;

/**
 * 认证应用服务接口，负责承接登录用例。
 */
public interface AuthApplicationService {

    LoginResponse login(LoginRequest request);
}
