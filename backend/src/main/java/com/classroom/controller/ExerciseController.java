package com.classroom.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 题目 / 作答相关接口（P0 单选、P1 多选）。
 *
 * <p>占位骨架；字段定义见 docs/04-接口契约模板.md §5。
 *
 * @author M3 后端 API
 */
@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("code", 0, "message", "TODO");
    }

    /**
     * 发布题目（TODO：题干、选项、倒计时、实时推送）。
     */
    @PostMapping("/publish")
    public Map<String, Object> publish() {
        // TODO(M3): 发布后通过 WebSocket 广播 EXERCISE_PUBLISHED
        return Map.of("code", 0, "message", "TODO");
    }
}
