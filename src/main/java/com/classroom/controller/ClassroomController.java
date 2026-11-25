package com.classroom.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.classroom.common.Result;
import com.classroom.entity.Classroom;
import com.classroom.entity.User;
import com.classroom.mapper.ClassroomMapper;
import com.classroom.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 建课相关接口（M2 负责）。
 *
 * POST /api/classroom          建课，生成 6 位课堂口令
 * GET  /api/classroom/list     课堂列表（可按 teacherId 过滤）
 * GET  /api/classroom/byCode   按口令查课堂（给 M1 签到用）
 *
 * 注意：接口返回统一走 Result，字段一律小驼峰，别乱改（契约第 7 节奶茶条款）。
 */
@RestController
@RequestMapping("/api/classroom")
public class ClassroomController {

    /**
     * 口令字符集：去掉容易看错的 I、O、0、1，
     * 学生手输口令时不会把 O 打成 0。
     */
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /** 口令长度，契约要求 6 位 */
    private static final int CODE_LENGTH = 6;

    /** 生成口令时最多重试次数，防止极小概率一直撞码 */
    private static final int CODE_RETRY = 12;

    @Autowired
    private ClassroomMapper classroomMapper;

    /** 查老师姓名要用 PM 的 UserMapper，只调用，不修改 */
    @Autowired
    private UserMapper userMapper;

    /**
     * 建课。
     * 请求：{ "name": "软件工程", "teacherId": 1 }
     * 返回：{ id, code, name, teacherId, teacherName }
     */
    @PostMapping
    public Result create(@RequestBody Classroom classroom) {
        String name = trim(classroom.getName());
        if (name.isEmpty()) {
            return Result.error("课程名称不能为空");
        }
        if (name.length() > 100) {
            return Result.error("课程名称最多 100 个字");
        }

        Long teacherId = classroom.getTeacherId();
        if (teacherId == null) {
            return Result.error("老师 id 不能为空，请重新登录");
        }

        // 老师必须是数据库里真实存在的 teacher 账号，防止前端乱传 id
        User teacher = userMapper.selectById(teacherId);
        if (teacher == null) {
            return Result.error("老师账号不存在，请重新登录");
        }
        if (!"teacher".equals(teacher.getRole())) {
            return Result.error("只有老师才能建课");
        }

        // 生成一个数据库里还没用过的 6 位口令
        String code = generateUniqueCode();
        if (code == null) {
            return Result.error("课堂口令生成失败，请重试");
        }

        Classroom row = new Classroom();
        row.setName(name);
        row.setCode(code);
        row.setTeacherId(teacherId);
        classroomMapper.insert(row);

        // teacherName 数据库没有，返回前手动补上
        row.setTeacherName(displayName(teacher));

        return Result.ok("创建成功", row);
    }

    /**
     * 课堂列表。
     * teacherId 不传就查全部，传了就只查这个老师的课。
     */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) Long teacherId) {
        QueryWrapper<Classroom> wrapper = new QueryWrapper<>();
        if (teacherId != null) {
            wrapper.eq("teacher_id", teacherId);
        }
        // 新建的课排前面，老师一眼能看到刚才建的
        wrapper.orderByDesc("id");

        List<Classroom> rows = classroomMapper.selectList(wrapper);
        fillTeacherNames(rows);
        return Result.ok(rows);
    }

    /**
     * 按口令查课堂，M1 的签到页在签到前先调这个接口。
     * 查不到就返回 400 + 人话提示，前端 myFetch 会直接 alert 出来。
     */
    @GetMapping("/byCode")
    public Result byCode(@RequestParam(required = false) String code) {
        String normalized = trim(code).toUpperCase();
        if (normalized.isEmpty()) {
            return Result.error("课堂口令不能为空");
        }

        QueryWrapper<Classroom> wrapper = new QueryWrapper<>();
        wrapper.eq("code", normalized);
        List<Classroom> rows = classroomMapper.selectList(wrapper);

        if (rows.isEmpty()) {
            return Result.error("课堂口令不存在，检查一下再试");
        }

        Classroom row = rows.get(0);
        fillTeacherNames(rows);
        return Result.ok(row);
    }

    // ---------------- 私有工具方法 ----------------

    /** 随机生成一个当前数据库里没被占用的口令 */
    private String generateUniqueCode() {
        for (int i = 0; i < CODE_RETRY; i++) {
            String candidate = randomCode();
            QueryWrapper<Classroom> wrapper = new QueryWrapper<>();
            wrapper.eq("code", candidate);
            Long count = classroomMapper.selectCount(wrapper);
            if (count == null || count == 0) {
                return candidate;
            }
        }
        return null;
    }

    private String randomCode() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = ThreadLocalRandom.current().nextInt(CODE_CHARS.length());
            builder.append(CODE_CHARS.charAt(index));
        }
        return builder.toString();
    }

    /** 批量补老师姓名，避免一条一条查 */
    private void fillTeacherNames(List<Classroom> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        Set<Long> teacherIds = rows.stream()
                .map(Classroom::getTeacherId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (teacherIds.isEmpty()) {
            return;
        }

        Map<Long, String> names = new HashMap<>();
        for (User user : userMapper.selectBatchIds(teacherIds)) {
            names.put(user.getId(), displayName(user));
        }
        for (Classroom row : rows) {
            row.setTeacherName(names.getOrDefault(row.getTeacherId(), ""));
        }
    }

    /** 姓名没填就用账号名兜底，保证前端不会显示 null */
    private String displayName(User user) {
        if (user == null) {
            return "";
        }
        String realName = user.getRealName();
        return (realName == null || realName.trim().isEmpty()) ? user.getUsername() : realName.trim();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
