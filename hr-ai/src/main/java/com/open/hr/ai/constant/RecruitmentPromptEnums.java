package com.open.hr.ai.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author liuzilin
 * @Date 2025/2/8 19:39
 */
public enum RecruitmentPromptEnums {
    //公司名称
    // 企业基础信息
    COMPANY_NAME("company", "公司名称", 1, 1, 1),
    INDUSTRY_FIELD("area", "行业领域", 1, 1, 1),
    ESTABLISHMENT_TIME("establishedTime", "成立时间", 1, 1, 0),
    COMPANY_SIZE("scale", "公司规模", 1, 1, 0),
    HEADQUARTERS_LOCATION("headquartersLocation", "总部地点", 1, 1, 0),
    OFFICIAL_WEBSITE("officialWebsite", "官方网站", 1, 1, 0),

    // 招聘职位信息
    JOB_TITLE("positionName", "职位名称", 2, 2, 1),
    WORK_CITY("city", "工作城市", 2, 2, 1),
    WORK_LOCATION("workLocation", "工作地点", 2, 1, 1),
    WORK_TIME("workTime", "工作时间", 2, 1, 1),
    MIN_WORK_DURATION("workMiniTime", "最短工作时间要求", 2, 1, 1),
    JOB_TYPE("positionType", "职位类型", 2, 2, 1),
    SALARY_RANGE("salaryArea", "薪资范围", 2, 2, 1),
    NUMBER_OF_RECRUITS("recruitingNumbers", "招聘人数", 2, 1, 0),
    URGENCY_LEVEL("emergencyDegree", "紧急程度", 2, 1, 1),

    // 职位要求信息
    EDUCATION_REQUIREMENT("degree", "学历要求", 3, 2, 1),
    WORK_EXPERIENCE("experience", "工作经验", 3, 2, 1),
    PROFESSIONAL_SKILLS("skills", "专业技能", 3, 2, 1),
    OTHER_REQUIREMENTS("otherArgue", "其他要求", 3, 1, 0),

    // 差异化优势
    COMPENSATION_BENEFITS("salaryAndWelfare", "薪酬福利", 4, 1, 0),
    CAREER_DEVELOPMENT("careerDevelopment", "职业发展", 4, 1, 0),
    WORK_ENVIRONMENT("workEnvironment", "工作环境", 4, 1, 0),
    SPECIAL_BENEFITS("welfare", "特别福利", 4, 1, 0),

    OTHER_INFORMATION("otherInformation", "其他招聘信息", 5, 1, 0),

    TONE_STYLE("toneStyle", "语气风格", 6, 1, 0);

    ;

    private String key;

    private String keyDesc;

    /**
     * 类型
     * 1、企业基础信息
     * 2、招聘职位信息
     * 3、职位要求信息
     * 4、差异化优势
     * 5、其他招聘信息
     * 6、智能交互指令
     */
    private Integer type;

    /**
     * 获取方式
     * 1、用户填写
     * 2、查接口获取
     */
    private Integer accessType;

    /**
     * 是否必须
     */
    private Integer required;

    RecruitmentPromptEnums(String key, String keyDesc, Integer type, Integer accessType, Integer required) {
        this.key = key;
        this.keyDesc = keyDesc;
        this.type = type;
        this.accessType = accessType;
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyDesc() {
        return keyDesc;
    }

    public void setKeyDesc(String keyDesc) {
        this.keyDesc = keyDesc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAccessType() {
        return accessType;
    }

    public void setAccessType(Integer accessType) {
        this.accessType = accessType;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }


    public static RecruitmentPromptEnums getByKey(String key) {
        for (RecruitmentPromptEnums value : RecruitmentPromptEnums.values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }

    public static RecruitmentPromptEnums getByKeyDesc(String keyDesc) {
        for (RecruitmentPromptEnums value : RecruitmentPromptEnums.values()) {
            if (value.getKeyDesc().equals(keyDesc)) {
                return value;
            }
        }
        return null;
    }

    public static RecruitmentPromptEnums getByType(Integer type) {
        for (RecruitmentPromptEnums value : RecruitmentPromptEnums.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static List<RecruitmentPromptEnums> getByAccessTypeAndType(Integer type, Integer accessType) {
        List<RecruitmentPromptEnums> list = new ArrayList<>();
        for (RecruitmentPromptEnums value : RecruitmentPromptEnums.values()) {
            if (value.getAccessType().equals(accessType) && value.getType().equals(type)) {
                list.add(value);
            }
        }
        return list;
    }
}
