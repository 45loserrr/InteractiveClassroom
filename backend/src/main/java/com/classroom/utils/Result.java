package com.classroom.utils;

/**
 * 统一响应体：{@code {"code":0,"message":"success","data":{}}}。
 *
 * <p>约定见 {@code docs/04-接口契约模板.md §0.2}：
 * <ul>
 *   <li>前端只判断 {@code code == 0}，不解析 HTTP 状态码做业务分支；</li>
 *   <li>{@code message} 必须是可以直接 toast 的中文提示；</li>
 *   <li>失败时 {@code data} 为 null（全局配置 {@code default-property-inclusion: non_null} 会省略该字段）。</li>
 * </ul>
 *
 * <p>用法：
 * <pre>{@code
 * return Result.ok(data);                          // 成功带数据
 * return Result.ok();                              // 成功无数据
 * return Result.fail(ResultCode.DUPLICATE_CHECKIN); // 业务失败
 * }</pre>
 *
 * @param <T> 业务数据类型
 * @author M3 后端 API
 */
public class Result<T> {

    /** 成功码 */
    public static final int SUCCESS_CODE = 0;

    private int code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ---------------- 成功 ----------------

    public static <T> Result<T> ok() {
        return new Result<>(SUCCESS_CODE, "success", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(SUCCESS_CODE, "success", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    // ---------------- 失败 ----------------

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    /**
     * 占位返回：骨架阶段使用，业务实现后应替换为真实结果。
     *
     * @return code=0，message=TODO
     */
    public static <T> Result<T> todo() {
        return new Result<>(SUCCESS_CODE, "TODO", null);
    }

    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }

    // ---------------- Getter / Setter ----------------

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
