package com.open.ai.eros.db.constants;

import java.util.HashMap;
import java.util.Map;

public enum RightsTypeEnum {

    // 次数
    NUMBER("NUMBER","次数权益",UserRightsStatusEnum.USED),
    // 时间次数
    TIME_NUMBER("TIME_NUMBER","时间次数权益",UserRightsStatusEnum.ACTIVE),

    // 时间余额
    TIME_BALANCE("TIME_BALANCE","时间余额权益",UserRightsStatusEnum.ACTIVE),

    // 余额
    BALANCE("BALANCE","余额权益",UserRightsStatusEnum.USED),
    ;


    private String type;

    private String desc;

    private UserRightsStatusEnum initStatus;

    RightsTypeEnum(String type, String desc, UserRightsStatusEnum initStatus) {
        this.type = type;
        this.desc = desc;
        this.initStatus = initStatus;
    }

    static Map<String,RightsTypeEnum> RightsTypeEnumMap = new HashMap<>();

    static {
        for (RightsTypeEnum value : values()) {
            RightsTypeEnumMap.put(value.name(),value);
        }
    }


    /**
     * 是否存在
     *
     * @param type
     * @return
     */
    public static boolean exist(String type){
        RightsTypeEnum s = RightsTypeEnumMap.get(type);
        return s!=null;
    }


    public static String getDesc(String type){
        RightsTypeEnum s = RightsTypeEnumMap.get(type);
        if(s==null){
            return "";
        }
        return s.desc;
    }


    public static UserRightsStatusEnum getInitStatus(String type){
        RightsTypeEnum s = RightsTypeEnumMap.get(type);
        if(s==null){
            return null;
        }
        return s.initStatus;
    }


    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public UserRightsStatusEnum getInitStatus() {
        return initStatus;
    }
}
