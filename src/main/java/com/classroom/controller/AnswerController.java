package com.classroom.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.classroom.common.Result;
import com.classroom.entity.Answer;
import com.classroom.entity.Exercise;
import com.classroom.mapper.AnswerMapper;
import com.classroom.mapper.ExerciseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * M4 答题：POST /api/answer
 * 对错由后端查 exercise.answer 判断，不信前端。
 */
@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    @Autowired
    private AnswerMapper answerMapper;

    // M2 的题目 Mapper，只读不改
    @Autowired
    private ExerciseMapper exerciseMapper;

    @PostMapping
    public Result submit(@RequestBody Answer req) {
        Long exerciseId = req.getExerciseId();
        Long studentId = req.getStudentId();
        String choose = req.getChooseOption() == null ? "" : req.getChooseOption().trim().toUpperCase();

        if (exerciseId == null || studentId == null || choose.isEmpty()) {
            return Result.error("参数不完整");
        }
        // 选项只能是 A/B/C/D，挡掉乱传的脏数据
        if (!choose.matches("[ABCD]")) {
            return Result.error("选项只能是 A/B/C/D");
        }

        // 1. 题目必须存在
        Exercise exercise = exerciseMapper.selectById(exerciseId);
        if (exercise == null) {
            return Result.error("题目不存在");
        }

        // 2. 同一学生不能重复答同一题
        QueryWrapper<Answer> wrapper = new QueryWrapper<>();
        wrapper.eq("exercise_id", exerciseId).eq("student_id", studentId);
        Long count = answerMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return Result.error("你已经答过这题了");
        }

        // 3. 后端判对错：拿题目的 answer 比对学生选的
        String right = exercise.getAnswer();
        boolean correct = right != null && right.equalsIgnoreCase(choose);

        // 4. 存答题记录
        Answer answer = new Answer();
        answer.setExerciseId(exerciseId);
        answer.setStudentId(studentId);
        answer.setChooseOption(choose);
        answerMapper.insert(answer);

        // 5. 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("correct", correct);
        data.put("rightAnswer", right);
        return Result.ok("提交成功", data);
    }
}
