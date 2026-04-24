package com.attendance.server.interfaces.rest.auth;

import com.attendance.server.application.auth.AuthApplicationService;
import com.attendance.server.application.auth.dto.LoginRequest;
import com.attendance.server.application.auth.dto.LoginResponse;
import com.attendance.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口入口，只负责接收请求、调用认证服务和包装响应。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.success(authApplicationService.login(loginRequest));
    }
}
