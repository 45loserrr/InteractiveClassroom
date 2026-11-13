package com.classroom.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置。
 *
 * <ul>
 *   <li><b>分页插件</b>：排行榜、签到名单、学情报告都需要分页，M3 直接写
 *       {@code Page<T> page = new Page<>(1, 10)} 即可；</li>
 *   <li><b>防全表更新/删除</b>：拦截没有 where 条件的 update/delete，避免误操作清库。</li>
 * </ul>
 *
 * @author M3 后端 API
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页：单页上限 200，防止前端传 size=999999 拖垮数据库
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setMaxLimit(200L);
        pagination.setOverflow(false);
        interceptor.addInnerInterceptor(pagination);

        // 防止误写全表
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }
}
