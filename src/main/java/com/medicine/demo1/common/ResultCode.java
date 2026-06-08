package com.medicine.demo1.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "资源不存在"),
    USER_NOT_EXIST(1001, "用户不存在"),
    MEDICINE_NOT_EXIST(2001, "药品不存在"),
    INSUFFICIENT_QUANTITY(2002, "数量不足");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
