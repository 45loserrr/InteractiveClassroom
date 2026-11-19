package com.example.classroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类。
 * 右键运行 main 方法即可，默认端口 8080。
 */
@SpringBootApplication
@MapperScan("com.example.classroom.mapper")
public class ClassroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassroomApplication.class, args);
        System.out.println("启动成功！浏览器打开：http://localhost:8080/login.html");
    }
}
