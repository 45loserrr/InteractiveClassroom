package com.classroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 移动互动课堂系统 - 启动类。
 *
 * <p>启动后：
 * <ul>
 *   <li>REST API: http://localhost:8080/api/**</li>
 *   <li>Swagger: http://localhost:8080/swagger-ui.html</li>
 *   <li>WebSocket: ws://localhost:8080/ws （由 M4 实时服务实现）</li>
 * </ul>
 *
 * @author M3 后端 API
 */
@SpringBootApplication
@MapperScan("com.classroom.mapper")
public class ClassroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassroomApplication.class, args);
    }
}
