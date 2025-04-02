package com.open.ai.eros.ai.vector.factory;

import com.open.ai.eros.ai.constatns.ModelTypeEnum;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LLMFactory {

    private final static Logger log = LoggerFactory.getLogger(LLMFactory.class);
    public static ChatLanguageModel getLLM(ModelTypeEnum typeEnum, String apiKey, String baseUrl, String modelName, Double temperature){
        switch (typeEnum){
            case OPEN_AI:
                return OpenAiChatModel.builder()
                        .baseUrl(baseUrl)
                        .apiKey(apiKey)
                        .modelName(modelName)
                        .temperature(temperature)
                        .build();
            default:
                log.error("不支持的LLM");
                throw new RuntimeException("不支持的LLM");
        }
    }
}
