package com.open.hr.ai.constant;

public enum AmGreetDegreeEnum {

    /**
     *  0初中及以下，
     *  1中专/技校，
     *  2高中，
     *  3大专，
     *  4本科，
     *  5硕士，
     *  6博士,
     *  -1未知
     */
    UM_LIMITED(-1,"不限"),
    JUNIOR_HIGH_SCHOOL(0,"初中及以下"),
    SECONDARY_VOCATIONAL_SCHOOL(1,"中专/技校"),
    HIGH_SCHOOL(2,"高中"),
    JUNIOR_COLLEGE(3,"大专"),
    UNDERGRADUATE(4,"本科"),
    MASTER(5,"硕士"),
    DOCTOR(6,"博士");

    private final Integer type;
    private final String value;



    AmGreetDegreeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    // 获取枚举值
    public Integer getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    // 根据type查询value
    public static String getValueByType(Integer type) {
        for (AmGreetDegreeEnum value : AmGreetDegreeEnum.values()) {
            if (value.getType().equals(type)) {
                return value.getValue();
            }
        }
        return null;
    }
}
