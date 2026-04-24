package com.attendance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.attendance.entity.BaseEntity;
import com.attendance.entity.User;
import com.attendance.mapper.UserMapper;
import com.attendance.service.impl.UserServiceImpl;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * 锁定用户服务分层，避免再次退回到 service 直接依赖 mapper 的写法。
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
    void userServiceExposesFindByUsernameContract() throws NoSuchMethodException {
        assertTrue(User.class.equals(UserService.class.getMethod("findByUsername", String.class).getReturnType()));
        Type genericSuperclass = UserServiceImpl.class.getGenericSuperclass();
        assertTrue(genericSuperclass instanceof ParameterizedType);
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        assertTrue(UserMapper.class.equals(parameterizedType.getActualTypeArguments()[0]));
        assertTrue(User.class.equals(parameterizedType.getActualTypeArguments()[1]));
    }

    @Test
    void userMapperKeepsOnlyBaseMapperCapabilitiesForSingleTableQueries() {
        assertEquals(0, UserMapper.class.getDeclaredMethods().length);
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

    private void assertHasField(String fieldName) {
        assertTrue(Arrays.stream(BaseEntity.class.getDeclaredFields()).map(Field::getName).anyMatch(fieldName::equals));
    }
}
