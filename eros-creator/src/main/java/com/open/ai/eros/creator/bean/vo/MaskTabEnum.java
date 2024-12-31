package com.open.ai.eros.creator.bean.vo;

public enum MaskTabEnum {

    //ALL("all","全部"),

    HEAT("heat","总榜"),
    MONTH("month","月榜"),
    WEEK("week","周榜"),
    DAY("day","天榜"),
    NEW("new","最新"),
    ;


    private String type;
    private String desc;


    MaskTabEnum(String type, String desc) {
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
