package com.classroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classroom.entity.Classroom;

/**
 * 课堂表 Mapper（M2 负责）。
 * 继承 BaseMapper 就有增删改查，不用写 SQL。
 * 启动类上已有 @MapperScan("com.classroom.mapper")，这里不用再加 @Mapper。
 */
public interface ClassroomMapper extends BaseMapper<Classroom> {
}
