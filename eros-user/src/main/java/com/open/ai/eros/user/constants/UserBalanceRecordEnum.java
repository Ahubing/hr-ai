package com.open.ai.eros.user.constants;

import java.util.HashMap;
import java.util.Map;

public enum UserBalanceRecordEnum {

    SYSTEM_UPDATE_BALANCE("system_update","管理员修改余额"),
    INVITATION_NEW_USER_BALANCE("invitation_user","邀请新人"),
    MASK_CHAT_BALANCE("mask_chat","创作分红"),
    EXCHANGE_CODE("exchange_code","兑换码"),
    BUY_GOODS("buy_goods","购买商品")
    ;


    private String type;
    private String desc;


    static Map<String,UserBalanceRecordEnum> userBalanceRecordEnumMap = new HashMap<>();

    static {
        for (UserBalanceRecordEnum userBalanceRecordEnum : values()) {
            userBalanceRecordEnumMap.put(userBalanceRecordEnum.getType(),userBalanceRecordEnum);
        }
    }

    public static String getDesc(String type){
        UserBalanceRecordEnum userBalanceRecordEnum = userBalanceRecordEnumMap.get(type);
        if(userBalanceRecordEnum==null){
            return "未知";
        }
        return userBalanceRecordEnum.getDesc();
    }


    UserBalanceRecordEnum(String type, String desc) {
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
