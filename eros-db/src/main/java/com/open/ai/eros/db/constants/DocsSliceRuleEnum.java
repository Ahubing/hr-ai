package com.open.ai.eros.db.constants;

/**
 * @类名：DocsSliceRuleEnum
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/15 12:38
 */
public enum DocsSliceRuleEnum {



    SIMPLE("simple","去空格&回车"),
    AI_SLICE("按字数-200","字数切割")
    ;

    private String value;
    private String desc;

    DocsSliceRuleEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }


}
