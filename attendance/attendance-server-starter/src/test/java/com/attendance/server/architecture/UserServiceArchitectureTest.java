package com.attendance.server.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.attendance.server.domain.common.entity.BaseEntity;
import com.attendance.server.domain.user.entity.User;
import com.attendance.server.domain.user.service.UserService;
import com.attendance.server.infrastructure.persistence.mapper.UserMapper;
import com.attendance.server.infrastructure.persistence.service.UserServiceImpl;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * 锁定用户领域服务和持久化实现的分层关系，避免再次退回到单模块直接耦合。
 */
class UserServiceArchitectureTest {

    @Test
    void userServiceExtendsIService() {
        assertTrue(IService.class.isAssignableFrom(UserService.class));
    }

    @Test
    void userServiceImplExtendsMyBatisPlusServiceImpl() {
        assertTrue(ServiceImpl.class.isAssignableFrom(UserServiceImpl.class));
        assertTrue(UserService.class.isAssignableFrom(UserServiceImpl.class));
    }

    @Test
    void userServiceImplUsesUserMapperAndUserEntity() {
        Type genericSuperclass = UserServiceImpl.class.getGenericSuperclass();
        assertTrue(genericSuperclass instanceof ParameterizedType);
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        assertTrue(UserMapper.class.equals(parameterizedType.getActualTypeArguments()[0]));
        assertTrue(User.class.equals(parameterizedType.getActualTypeArguments()[1]));
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
