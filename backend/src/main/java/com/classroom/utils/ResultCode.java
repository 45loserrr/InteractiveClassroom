package com.classroom.utils;

/**
 * 业务错误码字典。
 *
 * <p>与 {@code docs/04-接口契约模板.md §0.3} 严格保持一致：新增错误码必须先改文档再改这里。
 * 约定：0 成功；4xx/5xx 与 HTTP 语义对齐；1xxx 课堂、2xxx 答题、3xxx 抢答。
 *
 * @author M3 后端 API
 */
public enum ResultCode {

    /** 成功 */
    SUCCESS(0, "success"),

    /** 参数校验失败 */
    BAD_REQUEST(400, "参数不合法"),

    /** 未登录或 Token 失效 */
    UNAUTHORIZED(401, "请先登录"),

    /** 已登录但无权限（如学生调教师接口） */
    FORBIDDEN(403, "无权限执行该操作"),

    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),

    /** 重复提交（通用兜底，具体业务请用下方专用码） */
    CONFLICT(409, "请勿重复提交"),

    /** 服务器内部错误 */
    SERVER_ERROR(500, "服务器开小差了，请稍后重试"),

    // ---------- 1xxx 课堂 ----------
    /** 课堂码不存在 */
    CLASS_CODE_NOT_FOUND(1001, "课堂码不存在，请核对后重试"),

    /** 课堂已结束 */
    CLASS_CLOSED(1002, "该课堂已结束"),

    /** 重复签到 */
    DUPLICATE_CHECKIN(1003, "你已签到，无需重复操作"),

    /** 签到窗口已关闭 */
    CHECKIN_CLOSED(1004, "签到已结束"),

    // ---------- 2xxx 答题 ----------
    /** 已提交过答案 */
    DUPLICATE_ANSWER(2001, "你已提交过本题"),

    /** 题目未发布或已截止 */
    EXERCISE_NOT_OPEN(2002, "题目未发布或已截止"),

    // ---------- 3xxx 抢答 ----------
    /** 抢答未开始或已结束 */
    RACE_NOT_OPEN(3001, "抢答未开始或已结束"),

    /** 重复抢答 */
    DUPLICATE_RACE(3002, "你已参与过本次抢答");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
