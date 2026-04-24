package com.attendance.server.application.auth;

import com.attendance.server.application.auth.dto.LoginRequest;
import com.attendance.server.application.auth.dto.LoginResponse;
import com.attendance.server.domain.user.entity.User;
import com.attendance.server.domain.user.service.UserService;
import com.attendance.common.exception.BusinessException;
import com.attendance.common.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 登录认证实现，只处理用户查询、状态校验、密码校验和 Token 生成。
 */
@Service
public class AuthApplicationServiceImpl implements AuthApplicationService {

    private static final int UNAUTHORIZED_CODE = 401;
    private static final int FORBIDDEN_CODE = 403;
    private static final String DEFAULT_TOKEN_TYPE = "Bearer";
    private static final String INVALID_CREDENTIALS_MESSAGE = "用户名或密码错误";
    private static final String DISABLED_ACCOUNT_MESSAGE = "账号已被禁用";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-token-expiration-minutes:30}")
    private long accessTokenExpirationMinutes = 30L;

    public AuthApplicationServiceImpl(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.username())
                || !StringUtils.hasText(request.password())) {
            throw new BusinessException(UNAUTHORIZED_CODE, INVALID_CREDENTIALS_MESSAGE);
        }

        User user = userService.findByUsername(request.username());
        if (user == null) {
            throw new BusinessException(UNAUTHORIZED_CODE, INVALID_CREDENTIALS_MESSAGE);
        }
        if (!Boolean.TRUE.equals(user.getEnabledFlag())) {
            throw new BusinessException(FORBIDDEN_CODE, DISABLED_ACCOUNT_MESSAGE);
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(UNAUTHORIZED_CODE, INVALID_CREDENTIALS_MESSAGE);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getUsername(), user.getRole());

        return new LoginResponse(
                accessToken,
                refreshToken,
                DEFAULT_TOKEN_TYPE,
                accessTokenExpirationMinutes * 60,
                new LoginResponse.LoginUser(
                        user.getId(),
                        user.getUsername(),
                        user.getRealName(),
                        user.getRole()));
    }
}
