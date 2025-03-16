package com.open.hr.ai.constant;

public enum AmGreetDegreeEnum {

    //不限
    //初中及以下
    //中专/技校
    // 高中
    //大专
    //本科
    //硕士
    //博士
    //博士后
    UM_LIMITED("unlimited","不限"),
    JUNIOR("junior","初中及以下"),
    SECONDARY("secondary","中专/技校"),
    HIGH("high","高中"),
    JUNIOR_COLLEGE("junior_college","大专"),
    UNDERGRADUATE("undergraduate","本科"),
    MASTER("master","硕士"),
    DOCTOR("doctor","博士"),
    POST_DOCTOR("post_doctor","博士后")

  ;

    private final String type;
    private final String value;



    AmGreetDegreeEnum(String type, String value) {
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
