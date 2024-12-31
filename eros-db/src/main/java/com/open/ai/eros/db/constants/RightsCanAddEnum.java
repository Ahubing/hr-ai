package com.open.ai.eros.db.constants;




public enum RightsCanAddEnum {

    CAN_ADD(1,"可叠加"),
    NOT_CAN_ADD(2,"不可叠加"),
    ;


    private int status;
    private String desc;

    RightsCanAddEnum(int status, String desc) {
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
