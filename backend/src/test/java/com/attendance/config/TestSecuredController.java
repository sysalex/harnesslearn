package com.attendance.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置级测试使用的最小控制器，避免把断言绑定到业务控制器上。
 */
@RestController
class TestSecuredController {

    @GetMapping("/api/auth/login")
    public String login() {
        return "ok";
    }

    @GetMapping("/api/test/secured")
    public String secured() {
        return "secured";
    }
}
