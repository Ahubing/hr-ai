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
     * 1. 简历初筛：等待简历初筛（因为目前系统只有自动简历初筛，会根据筛选条件自动完成筛选，所以简历会直接进入下一阶段）
     * 2. 业务筛选：使用“筛选提示词”对候选人进行筛选，如果不存在“筛选提示词”则跳过此阶段直接进入下一阶段
     * 3. 邀约跟进：进行意向确认，确认后预约面试（时间，地点）。
     * 4. 等待面试：AI只需要跟进到这个阶段，后面的阶段由人工进行操作。
     * 5. 已发offer
     * 6. 已入职
     */

    ABANDON(-1, "abandon","不符合"),
    RESUME_SCREENING(0, "resume_screening","简历初筛"),
    BUSINESS_SCREENING(1, "business_screening","业务筛选"),
    INVITATION_FOLLOW_UP(2, "invitation_follow","邀约跟进"),
    INTERVIEW_ARRANGEMENT(3, "interview_arrangement","面试安排"),
    OFFER_ISSUED(4, "offer_issued","发放offer"),
    ONBOARD(5, "onboard","已入职");

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
        return null;
    }



    /**
     * 根据key查找枚举,如果找不到,默认返回跟进中
     */
    public static ReviewStatusEnums getEnum(String key) {
        for (ReviewStatusEnums e : ReviewStatusEnums.values()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        return null;
    }
    /**
     * 根据status查找枚举,如果找不到,默认返回初筛
     */
    public static ReviewStatusEnums getEnumByStatus(Integer status) {
        for (ReviewStatusEnums e : ReviewStatusEnums.values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return RESUME_SCREENING;
    }
}
