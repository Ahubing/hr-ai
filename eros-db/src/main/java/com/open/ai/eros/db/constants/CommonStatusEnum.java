package com.open.ai.eros.db.constants;

import java.util.HashMap;
import java.util.Map;


public enum CommonStatusEnum {

    OK(1,"使用中"),
    DELETE(2,"下架"),
    ;


    private int status;
    private String desc;


    static Map<Integer, CommonStatusEnum> commonStatusEnumHashMap = new HashMap<>();
    static {
        for (CommonStatusEnum value : values()) {
            commonStatusEnumHashMap.put(value.status,value);
        }
    }


    /**
     * 获取状态的详细描述
     *
     * @param status
     * @return
     */
    public static String getDesc(Integer status){
        return commonStatusEnumHashMap.getOrDefault(status,DELETE).desc;
    }


    CommonStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
