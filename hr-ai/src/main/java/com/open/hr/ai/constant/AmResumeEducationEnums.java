package com.open.hr.ai.constant;

/**
 * @Author
 * @Date 2025/1/12 20:12
 */
public enum AmResumeEducationEnums {

    junior_college(1, "大专"),
    regular_college(2, "本科"),
    master_degree(3, "硕士"),
    doctor_degree(4, "博士");

    private Integer code;
    private String desc;

    AmResumeEducationEnums(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static AmResumeEducationEnums getByCode(Integer code) {
        for (AmResumeEducationEnums value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}

