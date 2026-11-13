package com.classroom.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 统一响应体单元测试：不依赖 Spring 容器与数据库，{@code mvn test} 秒级通过。
 *
 * <p>骨架阶段用它保证"响应结构一旦被改动，CI 立刻发现"。
 *
 * @author M3 后端 API
 */
class ResultTest {

    @Test
    @DisplayName("成功响应：code=0、message=success、data 为空")
    void shouldReturnSuccessWithoutData() {
        Result<Void> result = Result.ok();

        assertEquals(Result.SUCCESS_CODE, result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getData());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("成功响应：携带业务数据")
    void shouldCarryData() {
        Result<String> result = Result.ok("classroom-42");

        assertEquals(0, result.getCode());
        assertEquals("classroom-42", result.getData());
    }

    @Test
    @DisplayName("失败响应：透传业务错误码与中文提示（重复签到 1003）")
    void shouldReturnFailCodeFromEnum() {
        Result<Void> result = Result.fail(ResultCode.DUPLICATE_CHECKIN);

        assertEquals(1003, result.getCode());
        assertEquals("你已签到，无需重复操作", result.getMessage());
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("失败响应：允许覆盖提示文案")
    void shouldOverrideMessage() {
        Result<Void> result = Result.fail(ResultCode.BAD_REQUEST, "课堂码必须是 6 位数字");

        assertEquals(400, result.getCode());
        assertEquals("课堂码必须是 6 位数字", result.getMessage());
    }

    @Test
    @DisplayName("占位响应：骨架阶段统一为 code=0 + TODO")
    void shouldReturnTodo() {
        Result<Void> result = Result.todo();

        assertEquals(0, result.getCode());
        assertEquals("TODO", result.getMessage());
    }

    @Test
    @DisplayName("错误码字典：与 docs/04 §0.3 对齐，不允许重复")
    void resultCodesShouldBeUnique() {
        long distinct = java.util.Arrays.stream(ResultCode.values())
                .map(ResultCode::getCode)
                .distinct()
                .count();

        assertEquals(ResultCode.values().length, distinct);
    }
}
