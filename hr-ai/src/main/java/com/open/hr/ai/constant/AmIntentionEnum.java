package com.open.hr.ai.constant;

public enum AmIntentionEnum {
    /**
     * 不限
     * 离职/离校-正在找工作，
     * 在职/在校-考虑机会，
     * 在职/在校-寻找新工作
     */
    UM_LIMITED(-1,"不限"),
    LEAVING_JOB(0,"离职/离校-正在找工作"),
    CONSIDERING_OPPORTUNITIES(1,"在职/在校-考虑机会"),
    LOOKING_FOR_NEW_JOB(2,"在职/在校-寻找新工作")
   ;

    private final Integer type;
    private final String value;



    AmIntentionEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    // 获取枚举值
    public Integer getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    /**
     * 通过type查询value
     */
    public static String getValueByType(Integer type) {
        for (AmIntentionEnum value : AmIntentionEnum.values()) {
            if (value.getType().equals(type)) {
                return value.getValue();
            }
        }
        return null;
    }
}
