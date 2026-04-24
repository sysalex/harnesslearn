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
    void topLevelParentSeparatesCommonAndServerModules() throws Exception {
        Path topLevelPom = Path.of("..", "..", "pom.xml");
        assertTrue(Files.exists(topLevelPom));

        String pomSource = Files.readString(topLevelPom);
        assertTrue(pomSource.contains("<packaging>pom</packaging>"));
        assertTrue(pomSource.contains("<module>attendance-common</module>"));
        assertTrue(pomSource.contains("<module>attendance-server</module>"));
    }

    @Test
    void serverModuleKeepsOnlyFiveLayerModules() throws Exception {
        Path serverPom = Path.of("..", "pom.xml");
        assertTrue(Files.exists(serverPom));

        String pomSource = Files.readString(serverPom);
        assertTrue(pomSource.contains("<packaging>pom</packaging>"));
        assertTrue(!pomSource.contains("attendance-server-shared"));
        assertTrue(pomSource.contains("<module>attendance-server-domain</module>"));
        assertTrue(pomSource.contains("<module>attendance-server-infrastructure</module>"));
        assertTrue(pomSource.contains("<module>attendance-server-application</module>"));
        assertTrue(pomSource.contains("<module>attendance-server-interfaces</module>"));
        assertTrue(pomSource.contains("<module>attendance-server-starter</module>"));
    }

    @Test
    void childModulePomFilesExist() {
        List<Path> modulePoms = List.of(
                Path.of("..", "..", "attendance-common", "pom.xml"),
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
        assertTrue(source.contains("\"com.attendance.server\""));
        assertTrue(source.contains("\"com.attendance.common\""));
        assertTrue(source.contains("@MapperScan(\"com.attendance.server.infrastructure.persistence.mapper\")"));
    }

    @Test
    void interfacesModuleDoesNotDependOnInfrastructureModule() throws Exception {
        Path interfacesPom = Path.of("..", "attendance-server-interfaces", "pom.xml");
        String pomSource = Files.readString(interfacesPom);

        assertTrue(!pomSource.contains("<artifactId>attendance-server-infrastructure</artifactId>"));
    }

    @Test
    void applicationModuleDoesNotDependOnInfrastructureModule() throws Exception {
        Path applicationPom = Path.of("..", "attendance-server-application", "pom.xml");
        String pomSource = Files.readString(applicationPom);

        assertTrue(!pomSource.contains("<artifactId>attendance-server-infrastructure</artifactId>"));
    }

    @Test
    void starterAggregatesInfrastructureAtRuntime() throws Exception {
        Path starterPom = Path.of("pom.xml");
        String pomSource = Files.readString(starterPom);

        assertTrue(pomSource.contains("<artifactId>attendance-server-interfaces</artifactId>"));
        assertTrue(pomSource.contains("<artifactId>attendance-server-infrastructure</artifactId>"));
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
                "com.attendance.server.domain.user.repository.UserRepository",
                "com.attendance.server.domain.user.service.UserService",
                "com.attendance.server.domain.user.service.impl.UserServiceImpl",
                "com.attendance.server.infrastructure.persistence.mapper.UserMapper",
                "com.attendance.server.infrastructure.persistence.repository.UserRepositoryImpl",
                "com.attendance.common.security.JwtTokenProvider",
                "com.attendance.server.infrastructure.security.JwtAuthenticationFilter",
                "com.attendance.server.starter.config.SecurityConfig",
                "com.attendance.server.infrastructure.config.MyBatisPlusConfig",
                "com.attendance.common.response.ApiResponse",
                "com.attendance.common.exception.BusinessException");
    }
}
