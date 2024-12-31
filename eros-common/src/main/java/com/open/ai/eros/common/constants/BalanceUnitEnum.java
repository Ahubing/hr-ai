package com.open.ai.eros.common.constants;

public enum BalanceUnitEnum {
    CNY("CNY","元"),
    DOLLAR("USD","美元");

    private String unit;
    private String desc;

    BalanceUnitEnum(String unit, String desc) {
        this.unit = unit;
        this.desc = desc;
    }

    public String getUnit() {
        return unit;
    }

    public String getDesc() {
        return desc;
    }
}
