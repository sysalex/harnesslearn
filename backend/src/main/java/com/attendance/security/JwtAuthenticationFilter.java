package com.attendance.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 从 Bearer Token 中恢复认证信息的过滤器。
 * 当前阶段直接基于 token 中的用户名和角色建认证对象，后续如需接数据库可在这里扩展。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveBearerToken(request);
        if (StringUtils.hasText(token)) {
            authenticate(token);
        }
        filterChain.doFilter(request, response);
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }

    private void authenticate(String token) {
        try {
            if (!jwtTokenProvider.validateToken(token)) {
                return;
            }
            String username = jwtTokenProvider.getUsername(token);
            String role = jwtTokenProvider.getRole(token);
            if (!StringUtils.hasText(username)) {
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            username, null, List.of(new SimpleGrantedAuthority(resolveAuthority(role)))));
        } catch (RuntimeException ignored) {
            // 认证失败时保持未认证状态，交由后续安全链路统一返回 401。
        }
    }

    private String resolveAuthority(String role) {
        if (!StringUtils.hasText(role) || role.startsWith(ROLE_PREFIX)) {
            return role;
        }
        return ROLE_PREFIX + role;
    }
}
