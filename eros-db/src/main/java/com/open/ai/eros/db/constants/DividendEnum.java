package com.open.ai.eros.db.constants;

public enum  DividendEnum {

    dividend(2),
    no_dividend(1);


    private final int dividendType;

    DividendEnum(int dividendType) {
        this.dividendType = dividendType;
    }


    public int getDividendType() {
        return dividendType;
    }
}
