package com.open.ai.eros.common.constants;

/**
 * 简历状态枚举
 *
 */
public enum ReviewStatusEnums {
    /**
     * 1、简历初筛
     * 2、邀约跟进
     * 3、面试安排
     * 4、发放offer
     * 5、已入职
     */
    ABANDON(-1, "abandon","无意向"),
    RESUME_SCREENING(0, "resume_screening","简历初筛"),
    INVITATION_FOLLOW_UP(1, "invitation_follow","邀约跟进"),
    INTERVIEW_ARRANGEMENT(2, "interview_arrangement","面试安排"),
    OFFER_ISSUED(3, "offer_issued","发放offer"),
    ONBOARD(4, "onboard","已入职");

    private Integer status;
    private String desc;
    private String key;

    ReviewStatusEnums(Integer status, String key, String desc) {
        this.status = status;
        this.desc = desc;
        this.key = key;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 根据key查找枚举,如果找不到,默认返回跟进中
     */
    public static ReviewStatusEnums getEnumByKey(String key) {
        for (ReviewStatusEnums e : ReviewStatusEnums.values()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        return INVITATION_FOLLOW_UP;
    }
}
