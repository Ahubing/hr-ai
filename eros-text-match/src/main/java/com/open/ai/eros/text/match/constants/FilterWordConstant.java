package com.open.ai.eros.text.match.constants;

public class FilterWordConstant {

    /**
     * 敏感词类型 1：风险词，2：url 3 全拼  4：白词
     */
    public static int FILTER_WORD = 1;

    public static int URL_WORD = 2;

    public static int PINYIN_WORD = 3;

    public static int WRITE_WORD = 4;

    /**
     * 自动回复
     */
    public static int REPLY_TYPE = 5;

    public static int CLOSE_STATUS = 0;//敏感词删除的状态

}
