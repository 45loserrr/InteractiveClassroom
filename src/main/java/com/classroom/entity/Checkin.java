package com.classroom.entity;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@TableName ("checkin")
public class Checkin {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long classroomId;
    private Long studentId;

    @TableField (exist = false)
    private String studentName;

    @JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
