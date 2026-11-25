package com.classroom.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classroom.common.Result;
import com.classroom.entity.Answer;
import com.classroom.entity.Checkin;
import com.classroom.entity.Classroom;
import com.classroom.entity.Exercise;
import com.classroom.entity.User;
import com.classroom.mapper.AnswerMapper;
import com.classroom.mapper.CheckinMapper;
import com.classroom.mapper.ClassroomMapper;
import com.classroom.mapper.ExerciseMapper;
import com.classroom.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/screen")
public class ScreenController {
    @Autowired private ClassroomMapper classroomMapper;  // M2：查课堂名、口令
    @Autowired private UserMapper       userMapper;       // PM：查学生总数
    @Autowired private CheckinMapper    checkinMapper;    // M1：查签到人数
    @Autowired private ExerciseMapper   exerciseMapper;   // M2：查本课堂题目
    @Autowired private AnswerMapper     answerMapper;     // M4：查答题记录
    @GetMapping("/stats")
    public Result stats(@RequestParam Long classroomId) {
        Classroom classroom = classroomMapper.selectById(classroomId);
        if (classroom == null) {
            return Result.error("课堂不存在");
        }

        // 学生总数
        Long studentCount = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getRole, "student"));

        // 签到人数
        Long checkinCount = checkinMapper.selectCount(
            new LambdaQueryWrapper<Checkin>().eq(Checkin::getClassroomId, classroomId));
        
        // 查题目，取id
        List<Exercise> exercises = exerciseMapper.selectList(
            new LambdaQueryWrapper<Exercise>().eq(Exercise::getClassroomId, classroomId));
        List<Long> exerciseIds = new ArrayList<>();
        for (Exercise e : exercises) {
            exerciseIds.add(e.getId());
        }

        //四个键先全部初始化
        Map<String, Integer> optionCount = new LinkedHashMap<>();
        optionCount.put("A", 0);
        optionCount.put("B", 0);
        optionCount.put("C", 0);
        optionCount.put("D", 0);

        int answerCount = 0;
        // 一道题都没发时，跳过查询
        if (!exerciseIds.isEmpty()) {
            // 用题目 id 列表查所有答题记录
            List<Answer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<Answer>().in(Answer::getExerciseId, exerciseIds));
            answerCount = answers.size();

            for (Answer a : answers) {
                String op = a.getChooseOption();
                // 只统计 A/B/C/D
                if (optionCount.containsKey(op)) {
                    optionCount.put(op, optionCount.get(op) + 1);
                }
            }
        }
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("classroomName", classroom.getName());
        data.put("classroomCode", classroom.getCode());   // 大屏要显示口令
        data.put("studentCount", studentCount);
        data.put("checkinCount", checkinCount);
        data.put("answerCount", answerCount);
        data.put("optionCount", optionCount);

        return Result.ok(data);
    }
}
