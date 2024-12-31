package com.open.ai.eros.db.constants;

public enum MaskTypeEnum {

    STORY("story","故事"),
    TOOL("tool","工具"),
    COPY_WRITING("copy_writing","文案"),
    TRANSLATE("translate","翻译"),
    CODE("code","编码"),
    DATA_PROCESSING("data_processing","数据处理"),
    GAMING("gaming","游戏"),
    COMMON("common ","常识"),
    EMOTIONAL("emotional ","情感"),
    INTERESTS("interests","趣味")
    ;


    private String type;
    private String desc;


    MaskTypeEnum(String type, String desc) {
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
