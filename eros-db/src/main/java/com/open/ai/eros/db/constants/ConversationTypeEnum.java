package com.open.ai.eros.db.constants;

public enum ConversationTypeEnum {

    MASK(1),
    KNOWLEDGE(2),
    COMMON(3)
    ;


    private Integer type;

    ConversationTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
