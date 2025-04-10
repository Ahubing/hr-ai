package com.open.ai.eros.common.constants;

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
