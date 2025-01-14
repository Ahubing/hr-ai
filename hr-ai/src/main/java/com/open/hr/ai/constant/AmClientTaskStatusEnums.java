package com.open.hr.ai.constant;

/**
 * @Author liuzilin
 * @Date 2025/1/12 20:12
 */
public enum AmClientTaskStatusEnums {
    NOT_START(0,"未开始"),
    START(1,"开始"),
    FINISH(2,"已完成"),
    FAILURE(3,"失败");

    private Integer status;
    private String desc;

    AmClientTaskStatusEnums(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

