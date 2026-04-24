package com.attendance.server.starter;

import com.attendance.server.infrastructure.persistence.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = AttendanceServerApplication.class)
class AttendanceServerApplicationTests {

    @MockBean
    private UserMapper userMapper;

    @Test
    void contextLoads() {
    }
}
