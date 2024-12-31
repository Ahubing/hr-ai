package com.open.ai.eros.creator.bean;

import java.util.HashMap;
import java.util.Map;

public enum RankTypeEnum {

    hour("hour"),
    day("day"),
    week("week"),
    month("month");

    static Map<String,RankTypeEnum> rankTypeEnumMap = new HashMap<>();
    static {
        for (RankTypeEnum value : RankTypeEnum.values()) {
            rankTypeEnumMap.put(value.getKey(),value);
        }
    }

    public static boolean exist(String key){
        return rankTypeEnumMap.get(key)!=null;
    }

    RankTypeEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    private String key;
}
