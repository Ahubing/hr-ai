package com.open.ai.eros.db.constants;

import com.open.ai.eros.common.exception.BizException;

import java.util.HashMap;
import java.util.Map;

public enum ExchangeCodeTypeEnum {



    RIGHTS("rights","权益兑换码"),
    BALANCE("balance","余额兑换码");

    private String type;
    private String desc;


    static Map<String,ExchangeCodeTypeEnum> exchangeCodeTypeEnumHashMap = new HashMap<>();

    static {
        for (ExchangeCodeTypeEnum value : values()) {
            exchangeCodeTypeEnumHashMap.put(value.getType(),value);
        }
    }

    public static String getDesc(String type){

        ExchangeCodeTypeEnum exchangeCodeTypeEnum = exchangeCodeTypeEnumHashMap.get(type);
        if(exchangeCodeTypeEnum==null){
            throw new BizException("未知商品类型！");
        }
        return exchangeCodeTypeEnum.getDesc();
    }


    public static boolean exist(String type){
        ExchangeCodeTypeEnum exchangeCodeTypeEnum = exchangeCodeTypeEnumHashMap.get(type);
        if(exchangeCodeTypeEnum==null){
            return false;
        }
        return true;
    }



    ExchangeCodeTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }


}
