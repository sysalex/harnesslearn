package com.attendance.server.architecture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * 锁定后端多模块分层后的目标结构，避免目录迁移到一半时又回退到单模块。
 */
class BackendLayeringStructureTest {

    @Test
    void coreClassesMoveToTargetPackages() throws Exception {
        for (String className : expectedClassNames()) {
            Class.forName(className);
        }
    }

    @Test
    void backendBecomesReactorParentWithSixModules() throws Exception {
        Path parentPom = Path.of("..", "pom.xml");
        assertTrue(Files.exists(parentPom));

        String pomSource = Files.readString(parentPom);
        assertTrue(pomSource.contains("<packaging>pom</packaging>"));
        assertTrue(pomSource.contains("<module>attendance-server-shared</module>"));
        assertTrue(pomSource.contains("<module>attendance-server-domain</module>"));
        assertTrue(pomSource.contains("<module>attendance-server-infrastructure</module>"));
        assertTrue(pomSource.contains("<module>attendance-server-application</module>"));
        assertTrue(pomSource.contains("<module>attendance-server-interfaces</module>"));
        assertTrue(pomSource.contains("<module>attendance-server-starter</module>"));
    }

    @Test
    void childModulePomFilesExist() {
        List<Path> modulePoms = List.of(
                Path.of("..", "attendance-server-shared", "pom.xml"),
                Path.of("..", "attendance-server-domain", "pom.xml"),
                Path.of("..", "attendance-server-infrastructure", "pom.xml"),
                Path.of("..", "attendance-server-application", "pom.xml"),
                Path.of("..", "attendance-server-interfaces", "pom.xml"),
                Path.of("..", "attendance-server-starter", "pom.xml"));

        for (Path modulePom : modulePoms) {
            assertTrue(Files.exists(modulePom), () -> "missing module pom: " + modulePom);
        }
    }

    @Test
    void starterUsesServerPackageComponentScanAndMapperScan() throws Exception {
        Path starterPath = Path.of(
                "src",
                "main",
                "java",
                "com",
                "attendance",
                "server",
                "starter",
                "AttendanceServerApplication.java");
        assertTrue(Files.exists(starterPath));

        String source = Files.readString(starterPath);
        assertTrue(source.contains("scanBasePackages = \"com.attendance.server\""));
        assertTrue(source.contains("@MapperScan(\"com.attendance.server.infrastructure.persistence.mapper\")"));
    }

    @Test
    void legacySingleModuleSourceTreeIsRemoved() {
        List<Path> legacyPaths = List.of(
                Path.of("..", "src", "main", "java"),
                Path.of("..", "src", "test", "java"));

        for (Path legacyPath : legacyPaths) {
            assertFalse(Files.exists(legacyPath), () -> "legacy path still exists: " + legacyPath);
        }
    }

    private List<String> expectedClassNames() {
        return List.of(
                "com.attendance.server.starter.AttendanceServerApplication",
                "com.attendance.server.interfaces.rest.auth.AuthController",
                "com.attendance.server.interfaces.rest.error.GlobalExceptionHandler",
                "com.attendance.server.application.auth.AuthApplicationService",
                "com.attendance.server.application.auth.AuthApplicationServiceImpl",
                "com.attendance.server.application.auth.dto.LoginRequest",
                "com.attendance.server.application.auth.dto.LoginResponse",
                "com.attendance.server.domain.common.entity.BaseEntity",
                "com.attendance.server.domain.user.entity.User",
                "com.attendance.server.domain.user.service.UserService",
                "com.attendance.server.infrastructure.persistence.mapper.UserMapper",
                "com.attendance.server.infrastructure.persistence.service.UserServiceImpl",
                "com.attendance.server.infrastructure.security.JwtTokenProvider",
                "com.attendance.server.infrastructure.security.JwtAuthenticationFilter",
                "com.attendance.server.infrastructure.config.SecurityConfig",
                "com.attendance.server.infrastructure.config.MyBatisPlusConfig",
                "com.attendance.server.shared.response.ApiResponse",
                "com.attendance.server.shared.exception.BusinessException");
    }
}
