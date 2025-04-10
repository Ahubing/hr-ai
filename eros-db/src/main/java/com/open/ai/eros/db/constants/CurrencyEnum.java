//package com.open.ai.eros.db.constants;
//
//
//import java.util.HashMap;
//
///**
// * 币种枚举
// */
//public enum CurrencyEnum {
//    USD("USD", "美元"),
//    CNY("CNY", "人民币");
//
//    private final String value;
//    private final String label;
//
//
//    CurrencyEnum(String value, String label) {
//        this.value = value;
//        this.label = label;
//    }
//
//    private static HashMap<String, CurrencyEnum> data = new HashMap<>();
//
//    static {
//        for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
//            data.put(currencyEnum.getValue(), currencyEnum);
//        }
//    }
//
//    public static CurrencyEnum parse(String code) {
//        if (data.containsKey(code)) {
//            return data.get(code);
//        }
//        return null;
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public String getLabel() {
//        return label;
//    }
//}
