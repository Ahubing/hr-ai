package com.open.hr.ai.constant;

public enum AmGreetExperienceEnum {

    UM_LIMITED("不限","不限"),
    AT_SCHOOL("在校/应届","在校/应届"),
    // 一年以内
    // 1-3年
    // 3-5年
    // 5-10年
    // 10年以上
    ONE_YEAR("一年以内","一年以内"),
    ONE_TO_THREE("1-3年","1-3年"),
    THREE_TO_FIVE("3-5年","3-5年"),
    FIVE_TO_TEN("5-10年","5-10年"),
    TEN_ABOVE("10年以上","10年以上");

    private final String type;
    private final String value;



    AmGreetExperienceEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    // 获取枚举值
    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
