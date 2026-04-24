package com.attendance.server.shared.exception;

/**
 * 涓氬姟寮傚父锛屾壙杞藉彲鐩存帴鏄犲皠缁欏鎴风鐨勯敊璇爜涓庨敊璇俊鎭€? */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
