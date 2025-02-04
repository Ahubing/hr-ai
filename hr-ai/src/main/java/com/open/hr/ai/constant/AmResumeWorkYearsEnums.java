package com.open.hr.ai.constant;

/**
 * @Author
 * @Date 2025/1/12 20:12
 */
public enum AmResumeWorkYearsEnums {

    //工作年限 1：应届 2：1-3年 3：3-5年 4：5-10年 5：10年以上

    zero(1,0,0, "应届"),
    one_three(2, 1,3,"1-3年"),
    three_five(3, 3,5,"3-5年"),
    five_ten(4, 5,10,"5-10年"),
    ten_plus(4, 10,0,"5-10年以上");

    private Integer code;
    private Integer begin;
    private Integer end;
    private String desc;

    AmResumeWorkYearsEnums(Integer code, Integer begin, Integer end, String desc) {
        this.code = code;
        this.begin = begin;
        this.end = end;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getBegin() {
        return begin;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static AmResumeWorkYearsEnums getByCode(Integer code) {
        for (AmResumeWorkYearsEnums value : values()) {
            if (value.getBegin().equals(code)) {
                return value;
            }
        }
        return null;
    }
}

