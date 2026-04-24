package com.attendance.server.application.auth;

import com.attendance.server.application.auth.dto.LoginRequest;
import com.attendance.server.application.auth.dto.LoginResponse;

/**
 * 璁よ瘉搴旂敤鏈嶅姟鎺ュ彛锛岃礋璐ｆ壙鎺ョ櫥褰曠敤渚嬨€? */
public interface AuthApplicationService {

    LoginResponse login(LoginRequest request);
}
