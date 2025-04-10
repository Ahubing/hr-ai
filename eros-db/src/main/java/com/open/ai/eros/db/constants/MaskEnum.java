package com.open.ai.eros.db.constants;

public enum MaskEnum {


    MASK("mask","伴侣",1),
    SHARE_MASK("share_mask","分享伴侣",1),
    KNOWLEDGE_MASK("knowledge_mask","知识伴侣",1),
    ;


    private String type;
    private String desc;
    private int show;


    MaskEnum(String type, String desc,int show) {
        this.type = type;
        this.desc = desc;
        this.show = show;
    }

    public int getShow() {
        return show;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
