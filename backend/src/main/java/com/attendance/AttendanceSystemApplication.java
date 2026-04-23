package com.attendance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后端应用启动入口。
 * 当前阶段先统一开启 Mapper 扫描，后续实体和 Mapper 落地后直接复用。
 */
@SpringBootApplication
@MapperScan("com.attendance.mapper")
public class AttendanceSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceSystemApplication.class, args);
    }
}
