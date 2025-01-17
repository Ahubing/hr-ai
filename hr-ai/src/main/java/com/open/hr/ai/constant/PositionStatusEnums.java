package com.open.hr.ai.constant;

/**
 * @Author
 * @Date 2025/1/12 20:12
 */
public enum PositionStatusEnums {
    POSITION_OPEN(1, "职位开放"),
    POSITION_CLOSE(0, "职位关闭");

    private Integer status;
    private String desc;

    PositionStatusEnums(Integer status, String desc) {
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

