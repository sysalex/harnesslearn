package com.attendance.server.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
class TestSecuredController {

    @GetMapping("/secured")
    String secured() {
        return "secured";
    }
}
