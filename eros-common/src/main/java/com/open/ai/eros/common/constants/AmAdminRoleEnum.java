package com.open.ai.eros.common.constants;

public enum AmAdminRoleEnum {
    SYSTEM("system", "系统管理员"),
    MANAGER("manager", "管理员"),
    COMMON("common","普通用户");

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

    public static Boolean getByType(String type){
        for (AmAdminRoleEnum value : AmAdminRoleEnum.values()) {
            if (value.getType().equals(type)){
                return true;
            }
        }
        return false;
    }
}
