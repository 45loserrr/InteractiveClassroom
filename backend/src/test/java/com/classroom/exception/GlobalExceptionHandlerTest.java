package com.classroom.exception;

import com.classroom.utils.Result;
import com.classroom.utils.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * 全局异常处理单元测试：不启动 Spring 容器，直接调用处理器方法。
 *
 * <p>保证"错误码 → 前端提示"的映射不被后续改动悄悄破坏。
 *
 * @author M3 后端 API
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("业务异常：错误码与中文提示原样透传（重复签到 1003）")
    void shouldPassThroughBizException() {
        Result<Void> result = handler.handleBizException(new BizException(ResultCode.DUPLICATE_CHECKIN));

        assertEquals(1003, result.getCode());
        assertEquals("你已签到，无需重复操作", result.getMessage());
    }

    @Test
    @DisplayName("业务异常：允许自定义提示（课堂码错误）")
    void shouldKeepCustomMessage() {
        Result<Void> result = handler.handleBizException(
                new BizException(ResultCode.CLASS_CODE_NOT_FOUND, "课堂码 000000 不存在"));

        assertEquals(1001, result.getCode());
        assertEquals("课堂码 000000 不存在", result.getMessage());
    }

    @Test
    @DisplayName("唯一索引冲突：转成 409，不让数据库异常直接暴露给前端")
    void shouldMapDuplicateKeyToConflict() {
        Result<Void> result = handler.handleDuplicateKey(
                new DuplicateKeyException("Duplicate entry '12-101' for key 'uk_checkin_class_student'"));

        assertEquals(ResultCode.CONFLICT.getCode(), result.getCode());
        assertEquals(ResultCode.CONFLICT.getMessage(), result.getMessage());
    }

    @Test
    @DisplayName("未知异常：统一 500，且不把原始异常信息返回给前端")
    void shouldHideInternalMessage() {
        Result<Void> result = handler.handleException(new IllegalStateException("数据库连接池已耗尽"));

        assertEquals(500, result.getCode());
        assertEquals(ResultCode.SERVER_ERROR.getMessage(), result.getMessage());
        assertFalse(result.getMessage().contains("数据库连接池"));
        assertNotEquals("数据库连接池已耗尽", result.getMessage());
    }
}
