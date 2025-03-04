package com.open.ai.eros.common.constants;

public enum AmAdminStatusEnums {
    //状态。0未启用，1禁用，2启用
    UN_OPEN(0, "未启用"),
    BAN(1, "未启用"),
    OPEN(2, "启用"),
    ;

    private Integer status;
    private String desc;

    AmAdminStatusEnums(Integer status, String desc) {
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
