package com.attendance.server.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后端启动入口，统一承接服务组件扫描和 Mapper 扫描。
 */
@SpringBootApplication(scanBasePackages = {"com.attendance.server", "com.attendance.common"})
@MapperScan("com.attendance.server.infrastructure.persistence.mapper")
public class AttendanceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceServerApplication.class, args);
    }
}
