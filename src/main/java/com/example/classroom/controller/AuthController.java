package com.example.classroom.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.classroom.common.Result;
import com.example.classroom.entity.User;
import com.example.classroom.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 登录 / 注册接口。
 * 地址都以 /api/auth 开头，拦截器会放行这一组。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录：账号密码正确就发一个 UUID 当 Token。
     * 这里不搞 JWT，Token 也不存数据库，前端带着就行（大作业够用）。
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        String username = user.getUsername() == null ? "" : user.getUsername().trim();
        String password = user.getPassword() == null ? "" : user.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            return Result.error("账号和密码不能为空");
        }

        // 去数据库里查：账号 + 密码都对上才算登录成功
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("password", password);
        User dbUser = userMapper.selectOne(wrapper);

        if (dbUser == null) {
            return Result.error("账号或密码错误");
        }

        // 生成 Token：一串随机字符，前端存到 localStorage
        String token = UUID.randomUUID().toString().replace("-", "");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", dbUser.getId());
        data.put("username", dbUser.getUsername());
        data.put("role", dbUser.getRole());
        data.put("realName", dbUser.getRealName());

        return Result.ok("登录成功", data);
    }

    /**
     * 注册：最简单的插入。
     * role 不填默认 student，realName 不填就用账号名。
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        String username = user.getUsername() == null ? "" : user.getUsername().trim();
        String password = user.getPassword() == null ? "" : user.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            return Result.error("账号和密码不能为空");
        }

        // 账号不能重复
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        Long count = userMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return Result.error("用户名已存在，换一个吧");
        }

        String role = user.getRole() == null ? "" : user.getRole().trim();
        if (role.isEmpty()) {
            role = "student";
        }
        if (!"teacher".equals(role) && !"student".equals(role)) {
            return Result.error("角色只能是 teacher 或 student");
        }

        String realName = user.getRealName() == null ? "" : user.getRealName().trim();
        if (realName.isEmpty()) {
            realName = username;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole(role);
        newUser.setRealName(realName);
        userMapper.insert(newUser);

        return Result.ok("注册成功", null);
    }
}
