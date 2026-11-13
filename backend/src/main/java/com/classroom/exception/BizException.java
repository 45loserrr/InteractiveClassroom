package com.classroom.exception;

import com.classroom.utils.ResultCode;

/**
 * 业务异常：由 Service 层主动抛出，交给 {@link GlobalExceptionHandler} 统一转成
 * {@code {"code":xxx,"message":"..."}}，避免每个 Controller 写 try/catch。
 *
 * <p>使用原则：
 * <ul>
 *   <li>可预期的业务失败（课堂码错误、重复签到）→ 抛本异常；</li>
 *   <li>不可预期的系统故障（空指针、数据库断开）→ 不要包装，让它冒泡到全局 500 处理。</li>
 * </ul>
 *
 * @author M3 后端 API
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;

    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
