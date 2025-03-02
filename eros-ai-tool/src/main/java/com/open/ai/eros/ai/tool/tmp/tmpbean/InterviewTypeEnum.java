package com.open.ai.eros.ai.tool.tmp.tmpbean;

public enum InterviewTypeEnum {

    SINGLE("single"),
    GROUP("group");

    private final String code;

    InterviewTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
