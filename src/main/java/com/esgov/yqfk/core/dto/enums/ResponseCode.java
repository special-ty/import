package com.esgov.yqfk.core.dto.enums;

/**
 * 描述: 统一响应码（与原项目口径一致：0 成功，-1 错误）
 */
public enum ResponseCode {
    SUCCESS(0, "成功"),
    ERROR(-1, "错误");

    private final int code;
    private final String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
