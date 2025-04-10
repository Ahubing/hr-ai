package com.open.ai.eros.ai.constatns;


import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;

import java.util.Arrays;
import java.util.List;

/**
 * 模型的枚举类
 */
public enum EmbeddingModelTemplateEnum {

    OPEN_AI_API_GPT("OpenAI","Open AI官方",Arrays.asList(
            OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.toString(),
            OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_LARGE.toString(),
            OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002.toString()
    )),
    ;

    private String template;//模板名称
    private String desc;
    private List<String> models;

    EmbeddingModelTemplateEnum(String template, String desc, List<String> models) {
        this.desc = desc;
        this.template = template;
        this.models = models;
    }

    public static boolean isExist(String template){
        for (EmbeddingModelTemplateEnum value : EmbeddingModelTemplateEnum.values()) {
            if(value.template.equals(template)){
                return true;
            }
        }
        return false;
    }

    public static String getDescByTemplate(String template){
        for (EmbeddingModelTemplateEnum value : EmbeddingModelTemplateEnum.values()) {
            if(value.template.equals(template)){
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
