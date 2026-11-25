package com.classroom.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classroom.entity.Checkin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CheckinMapper extends BaseMapper<Checkin> {
    @Select("SELECT id FROM classroom WHERE id = #{classroomId} FOR UPDATE")
    Long lockClassroom(@Param("classroomId") Long classroomId);
}
