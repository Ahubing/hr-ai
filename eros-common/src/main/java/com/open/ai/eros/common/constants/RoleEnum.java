package com.open.ai.eros.common.constants;

import java.util.HashMap;

public enum RoleEnum {
    COMMON("user","普通用户"),
    CREATOR("creator","创作者"),
    SYSTEM("system","系统管理");
    private final String role;
    private final String desc;

    RoleEnum(String role, String desc) {
        this.role = role;
        this.desc = desc;
    }

    public String getRole() {
        return role;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByRole(String role){
        for (RoleEnum value : RoleEnum.values()) {
            if(value.role.equals(role)){
                return value.getDesc();
            }
        }
        return COMMON.getDesc();
    }


    static HashMap<String,RoleEnum> roleMap = new HashMap<>();
    static {
        for (RoleEnum value : RoleEnum.values()) {
            roleMap.put(value.role, value);
        }
    }

    public static RoleEnum getByRole(String role){
        return roleMap.getOrDefault(role,COMMON);
    }

    public static RoleEnum getByDesc(String desc){
        for (RoleEnum value : RoleEnum.values()) {
            if(value.desc.equals(desc)){
                return value;
            }
        }
        return COMMON;
    }

}
