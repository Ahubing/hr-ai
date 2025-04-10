package com.open.hr.ai.constant;

/**
 * @Author
 * @Date 2025/1/12 20:12
 */
public enum AmLocalAccountStatusEnums {

    //offline下线, wait_login 等待登录 ,free 已经登录成功,没有在执行任务, busy 正在执行任务
    OFFLINE("offline", "下线"),
    WAIT_LOGIN("wait_login", "等待登录"),
    FREE("free", "已经登录成功,没有在执行任务"),
    BUSY("busy", "正在执行任务"),
    ;

    private String status;
    private String desc;

    AmLocalAccountStatusEnums(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    // 根据status 判断是否存在枚举中
    public static boolean isExist(String status) {
        for (AmLocalAccountStatusEnums value : AmLocalAccountStatusEnums.values()) {
            if (value.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }
}

