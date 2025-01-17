package com.open.hr.ai.constant;

/**
 * https://zcn1d8khjde1.feishu.cn/wiki/EUHAwMwlSi9yJdkt0T9cqxANnkg
 *
 * @Date 2025/1/12 20:12
 */
public enum ClientTaskTypeEnums {
    GREET("greet", "打招呼"),
    GET_ALL_JOB("get_all_job", "获取全部岗位"),
    SWITCH_JOB_STATE("switch_job_state", "切换岗位开关状态"),
    SEND_MESSAGE("send_message", "发送消息"),
    REQUEST_ALL_INFO("request_all_info", "请求用户的所有信息"),

    ;

    private String type;
    private String desc;

    ClientTaskTypeEnums(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {

        return type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

