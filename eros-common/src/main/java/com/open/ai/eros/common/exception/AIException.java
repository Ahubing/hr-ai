package com.open.ai.eros.common.exception;

import com.open.ai.eros.common.constants.BaseCode;
import lombok.Data;

@Data
public class AIException extends RuntimeException {

    private int code;

    private String msg;

    public AIException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public AIException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public AIException(int code, String msg, Throwable throwable) {
        super(msg, throwable);
        this.code = code;
        this.msg = msg;
    }

    public AIException(BaseCode baseCode) {
        super(baseCode.getMsg());
        this.code = baseCode.getCode();
        this.msg = baseCode.getMsg();
    }

    public AIException(BaseCode baseCode, Throwable throwable) {
        super(baseCode.getMsg(), throwable);
        this.code = baseCode.getCode();
        this.msg = baseCode.getMsg();
    }
}
