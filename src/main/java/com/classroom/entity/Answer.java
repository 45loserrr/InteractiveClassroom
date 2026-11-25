package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 答题表实体，对应数据库 answer 表。
 * 字段名驼峰，数据库下划线，MyBatis-Plus 自动转换。
 */
@Data
@TableName("`answer`")
public class Answer {

    /** 主键，数据库自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 题目 id */
    private Long exerciseId;

    /** 学生 user.id */
    private Long studentId;

    /** 学生选的答案，例如 A */
    private String chooseOption;
}
