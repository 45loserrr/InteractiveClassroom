package com.classroom.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 抢答相关接口（P0）：发起抢答、学生抢答、抢答排名。
 *
 * <p>公平性要求：首答判定必须使用数据库服务端时间戳，禁止使用客户端时间。
 *
 * @author M3 后端 API
 */
@RestController
@RequestMapping("/api/race")
public class RaceController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("code", 0, "message", "TODO");
    }

    /**
     * 学生抢答（TODO：毫秒级截断 + 首答标记）。
     */
    @PostMapping("/grab")
    public Map<String, Object> grab() {
        // TODO(M3): INSERT ... raced_at = CURRENT_TIMESTAMP(3)，配合唯一索引防重复
        return Map.of("code", 0, "message", "TODO");
    }
}
