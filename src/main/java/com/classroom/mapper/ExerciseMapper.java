package com.classroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classroom.entity.Exercise;

/**
 * 题目表 Mapper（M2 负责）。
 * M4 判对错要读这张表（exercise.answer），他直接注入本接口查，不要改这个文件。
 */
public interface ExerciseMapper extends BaseMapper<Exercise> {
}
