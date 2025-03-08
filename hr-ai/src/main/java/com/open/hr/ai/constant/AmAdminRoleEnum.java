package com.open.hr.ai.constant;

public enum AmAdminRoleEnum {

    //普通用户
    //管理员
    //会员
    COMMON("common","普通用户"),
    ADMIN("admin","管理员"),
    VIP("vip","会员");

    private final String type;
    private final String desc;



    AmAdminRoleEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    // 获取枚举值
    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
