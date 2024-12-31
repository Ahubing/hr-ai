package com.open.ai.eros.db.constants;

import java.util.HashMap;
import java.util.Map;


public enum MaskStatusEnum {

    OK(1, "发布"),
    WAIT(2, "待发布"),
    DELETE(3, "删除");

//     1 发布 2 待发布 3：删除


    private int status;
    private String desc;


    static Map<Integer, MaskStatusEnum> maskMap = new HashMap<>();

    static {
        for (MaskStatusEnum value : values()) {
            maskMap.put(value.status, value);
        }
    }


    /**
     * 获取状态的详细描述
     *
     * @param status
     * @return
     */
    public static String getDesc(Integer status) {
        return maskMap.getOrDefault(status, DELETE).desc;
    }


    MaskStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
