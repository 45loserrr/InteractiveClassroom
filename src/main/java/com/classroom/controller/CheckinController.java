package com.classroom.controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.classroom.common.Result;
import com.classroom.entity.Checkin;
import com.classroom.mapper.CheckinMapper;
import com.classroom.mapper.UserMapper;
import com.classroom.entity.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@RestController
@RequestMapping("/api/checkin")
public class CheckinController {
    private final CheckinMapper checkinMapper;
    private final UserMapper userMapper;

    public CheckinController(CheckinMapper checkinMapper, UserMapper userMapper) {
        this.checkinMapper = checkinMapper;
        this.userMapper = userMapper;
    }

    public record CheckinRequest(Long classroomId,Long studentId) {
    }    

    @PostMapping
    @Transactional
    public Result add(@RequestBody(required = false) CheckinRequest request) {
        if (request == null || request.classroomId() == null || request.studentId() == null){
            return Result.error("请提供课堂ID和学生ID");
        }
        
        Long classroomId = request.classroomId();
        Long studentId = request.studentId();
        if(classroomId <= 0 || studentId <= 0){
            return Result.error("课堂ID和学生ID必须为正数");
        }

        if (checkinMapper.lockClassroom(classroomId) == null){
            return Result.error("课堂不存在");
        }

        User student = userMapper.selectById(studentId);
        if (student == null || !"student".equals(student.getRole())){
            return Result.error("学生不存在或角色不正确");
        }

        long count = checkinMapper.selectCount(new QueryWrapper<Checkin>()
            .eq("classroom_id",classroomId)
            .eq("student_id",studentId));
        if (count > 0){
            return Result.error("你已经签到过了");
        }

        Checkin checkin = new Checkin();
        checkin.setClassroomId(classroomId);
        checkin.setStudentId(studentId);
        checkin.setCreateTime(LocalDateTime.now().withNano(0));
        checkinMapper.insert(checkin);

        checkin.setStudentName(student.getRealName());
        return Result.ok("签到成功",checkin);
    }

    @GetMapping("/list")
    public Result list(@RequestParam(value = "classroomId",required = false) Long classroomId){
        if (classroomId == null || classroomId <= 0){
            return Result.error("请提供有效的课堂ID");
        }
        List<Checkin> rows = checkinMapper.selectList(new QueryWrapper<Checkin>()
            .eq("classroom_id",classroomId)
            .orderByAsc("create_time","id"));

        if(rows.isEmpty()){
            return Result.ok(rows);
        }
        List<Long> studentIds = rows.stream()
         .map(Checkin::getStudentId)
         .distinct()
         .toList();
         
        Map<Long,String> names = new HashMap<>();
        for (User student : userMapper.selectBatchIds(studentIds)){
            names.put(student.getId(),student.getRealName());
        }

        for(Checkin row:rows){
            row.setStudentName(names.get(row.getStudentId()));
        }

        return Result.ok(rows);
    }
}
