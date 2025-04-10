package com.open.ai.eros.db.constants;


import java.util.HashMap;
import java.util.Map;

/**
 * 计费类型 1：额度 2：次数
 */
public enum CostTypeEnum {



    BALANCE(1,"余额"),
    NUMBER(2,"次数"),
    ;


    private int status;
    private String desc;


    static Map<Integer, CostTypeEnum> costTypeEnumHashMap = new HashMap<>();
    static {
        for (CostTypeEnum value : values()) {
            costTypeEnumHashMap.put(value.status,value);
        }
    }


    /**
     * 获取状态的详细描述
     *
     * @param status
     * @return
     */
    public static String getDesc(Integer status){
        return costTypeEnumHashMap.getOrDefault(status,BALANCE).desc;
    }


    CostTypeEnum(int status, String desc) {
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
