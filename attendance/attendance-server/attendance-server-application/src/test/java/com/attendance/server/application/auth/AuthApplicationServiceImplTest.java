package com.attendance.server.application.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.attendance.server.application.auth.dto.LoginRequest;
import com.attendance.server.application.auth.dto.LoginResponse;
import com.attendance.server.domain.user.entity.User;
import com.attendance.server.domain.user.service.UserService;
import com.attendance.common.exception.BusinessException;
import com.attendance.common.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 锁定登录用例的最小认证语义，避免后续调整分层时把密码校验或 token 返回改坏。
 */
@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthApplicationServiceImpl authApplicationService;

    @Test
    void loginReturnsTokensForActiveUserWithValidPassword() {
        User user = buildUser();
        when(userService.findByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("admin123", user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(1L, "admin", "ADMIN")).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(1L, "admin", "ADMIN")).thenReturn("refresh-token");

        LoginResponse response = authApplicationService.login(new LoginRequest("admin", "admin123"));

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("admin", response.user().username());
        assertEquals("系统管理员", response.user().realName());
        assertEquals("ADMIN", response.user().role());
    }

    @Test
    void loginRejectsInvalidPassword() {
        User user = buildUser();
        when(userService.findByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authApplicationService.login(new LoginRequest("admin", "wrong-password")));

        assertEquals(401, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
        verify(jwtTokenProvider, never()).generateAccessToken(anyLong(), org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void loginRejectsDisabledUser() {
        User user = buildUser();
        user.setEnabledFlag(false);
        when(userService.findByUsername("admin")).thenReturn(user);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authApplicationService.login(new LoginRequest("admin", "admin123")));

        assertEquals(403, exception.getCode());
        assertEquals("账号已被禁用", exception.getMessage());
    }

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("$2a$12$hash");
        user.setRealName("系统管理员");
        user.setRole("ADMIN");
        user.setEnabledFlag(true);
        user.setDeletedFlag(false);
        return user;
    }
}
