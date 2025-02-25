package com.open.hr.ai.constant;

public enum InterviewStatusEnum {

    NOT_CANCEL(1, "未取消"),
    CANCEL(2, "已取消");

    private Integer status;
    private String desc;

    InterviewStatusEnum(Integer status, String desc) {
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
