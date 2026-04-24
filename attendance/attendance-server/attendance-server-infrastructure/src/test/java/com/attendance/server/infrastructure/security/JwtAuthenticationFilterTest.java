package com.attendance.server.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.attendance.common.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

    private static final String SECRET =
            "attendance-secret-attendance-secret-attendance-secret";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setsAuthenticationForValidBearerToken() throws ServletException, IOException {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 30, 7);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                "Authorization",
                "Bearer " + jwtTokenProvider.generateAccessToken(7L, "zhangsan", "ADMIN"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        RecordingFilterChain filterChain = new RecordingFilterChain();

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals("zhangsan", authentication.getName());
        assertAuthorities(authentication.getAuthorities(), "ROLE_ADMIN");
        assertTrue(filterChain.invoked);
    }

    @Test
    void leavesContextUnauthenticatedWhenHeaderIsMissing() throws ServletException, IOException {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 30, 7);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RecordingFilterChain filterChain = new RecordingFilterChain();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(filterChain.invoked);
    }

    @Test
    void leavesContextUnauthenticatedWhenTokenIsInvalid() throws ServletException, IOException {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 30, 7);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer not-a-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RecordingFilterChain filterChain = new RecordingFilterChain();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(filterChain.invoked);
    }

    private static void assertAuthorities(
            Collection<? extends GrantedAuthority> authorities, String expectedAuthority) {
        assertNotNull(authorities);
        assertTrue(authorities.stream().anyMatch(
                authority -> expectedAuthority.equals(authority.getAuthority())));
    }

    private static final class RecordingFilterChain implements FilterChain {

        private boolean invoked;

        @Override
        public void doFilter(
                jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) {
            invoked = true;
        }
    }
}
