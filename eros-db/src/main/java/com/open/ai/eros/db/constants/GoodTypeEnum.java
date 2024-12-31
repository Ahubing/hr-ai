package com.open.ai.eros.db.constants;

import com.open.ai.eros.common.exception.BizException;

import java.util.HashMap;
import java.util.Map;

/**
 * @类名：GoodTypeEnum
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/23 22:39
 */
public enum GoodTypeEnum {

    RIGHTS("rights","权益"),
    COMMON("common","普通");

    private String type;
    private String desc;


    static Map<String,GoodTypeEnum> goodTypeEnumMap = new HashMap<>();

    static {
        for (GoodTypeEnum value : values()) {
            goodTypeEnumMap.put(value.getType(),value);
        }
    }

    public static String getDesc(String type){

        GoodTypeEnum goodTypeEnum = goodTypeEnumMap.get(type);
        if(goodTypeEnum==null){
            throw new BizException("未知商品类型！");
        }
        return goodTypeEnum.getDesc();
    }


    public static boolean exist(String type){
        GoodTypeEnum goodTypeEnum = goodTypeEnumMap.get(type);
        if(goodTypeEnum==null){
            return false;
        }
        return true;
    }



    GoodTypeEnum(String type, String desc) {
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
