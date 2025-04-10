package com.open.ai.eros.ai.lang.chain.provider.build;


import com.open.ai.eros.ai.lang.chain.constants.ProviderEnum;
import com.open.ai.eros.common.constants.ModelPriceEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * ##           // text-embedding-ada-002 ->矢量长度：1536
 * ##           // text-embedding-3-small ->矢量长度：1536
 * ##           // text-embedding-3-large ->矢量长度：3072
 */
public enum ModelVectorEnum {

    text_embedding_ada_002("text-embedding-ada-002",1536, ProviderEnum.OPENAI.name(), ModelPriceEnum.GPT_3_TURBO.getModel()),
    text_embedding_small_3("text-embedding-3-small",1536,ProviderEnum.OPENAI.name(),ModelPriceEnum.GPT_3_TURBO.getModel()),
    text_embedding_large_3("text-embedding-3-large",3072,ProviderEnum.OPENAI.name(),ModelPriceEnum.GPT_4.getModel()),
    ;

    private String model;// 模型标识
    private int vectorLength;// 矢量长度
    private String modelSource;//模型来源
    private String encodingForModel;//解析模型

    ModelVectorEnum(String model, int vectorLength, String modelSource,String encodingForModel) {
        this.model = model;
        this.vectorLength = vectorLength;
        this.modelSource = modelSource;
        this.encodingForModel = encodingForModel;
    }


    public String getModel() {
        return model;
    }

    public int getVectorLength() {
        return vectorLength;
    }

    public String getModelSource() {
        return modelSource;
    }

    public String getEncodingForModel() {
        return encodingForModel;
    }

    static Map<String,ModelVectorEnum> modelVectorEnumMap = new HashMap<>();

    static{
        for (ModelVectorEnum value : values()) {
            modelVectorEnumMap.put(value.getModel(),value);
        }
    }


    public static ModelVectorEnum getModelVectorEnum(String model){
        return modelVectorEnumMap.getOrDefault(model,text_embedding_ada_002);
    }

}
