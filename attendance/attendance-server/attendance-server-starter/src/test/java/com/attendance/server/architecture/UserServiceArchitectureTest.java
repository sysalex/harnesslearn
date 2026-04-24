package com.attendance.server.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.attendance.server.domain.common.entity.BaseEntity;
import com.attendance.server.domain.user.entity.User;
import com.attendance.server.domain.user.repository.UserRepository;
import com.attendance.server.domain.user.service.UserService;
import com.attendance.server.infrastructure.persistence.repository.UserRepositoryImpl;
import com.attendance.server.infrastructure.persistence.mapper.UserMapper;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * 锁定用户领域服务和持久化实现的分层关系，避免再次退回到单模块直接耦合。
 */
class UserServiceArchitectureTest {

    @Test
    void userServiceStaysIndependentFromMyBatisPlusServiceApi() {
        assertTrue(Arrays.stream(UserService.class.getInterfaces())
                .noneMatch(type -> type.getName().startsWith("com.baomidou.mybatisplus")));
    }

    @Test
    void domainDefinesRepositoryContractImplementedByInfrastructure() {
        assertTrue(UserRepository.class.isInterface());
        assertTrue(UserRepository.class.isAssignableFrom(UserRepositoryImpl.class));
    }

    @Test
    void userMapperOnlyLivesBehindInfrastructureRepository() throws Exception {
        String applicationSource = Files.readString(Path.of(
                "..",
                "attendance-server-application",
                "src",
                "main",
                "java",
                "com",
                "attendance",
                "server",
                "application",
                "auth",
                "AuthApplicationServiceImpl.java"));
        assertTrue(!applicationSource.contains("com.attendance.server.infrastructure"));
        assertTrue(UserMapper.class.getPackageName().contains(".infrastructure.persistence.mapper"));
    }

    @Test
    void userExtendsBaseEntityWithCommonAuditFields() {
        assertTrue(BaseEntity.class.isAssignableFrom(User.class));
        assertHasField("id");
        assertHasField("createdByUserId");
        assertHasField("createdByUserName");
        assertHasField("createdTime");
        assertHasField("updatedByUserId");
        assertHasField("updatedByUserName");
        assertHasField("updatedTime");
        assertHasField("enabledFlag");
        assertHasField("deletedFlag");
    }

    @Test
    void baseEntityUsesLombokInsteadOfManualAccessors() throws Exception {
        String source = Files.readString(Path.of(
                "..",
                "attendance-server-domain",
                "src",
                "main",
                "java",
                "com",
                "attendance",
                "server",
                "domain",
                "common",
                "entity",
                "BaseEntity.java"));
        assertTrue(source.contains("@Getter"));
        assertTrue(source.contains("@Setter"));
        assertTrue(!source.contains("public Long getId()"));
        assertTrue(!source.contains("public void setId("));
    }

    @Test
    void userEntityUsesLombokInsteadOfManualAccessors() throws Exception {
        String source = Files.readString(Path.of(
                "..",
                "attendance-server-domain",
                "src",
                "main",
                "java",
                "com",
                "attendance",
                "server",
                "domain",
                "user",
                "entity",
                "User.java"));
        assertTrue(source.contains("@Getter"));
        assertTrue(source.contains("@Setter"));
        assertTrue(!source.contains("public String getUsername()"));
        assertTrue(!source.contains("public void setUsername("));
    }

    private void assertHasField(String fieldName) {
        assertTrue(Arrays.stream(BaseEntity.class.getDeclaredFields()).map(Field::getName).anyMatch(fieldName::equals));
    }
}
