package com.classroom.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 签到相关接口（P0）：学生签到、签到名单、关闭签到窗口。
 *
 * <p>占位骨架；签名规则见 docs/04-接口契约模板.md §3。
 *
 * @author M3 后端 API
 */
@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("code", 0, "message", "TODO");
    }

    /**
     * 学生签到（TODO：重复签到拦截、签到人数统计）。
     */
    @PostMapping
    public Map<String, Object> checkin() {
        // TODO(M3): 唯一索引 (classroom_id, student_id) 兜底防重
        return Map.of("code", 0, "message", "TODO");
    }
}
