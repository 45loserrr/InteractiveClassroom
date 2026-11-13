package com.classroom.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证相关接口（P0）。
 *
 * <p>占位骨架：第 1 周由 M3 实现登录、学生免注册进课堂。
 *
 * @author M3 后端 API
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 健康检查：用于确认服务已启动、前端代理是否打通。
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("code", 0, "message", "pong");
    }

    /**
     * 教师登录 / 学生进课堂（TODO：返回 JWT Token）。
     */
    @PostMapping("/login")
    public Map<String, Object> login() {
        // TODO(M3): 校验账号密码，签发 Token（契约见 docs/04-接口契约模板.md §1）
        return Map.of("code", 0, "message", "TODO");
    }
}
