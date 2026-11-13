package com.classroom.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 课堂相关接口（P0）：建课、生成 6 位课堂码、结束课堂、随机选人。
 *
 * <p>占位骨架，业务逻辑由 M3 在后续周次实现。
 *
 * @author M3 后端 API
 */
@RestController
@RequestMapping("/api/classroom")
public class ClassroomController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("code", 0, "message", "TODO");
    }

    /**
     * 创建课堂（TODO：生成唯一 6 位数字课堂码）。
     */
    @PostMapping
    public Map<String, Object> create() {
        // TODO(M3): 见 docs/04-接口契约模板.md §2.1
        return Map.of("code", 0, "message", "TODO");
    }
}
