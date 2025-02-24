package com.open.hr.ai.constant;

public enum InterviewTypeEnum {

    POSITION_CLOSE("single"),
    GROUP("group");

    private final String code;

    InterviewTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
