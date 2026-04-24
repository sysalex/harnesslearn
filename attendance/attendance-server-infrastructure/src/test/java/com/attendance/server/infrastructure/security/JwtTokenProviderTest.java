package com.attendance.server.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

/**
 * 锁定 JWT 工具类的核心行为，避免后续接 Spring Security 时把 token 语义改坏。
 */
class JwtTokenProviderTest {

    private static final String SECRET =
            "attendance-secret-attendance-secret-attendance-secret";

    @Test
    void generatesAndValidatesAccessToken() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 30, 7);

        String accessToken = jwtTokenProvider.generateAccessToken(1L, "admin", "ADMIN");

        assertTrue(jwtTokenProvider.validateToken(accessToken));
        assertEquals(1L, jwtTokenProvider.getUserId(accessToken));
        assertEquals("admin", jwtTokenProvider.getUsername(accessToken));
        assertEquals("ADMIN", jwtTokenProvider.getRole(accessToken));
        assertEquals("access", jwtTokenProvider.getTokenType(accessToken));
    }

    @Test
    void generatesAndValidatesRefreshToken() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 30, 7);

        String refreshToken = jwtTokenProvider.generateRefreshToken(1L, "admin", "ADMIN");

        assertTrue(jwtTokenProvider.validateToken(refreshToken));
        assertEquals("refresh", jwtTokenProvider.getTokenType(refreshToken));
        assertEquals("admin", jwtTokenProvider.getUsername(refreshToken));
    }

    @Test
    void refreshTokenProducesNewAccessTokenWithSameIdentityClaims() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 30, 7);

        String refreshToken = jwtTokenProvider.generateRefreshToken(1L, "admin", "ADMIN");
        String refreshedAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);

        assertNotEquals(refreshToken, refreshedAccessToken);
        assertTrue(jwtTokenProvider.validateToken(refreshedAccessToken));
        assertEquals("access", jwtTokenProvider.getTokenType(refreshedAccessToken));
        assertEquals(1L, jwtTokenProvider.getUserId(refreshedAccessToken));
        assertEquals("admin", jwtTokenProvider.getUsername(refreshedAccessToken));
        assertEquals("ADMIN", jwtTokenProvider.getRole(refreshedAccessToken));
    }

    @Test
    void rejectsTamperedToken() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 30, 7);

        String accessToken = jwtTokenProvider.generateAccessToken(1L, "admin", "ADMIN");
        String tamperedToken = accessToken.substring(0, accessToken.length() - 2) + "zz";

        assertFalse(jwtTokenProvider.validateToken(tamperedToken));
    }

    @Test
    void parsesClaimsFromToken() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 30, 7);

        String accessToken = jwtTokenProvider.generateAccessToken(2L, "zhangsan", "MANAGER");
        Claims claims = jwtTokenProvider.getClaims(accessToken);

        assertEquals("zhangsan", claims.getSubject());
        assertEquals(2L, ((Number) claims.get("userId")).longValue());
        assertEquals("MANAGER", claims.get("role"));
    }
}
