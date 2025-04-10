package com.open.ai.eros.db.constants;

import java.util.HashMap;
import java.util.Map;

public enum RightsRuleEnum {

    EVERY_DAY_INIT_USED("EVERY_DAY_INIT_USED","每天重置已使用量"),
    EVERY_DAY_ADD_TOTAL("EVERY_DAY_ADD_TOTAL","每天累计")

    ;


    private String rule;

    private String desc;

    RightsRuleEnum(String rule, String desc) {
        this.rule = rule;
        this.desc = desc;
    }


    static Map<String,RightsRuleEnum> rightsRuleEnumMap = new HashMap<>();
    static {
        for (RightsRuleEnum value : values()) {
            rightsRuleEnumMap.put(value.getRule(),value);
        }
    }


    public static String getDesc(String rule){

        RightsRuleEnum rightsRuleEnum = rightsRuleEnumMap.get(rule);
        if(rightsRuleEnum==null){
            return "";
        }
        return rightsRuleEnum.getDesc();
    }




    public String getRule() {
        return rule;
    }

    public String getDesc() {
        return desc;
    }
}
