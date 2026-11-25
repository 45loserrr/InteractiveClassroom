package com.classroom.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.classroom.common.Result;
import com.classroom.entity.Classroom;
import com.classroom.entity.Exercise;
import com.classroom.mapper.ClassroomMapper;
import com.classroom.mapper.ExerciseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发题相关接口（M2 负责）。
 *
 * POST /api/exercise        发布题目
 * GET  /api/exercise/list   题目列表（给 M4 答题页用，绝对不返回 answer）
 */
@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {

    /** 选项最多 4 个（A/B/C/D），最少 2 个 */
    private static final int MIN_OPTIONS = 2;
    private static final int MAX_OPTIONS = 4;

    /** 合法答案字母 */
    private static final String ANSWER_LETTERS = "ABCD";

    @Autowired
    private ExerciseMapper exerciseMapper;

    /** 发题前要确认课堂真的存在，只用不打别人代码 */
    @Autowired
    private ClassroomMapper classroomMapper;

    /**
     * 发布题目。
     * 请求：{ "classroomId": 1, "title": "...", "options": "A.80|B.443|C.8080|D.3306", "answer": "A" }
     * 返回：{ "id": 1 }
     */
    @PostMapping
    public Result publish(@RequestBody Exercise exercise) {
        Long classroomId = exercise.getClassroomId();
        if (classroomId == null) {
            return Result.error("课堂 id 不能为空，请先建课");
        }

        Classroom classroom = classroomMapper.selectById(classroomId);
        if (classroom == null) {
            return Result.error("课堂不存在，请刷新页面重新选课堂");
        }

        String title = trim(exercise.getTitle());
        if (title.isEmpty()) {
            return Result.error("题干不能为空");
        }
        if (title.length() > 500) {
            return Result.error("题干最多 500 个字");
        }

        String options = trim(exercise.getOptions());
        if (options.isEmpty()) {
            return Result.error("至少要填两个选项");
        }

        // 契约规定选项用 | 分隔，这里按 | 拆开校验，顺带去掉空选项
        String[] parts = options.split("\\|");
        List<String> validOptions = new ArrayList<>();
        for (String part : parts) {
            String item = trim(part);
            if (!item.isEmpty()) {
                validOptions.add(item);
            }
        }
        if (validOptions.size() < MIN_OPTIONS) {
            return Result.error("至少要填两个选项");
        }
        if (validOptions.size() > MAX_OPTIONS) {
            return Result.error("选项最多 4 个（A/B/C/D）");
        }

        String answer = trim(exercise.getAnswer()).toUpperCase();
        if (answer.isEmpty()) {
            return Result.error("请选择正确答案");
        }
        int answerIndex = ANSWER_LETTERS.indexOf(answer);
        if (answer.length() != 1 || answerIndex < 0) {
            return Result.error("正确答案只能是 A、B、C、D");
        }
        if (answerIndex >= validOptions.size()) {
            return Result.error("正确答案超出了已填选项的数量");
        }

        Exercise row = new Exercise();
        row.setClassroomId(classroomId);
        row.setTitle(title);
        row.setOptions(String.join("|", validOptions));
        row.setAnswer(answer);
        exerciseMapper.insert(row);

        Map<String, Object> data = new HashMap<>();
        data.put("id", row.getId());
        return Result.ok("发布成功", data);
    }

    /**
     * 题目列表（M4 答题页用）。
     * 只返回 id / classroomId / title / options，answer 绝不能出现在这里，
     * 否则学生打开 F12 就能看到答案。
     */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) Long classroomId) {
        if (classroomId == null) {
            return Result.error("课堂 id 不能为空");
        }

        QueryWrapper<Exercise> wrapper = new QueryWrapper<>();
        wrapper.eq("classroom_id", classroomId);
        // 按发布顺序排，先发的在前
        wrapper.orderByAsc("id");

        List<Exercise> rows = exerciseMapper.selectList(wrapper);

        List<Map<String, Object>> data = new ArrayList<>();
        for (Exercise row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row.getId());
            item.put("classroomId", row.getClassroomId());
            item.put("title", row.getTitle());
            item.put("options", row.getOptions());
            // 这里故意不放 answer
            data.add(item);
        }
        return Result.ok(data);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /** 仅供参考：把选项字符串拆成数组，前端也可以直接 split('|') */
    static List<String> splitOptions(String options) {
        if (options == null || options.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(options.split("\\|"));
    }
}
