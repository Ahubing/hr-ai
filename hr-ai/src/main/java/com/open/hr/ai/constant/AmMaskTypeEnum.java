package com.open.hr.ai.constant;

public enum AmMaskTypeEnum {
    JAVA("java", "java"),
    BACKEND("backend", "后端"),
    PYTHON("python", "python"),
    C_SHARP("c#", "c#"),
    PHP("php", "php"),
    DISTRIBUTED("distributed","分布式"),
    NGINX("nginx","nginx"),
    HR("HR","人力资源"),
    SALES("sales","销售"),
    OPERATIONS("operations","运维"),
    ASSISTANT("assistant","助理"),
    CHEMISTRY("chemistry","化学"),
    PHYSICS("physics","物理");

    private final String type;
    private final String desc;



    AmMaskTypeEnum(String type, String desc) {
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
}
