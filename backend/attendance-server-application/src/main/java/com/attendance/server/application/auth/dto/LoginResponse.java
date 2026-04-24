package com.attendance.server.application.auth.dto;

/**
 * 登录成功后的响应载体，包含 token 与最小用户信息。
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        LoginUser user) {

    /**
     * 登录返回的用户摘要，只暴露前端登录态所需的信息。
     */
    public record LoginUser(Long id, String username, String realName, String role) {
    }
}
