package com.attendance.server.application.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求参数，只保留认证所需的最小字段。
 */
public record LoginRequest(
        @NotBlank(message = "用户名不能为空")
        String username,
        @NotBlank(message = "密码不能为空")
        String password) {
}
