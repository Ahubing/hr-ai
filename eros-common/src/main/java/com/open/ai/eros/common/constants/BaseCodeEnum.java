package com.open.ai.eros.common.constants;


public enum BaseCodeEnum implements BaseCode {

    // 成功
    SUCCESS(200, "OK"),
    FAIL(500, "FAIL"),

    // mj 任务状态
    IN_PROGRESS(1,"执行中"),

    TASK_EXSIT(21,"任务已经存在"),
    QUEQEING(22,"任务排队中"),
    PARAM_ERROR(400, "PARAM VALID NOT PASS!"),
    SIGNATURE_NOT_MATCH(401, "SIGNATURE NOT MATCH!"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR!"),
    SERVER_BUSY(503, "系统繁忙，请稍微再试!"),
    LOGIN_EXPIRE(1001, "登录失效，请重新登录"),
    HAVE_FILTER_WORD(1003, "问题中包含保存违规信息，请重新编辑！,如果有疑惑~  "),
    IP_LIMIT_MSG(1006,"您好，不好意思！为防止接口被刷次数，登录账号即可重新访问！"),
    VISIT_BUZY(1008,"您好，不好意思！为防止接口被刷次数，发现您现在访问过于频繁，请等待三秒再进行访问！"),
    BACKLIST(1009,"您好，检测到您近期有异常，暂被限制使用，有疑惑可以关注公众号【蓝猫AI三千问】联系到我们！"),
    TERMINATE(1012,"回答已终止"),
    NO_MODEL(1011,"您好,当前不支持该模型，请联系管理员！"),
    OFFSITE_LOGIN(1010,"您好，当前账号已在其它设备登录，请确保您的账号密码是否安全！"),
    NO_MODEL_ROLE(1012,"您好,您暂无该模型的访问权，请联系管理员！"),
    MODEL_NO_OPEN(1013,"您好,您该模型暂未开放，请联系管理员！"),
    NO_MONEY(1014,"您好,余额不够，请联系管理员！"),
    NO_TOKEN(1015,"您好,当前令牌不合法，请确定令牌是否正确"),
    NO_TOKEN_PARAM(1016,"您好,令牌不能为空"),
    NO_CHAT_MESSAGE(1018,"您好,问题不能为空"),
    TOKEN_OVER(1017,"上下文加问题的token数已经超出当前模型支持的最大数"),

    TOKEN_USE_TYPE_ERROR(1019,"不支持该消费方式");

    private int value;

    private String text;

    BaseCodeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public int getCode() {
        return this.value;
    }

    @Override
    public String getMsg() {
        return this.text;
    }
}
