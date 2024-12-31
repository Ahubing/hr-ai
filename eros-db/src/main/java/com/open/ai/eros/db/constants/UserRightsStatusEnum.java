package com.open.ai.eros.db.constants;


import java.util.HashMap;
import java.util.Map;

public enum UserRightsStatusEnum {

    ACTIVE(1, "生效中"),
    INACTIVE(2, "已失效"),
    USED(3, "已使用");


    private Integer status;

    private String desc;

    UserRightsStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }


    static Map<Integer, UserRightsStatusEnum> rightsStatusEnumHashMap = new HashMap<>();

    static {
        for (UserRightsStatusEnum value : values()) {
            rightsStatusEnumHashMap.put(value.status, value);
        }
    }


    /**
     * 是否存在
     *
     * @param status
     * @return
     */
    public static boolean exist(Integer status) {
        UserRightsStatusEnum s = rightsStatusEnumHashMap.get(status);
        return s != null;
    }


    public static String getDesc(Integer status) {
        UserRightsStatusEnum s = rightsStatusEnumHashMap.get(status);
        return s.desc;
    }


    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
