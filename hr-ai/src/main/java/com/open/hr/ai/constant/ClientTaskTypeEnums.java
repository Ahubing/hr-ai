package com.open.hr.ai.constant;

/**
 * https://zcn1d8khjde1.feishu.cn/wiki/EUHAwMwlSi9yJdkt0T9cqxANnkg
 *
 * @Date 2025/1/12 20:12
 */
public enum ClientTaskTypeEnums {
    GREET("greet", "打招呼",3),
    GET_ALL_JOB("get_all_job", "同步职位",5),
    SWITCH_JOB_STATE("switch_job_state", "开关职位",1),
    SEND_MESSAGE("send_message", "发送消息",4),
    SEND_RECHAT_MESSAGE("send_message", "发送复聊消息",2),

    REQUEST_ALL_INFO("request_all_info", "读取求职者信息",1),
    REQUEST_INFO("request_info", "请求求职者信息",3),

    ;

    private String type;
    private String desc;

    private Integer order;


    ClientTaskTypeEnums(String type, String desc, Integer order) {
        this.type = type;
        this.desc = desc;
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}

