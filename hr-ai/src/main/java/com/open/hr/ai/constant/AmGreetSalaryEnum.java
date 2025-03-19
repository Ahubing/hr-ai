package com.open.hr.ai.constant;

public enum AmGreetSalaryEnum {
    /**
     * 不限
     * 3k以下
     * 3-5k
     * 5-10k
     * 10-20k
     * 20-50k
     * 50k以上
     */
    UM_LIMITED("不限","不限"),
    THREE_K_BELOW("3k以下","3k以下"),
    THREE_TO_FIVE_K("3-5k","3-5k"),
    FIVE_TO_TEN_K("5-10k","5-10k"),
    TEN_TO_TWENTY_K("10-20k","10-20k"),
    TWENTY_TO_FIFTY_K("20-50k","20-50k"),
    FIFTY_ABOVE("50k以上","50k以上")
   ;

    private final String type;
    private final String value;



    AmGreetSalaryEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    // 获取枚举值
    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
