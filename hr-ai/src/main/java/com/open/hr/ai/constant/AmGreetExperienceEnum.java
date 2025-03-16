package com.open.hr.ai.constant;

public enum AmGreetExperienceEnum {

    UM_LIMITED("unlimited","不限"),
    AT_SCHOOL("at_school","在校/应届"),
    // 一年以内
    // 1-3年
    // 3-5年
    // 5-10年
    // 10年以上
    ONE_YEAR("one_year","一年以内"),
    ONE_TO_THREE("one_to_three","1-3年"),
    THREE_TO_FIVE("three_to_five","3-5年"),
    FIVE_TO_TEN("five_to_ten","5-10年"),
    TEN_ABOVE("ten_above","10年以上");

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
