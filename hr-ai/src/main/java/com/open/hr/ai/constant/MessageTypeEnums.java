package com.open.hr.ai.constant;

/**
 * @Author
 * @Date 2025/1/12 20:12
 */
public enum MessageTypeEnums {
    daily_greet(0, "打招呼每日任务"),
    temporary_greet(1, "临时任务"),
    rechat(2, "复聊");

    private Integer code;
    private String desc;

    MessageTypeEnums(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

