package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 课堂表实体，对应数据库的 classroom 表（M2 负责）。
 * 表里只有 id / code / name / teacher_id 四列，
 * teacherName 是联表查出来的，数据库里没有，所以加 @TableField(exist = false)。
 */
@Data
@TableName("classroom")
public class Classroom {

    /** 主键，数据库自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 课堂口令：6 位大写字母 + 数字，学生凭它签到 */
    private String code;

    /** 课程名称 */
    private String name;

    /** 建课老师的 user.id */
    private Long teacherId;

    /** 老师姓名，数据库没有这一列，返回给前端用 */
    @TableField(exist = false)
    private String teacherName;
}
