package com.example.classroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.classroom.entity.User;

/**
 * 用户 Mapper。
 * 继承 BaseMapper 后，增删改查方法全都自带，不用写一行 SQL。
 */
public interface UserMapper extends BaseMapper<User> {
}
