package com.attendance.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.attendance.AttendanceSystemApplication;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

/**
 * 锁定 1.2.2 的三个验收点：数据源、Mapper 扫描、分页插件。
 */
@SpringBootTest
class MyBatisPlusConfigTest {

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private MybatisPlusInterceptor mybatisPlusInterceptor;

    @Test
    void datasourcePropertiesAreConfiguredForAttendanceDatabase() {
        assertEquals(
                "jdbc:mysql://127.0.0.1:3306/attendance_db?useUnicode=true&characterEncoding=utf8"
                        + "&serverTimezone=Asia/Shanghai",
                environment.getProperty("spring.datasource.url"));
        assertEquals("root", environment.getProperty("spring.datasource.username"));
        assertNotNull(environment.getProperty("spring.datasource.driver-class-name"));
    }

    @Test
    void applicationEnablesMapperScanningForMapperPackage() {
        MapperScan mapperScan = AttendanceSystemApplication.class.getAnnotation(MapperScan.class);

        assertNotNull(mapperScan);
        assertTrue(Arrays.asList(mapperScan.basePackages()).contains("com.attendance.mapper")
                || Arrays.asList(mapperScan.value()).contains("com.attendance.mapper"));
    }

    @Test
    void paginationInterceptorBeanUsesMysqlDialect() {
        assertNotNull(mybatisPlusInterceptor);

        InnerInterceptor paginationInterceptor = mybatisPlusInterceptor.getInterceptors().stream()
                .filter(PaginationInnerInterceptor.class::isInstance)
                .findFirst()
                .orElse(null);

        assertNotNull(paginationInterceptor);
        assertEquals(
                DbType.MYSQL,
                ((PaginationInnerInterceptor) paginationInterceptor).getDbType());
    }
}
