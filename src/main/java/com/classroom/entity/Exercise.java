package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 题目表实体，对应数据库的 exercise 表（M2 负责）。
 * options 存的是一个字符串，选项之间用竖线分隔，例如：A.80|B.443|C.8080|D.3306
 * answer 是正确答案，例如 A；学生端接口绝对不能返回这个字段。
 */
@Data
@TableName("exercise")
public class Exercise {

    /** 主键，数据库自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 属于哪个课堂 */
    private Long classroomId;

    /** 题干 */
    private String title;

    /** 选项，用 | 分隔 */
    private String options;

    /** 正确答案：A / B / C / D */
    private String answer;
}
