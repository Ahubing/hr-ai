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
    UM_LIMITED("unlimited","不限"),
    THREE_K_BELOW("three_k_below","3k以下"),
    THREE_TO_FIVE_K("three_to_five_k","3-5k"),
    FIVE_TO_TEN_K("five_to_ten_k","5-10k"),
    TEN_TO_TWENTY_K("ten_to_twenty_k","10-20k"),
    TWENTY_TO_FIFTY_K("twenty_to_fifty_k","20-50k"),
    FIFTY_ABOVE("fifty_above","50k以上")
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
