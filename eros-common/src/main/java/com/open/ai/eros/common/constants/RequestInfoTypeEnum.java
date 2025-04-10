package com.open.ai.eros.common.constants;

public enum RequestInfoTypeEnum {
    WECHAT("wechat", "微信"),
    PHONE("phone", "手机"),
    ATTACHMENT_RESUME("attachment_resume", "附件简历");

    private final String type;
    private final String desc;



    RequestInfoTypeEnum(String type, String desc) {
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
        for (RequestInfoTypeEnum value : RequestInfoTypeEnum.values()) {
            if (value.getType().equals(type)){
                return true;
            }
        }
        return false;
    }
}
