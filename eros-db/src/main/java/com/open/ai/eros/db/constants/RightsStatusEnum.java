package com.open.ai.eros.db.constants;

public enum RightsStatusEnum {


    OK(1, "上架中"),
    NO_OK(2, "已下架");


    private Integer status;

    private String desc;


    RightsStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
