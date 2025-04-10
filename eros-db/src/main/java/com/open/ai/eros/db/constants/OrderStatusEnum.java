package com.open.ai.eros.db.constants;


import java.util.HashMap;
import java.util.Map;

// 订单状态 状态 1：待支付 2 支付成功 3：用户取消 4、超时取消 5、已完成 6：支付失败
public enum OrderStatusEnum {

    WAIT_PAY(1,"待支付"),
    PAY_SUCCESS(2,"支付成功"),
    USER_REMOVE(3,"用户取消"),
    TIME_OUT(4,"超时取消"),
    DONE(5,"已完成"),
    PAY_FAIL(6,"支付失败")
    ;

    private Integer status;
    private String desc;

    OrderStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    static Map<Integer,OrderStatusEnum> orderStatusEnumMap = new HashMap<>();
    static {
        for (OrderStatusEnum value : values()) {
            orderStatusEnumMap.put(value.status,value);
        }
    }

    public static boolean exist(Integer status){
        OrderStatusEnum orderStatusEnum = orderStatusEnumMap.get(status);
        if(orderStatusEnum==null){
            return false;
        }
        return true;
    }

    public static String getDesc(Integer status){
        OrderStatusEnum orderStatusEnum = orderStatusEnumMap.get(status);
        if(orderStatusEnum==null){
            return "未知状态";
        }
        return orderStatusEnum.getDesc();
    }


    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
