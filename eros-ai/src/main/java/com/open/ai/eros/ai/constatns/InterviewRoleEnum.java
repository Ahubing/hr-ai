package com.open.ai.eros.ai.constatns;


public enum InterviewRoleEnum {

    EMPLOYER(1, "招聘方"),
    EMPLOYEE(2, "受聘方");

    private final Integer code;
    private final String name;

    InterviewRoleEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
