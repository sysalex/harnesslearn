package com.attendance.server.infrastructure.config;

import com.attendance.server.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 认证基础设施配置，保留 JWT 接入点并放行登录接口。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectProvider<JwtAuthenticationFilter> jwtAuthenticationFilterProvider)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated());

        jwtAuthenticationFilterProvider.ifAvailable(filter ->
                http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
