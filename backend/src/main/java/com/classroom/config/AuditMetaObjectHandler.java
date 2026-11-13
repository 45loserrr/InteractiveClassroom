package com.classroom.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充：实体里的 {@code createdAt} / {@code updatedAt} 无需手写赋值。
 *
 * <p>实体字段需配合注解使用：
 * <pre>{@code
 * @TableField(fill = FieldFill.INSERT)
 * private LocalDateTime createdAt;
 *
 * @TableField(fill = FieldFill.INSERT_UPDATE)
 * private LocalDateTime updatedAt;
 * }</pre>
 *
 * <p>数据库层仍保留 {@code DEFAULT CURRENT_TIMESTAMP} 兜底，双保险。
 *
 * @author M3 后端 API
 */
@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, CREATED_AT, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, UPDATED_AT, LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, UPDATED_AT, LocalDateTime.class, LocalDateTime.now());
    }
}
