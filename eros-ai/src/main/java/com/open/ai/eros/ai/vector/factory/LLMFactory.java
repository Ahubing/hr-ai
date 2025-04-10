package com.open.ai.eros.ai.vector.factory;

import cn.hutool.extra.spring.SpringUtil;
import com.open.ai.eros.ai.constatns.ModelTypeEnum;
import com.open.ai.eros.db.mysql.hr.entity.AmModel;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.service.impl.AmModelServiceImpl;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class LLMFactory {

    private final static Logger log = LoggerFactory.getLogger(LLMFactory.class);

    private final static AmModelServiceImpl modelService = SpringUtil.getBean(AmModelServiceImpl.class);

    public static ChatLanguageModel getLLM(LLMFactors factors){
        return getLLMInstance(factors);
    }

    public static ChatLanguageModel getLLM(ModelTypeEnum typeEnum, String baseUrl, String apiKey, String modelName, Double temperature){
        return getLLMInstance(new LLMFactors(typeEnum, baseUrl, apiKey, modelName, temperature));
    }

    public static ChatLanguageModel getLLM(Serializable modelId){
        AmModel model = modelService.getById(modelId);
        if(model == null){
            model = modelService.getDefaultModel();
        }
        return getLLMInstance(new LLMFactors(ModelTypeEnum.OPEN_AI,getUrl(model.getUrl()), model.getApikey(), model.getValue(), model.getTemperature()));
    }

    public static ChatLanguageModel getDefaultLLM(){
        AmModel model = modelService.getDefaultModel();
        return getLLMInstance(new LLMFactors(ModelTypeEnum.OPEN_AI,getUrl(model.getUrl()), model.getApikey(), model.getValue(), model.getTemperature()));
    }

    private static String getUrl(String cdnHost) {
        return cdnHost.endsWith("/") ? cdnHost + "v1" : cdnHost + "/v1";
    }

    private static ChatLanguageModel getLLMInstance(LLMFactors factors){
        switch (factors.getTypeEnum()){
            case OPEN_AI:
                return OpenAiChatModel.builder()
                        .baseUrl(factors.getBaseUrl())
                        .apiKey(factors.getApiKey())
                        .modelName(factors.getModelName())
                        .temperature(factors.getTemperature())
                        .build();
            default:
                log.error("不支持的LLM");
                throw new RuntimeException("不支持的LLM");
        }
    }

    @Data
    @AllArgsConstructor
    public static class LLMFactors{

        private ModelTypeEnum typeEnum;

        private String baseUrl;

        private String apiKey;

        private String modelName;

        private Double temperature;
    }
}
