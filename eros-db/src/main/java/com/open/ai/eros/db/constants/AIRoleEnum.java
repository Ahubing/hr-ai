package com.open.ai.eros.db.constants;

public enum AIRoleEnum {

    /**
     * system
     */
    SYSTEM("system"),
    ASSISTANT("assistant"),
    MODEL("model"),
    ASSOCIATION("association"), //标识 联想的消息 不拉入上下文
    USER("user");

    private final String roleName;

    AIRoleEnum(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
