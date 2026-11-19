package com.example.classroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户表实体，对应数据库的 user 表。
 * 字段名是驼峰，数据库是下划线，MyBatis-Plus 会自动转换。
 */
@Data
@TableName("`user`")
public class User {

    /** 主键，数据库自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** 密码（大作业明文，够用就行） */
    private String password;

    /** 角色：teacher / student */
    private String role;

    /** 真实姓名 */
    private String realName;
}
