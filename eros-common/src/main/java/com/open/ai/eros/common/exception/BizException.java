package com.open.ai.eros.common.exception;

import com.open.ai.eros.common.constants.BaseCode;
import lombok.Data;

@Data
public class BizException extends RuntimeException {

    private int code;

    private String msg;

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BizException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BizException(int code, String msg, Throwable throwable) {
        super(msg, throwable);
        this.code = code;
        this.msg = msg;
    }

    public BizException(BaseCode baseCode) {
        super(baseCode.getMsg());
        this.code = baseCode.getCode();
        this.msg = baseCode.getMsg();
    }

    public BizException(BaseCode baseCode, Throwable throwable) {
        super(baseCode.getMsg(), throwable);
        this.code = baseCode.getCode();
        this.msg = baseCode.getMsg();
    }
}
