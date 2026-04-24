package com.attendance.server.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 鍚庣鍗曟ā鍧楀惎鍔ㄥ叆鍙ｏ紝缁熶竴鎵挎帴椤跺眰缁勪欢鎵弿鍜?Mapper 鎵弿銆? */
@SpringBootApplication(scanBasePackages = "com.attendance.server")
@MapperScan("com.attendance.server.infrastructure.persistence.mapper")
public class AttendanceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceServerApplication.class, args);
    }
}
