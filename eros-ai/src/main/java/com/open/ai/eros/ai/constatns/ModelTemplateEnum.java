package com.open.ai.eros.ai.constatns;


import com.open.ai.eros.common.constants.ModelPriceEnum;

import java.util.Arrays;
import java.util.List;

/**
 * 模型的枚举类
 */
public enum ModelTemplateEnum {

    GEMINI_API("google", "谷歌官方API", Arrays.asList(
            ModelPriceEnum.gemini_1_5_flash.getModel(),
            ModelPriceEnum.gemini_1_5_pro.getModel(),
            ModelPriceEnum.gemini_exp_1114.getModel(),
            ModelPriceEnum.gemini_1_5_pro_last.getModel()
    )),
    //STAR_MJ_API("Midjourney","star-mj-api","Midjourney Star模版"),
    CLAUDE_API("claude", "claude官方", Arrays.asList(
            ModelPriceEnum.claude_3_opus.getModel(),
            ModelPriceEnum.claude_3_sonnet.getModel(),
            ModelPriceEnum.claude_3_sonnet20240620.getModel()
    )),
    AWS_CLAUDE_API("aws", "aws|gcp-claude", Arrays.asList(
            ModelPriceEnum.claude_3_opus.getModel(),
            ModelPriceEnum.claude_3_sonnet.getModel(),
            ModelPriceEnum.claude_3_sonnet20240620.getModel()
    )),
    OPEN_AI_API_GPT("OpenAI", "Open AI官方", Arrays.asList(
            ModelPriceEnum.gpt_4o_mini.getModel(),
            ModelPriceEnum.gpt_4o_2024_08_06.getModel(),
            ModelPriceEnum.gpt_4o.getModel(),
            ModelPriceEnum.gpt_4_1106_preview.getModel(),
            ModelPriceEnum.gpt_4_turbo_2024_04_09.getModel(),

            ModelPriceEnum.text_embedding_ada_002.getModel(),
            ModelPriceEnum.text_embedding_3_small.getModel(),
            ModelPriceEnum.text_embedding_3_large.getModel()
    )),

    GROK_API_GPT("grok", "Grok-X", Arrays.asList(
            ModelPriceEnum.grok_beta.getModel()
    )),
    AZURE_API_GPT("azure", "Azure AI官方", Arrays.asList(
            ModelPriceEnum.gpt_4o_mini.getModel(),
            ModelPriceEnum.gpt_4o_2024_08_06.getModel(),
            ModelPriceEnum.gpt_4o.getModel(),
            ModelPriceEnum.gpt_4_1106_preview.getModel(),
            ModelPriceEnum.gpt_4_turbo_2024_04_09.getModel()
            )),
    COHERE_API_GPT("cohere", "Cohere AI官方", Arrays.asList(
            ModelPriceEnum.command_r_plus.getModel(),
            ModelPriceEnum.command_r_plus_08_2024.getModel())
    ),

    EROS_AI("eros", "Eros AI官方", Arrays.asList(
            ModelPriceEnum.eros4_5.getModel()
    ));

    private String template;//模板名称
    private String desc;
    private List<String> models;

    ModelTemplateEnum(String template, String desc, List<String> models) {
        this.desc = desc;
        this.template = template;
        this.models = models;
    }

    public static boolean isExist(String template) {
        for (ModelTemplateEnum value : ModelTemplateEnum.values()) {
            if (value.template.equals(template)) {
                return true;
            }
        }
        return false;
    }

    public static String getDescByTemplate(String template) {
        for (ModelTemplateEnum value : ModelTemplateEnum.values()) {
            if (value.template.equals(template)) {
                return value.getDesc();
            }
        }
        return "";
    }

    public List<String> getModels() {
        return models;
    }

    public String getTemplate() {
        return template;
    }


    public String getDesc() {
        return desc;
    }
}
