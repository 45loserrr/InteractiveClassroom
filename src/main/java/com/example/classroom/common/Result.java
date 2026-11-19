package com.example.classroom.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回格式：{ code, msg, data }
 * code = 200 表示成功，其他都算失败。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    /** 200 成功；400 业务失败；401 没登录；500 服务器错误 */
    private Integer code;

    /** 给前端看的提示文字 */
    private String msg;

    /** 真正的数据，可以是对象、数组，也可以为 null */
    private Object data;

    public static Result ok(Object data) {
        return new Result(200, "成功", data);
    }

    public static Result ok(String msg, Object data) {
        return new Result(200, msg, data);
    }

    public static Result error(String msg) {
        return new Result(400, msg, null);
    }
}
