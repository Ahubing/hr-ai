package com.open.hr.ai.constant;

/**
 * @Author 
 * @Date 2025/1/12 20:12
 */
public enum PositionSyncTaskStatusEnums {
    NOT_START(0,"未开始"),
    START(1,"同步中"),
    FINISH(2,"同步完成"),
    ;

    private Integer status;
    private String desc;

    PositionSyncTaskStatusEnums(Integer status, String desc) {
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

