package com.attendance;

import com.attendance.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AttendanceSystemApplicationTests {

    @MockBean
    private UserMapper userMapper;

    @Test
    void contextLoads() {
    }
}
