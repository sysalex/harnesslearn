package com.attendance.server.interfaces.rest.auth;

import com.attendance.server.application.auth.AuthApplicationService;
import com.attendance.server.application.auth.dto.LoginRequest;
import com.attendance.server.application.auth.dto.LoginResponse;
import com.attendance.server.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 璁よ瘉鎺ュ彛鍏ュ彛锛屽彧璐熻矗鎺ユ敹璇锋眰銆佽皟鐢ㄨ璇佹湇鍔″拰鍖呰鍝嶅簲銆? */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.success(authApplicationService.login(loginRequest));
    }
}
