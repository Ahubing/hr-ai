package com.open.ai.eros.ai.util;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.constants.ModelPriceEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
public class TokenUtil {


    private final static EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    private static final Map<String, Encoding> modelMap = new HashMap<>();
    static {
        for (ModelType modelType : ModelType.values()) {
            modelMap.put(modelType.getName(), registry.getEncodingForModel(modelType));
        }
        modelMap.put(ModelPriceEnum.GPT_3_TURBO_16K_0613.getModel(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO_16K));
        modelMap.put(ModelPriceEnum.GPT_3_TURBO_0301.getModel(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelPriceEnum.GPT_3_TURBO_16K_1106.getModel(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelPriceEnum.GPT_3_TURBO_16K_0613.getModel(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelPriceEnum.gpt_3_5_turbo_1106.getModel(),registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelPriceEnum.GPT_3_TURBO_0613.getModel(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelPriceEnum.gpt_4_1106_preview.getModel(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.gpt_4_vision_preview.getModel(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.tts_1.getModel(),registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
//        modelMap.put(ModelPriceEnum.claude_1_100k.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.claude_instant_100k.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.claude_2_100k.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.gpt_4_all.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.whisper_1.getModel(),registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelPriceEnum.gpt_4_gizmo.getModel(),registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelPriceEnum.gemini_pro.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.bing_creative.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.bing_balanced.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.bing_precise.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.claude_2_1.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.text_embedding_3_large.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.text_embedding_3_small.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.text_embedding_ada_002.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.gpt_3_5_turbo_0125.getModel(),registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelPriceEnum.gpt_4_turbo_2024_04_09.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.command_r_plus.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.command_r.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.gpt_4o_2024_05_13.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.gpt_4o_all.getModel(),registry.getEncodingForModel(ModelType.GPT_4));
    }


    public static int countTokenText(String text,String model){
        Encoding encodingForModel = modelMap.get(model);
        if(encodingForModel==null){
            encodingForModel= modelMap.get(ModelPriceEnum.GPT_4.getModel());
        }
        return getTokenNum(encodingForModel,text);
    }


    public static int countTokenMessages(Collection<ChatMessage> chatMessages, String model){

        if(ModelPriceEnum.dall_e_3.getModel().equals(model)){
            return 10;
        }

        Encoding encodingForModel = modelMap.get(model);
        if(encodingForModel==null){
            log.info("countTokenMessages error 当前模型不能没计算token model={} ", model);
            encodingForModel= modelMap.get(ModelPriceEnum.GPT_4.getModel());
        }
        int tokensPerMessage;
        int tokensPerName;
        if ( "gpt-3.5-turbo-0301".equals(model)) {
            tokensPerMessage = 4;
            tokensPerName = -1; // If there's a name, the role is omitted
        } else {
            tokensPerMessage = 3;
            tokensPerName = 1;
        }
        int tokenNum = 0;
        for (ChatMessage chatMessage : chatMessages) {
            tokenNum += tokensPerMessage;
            tokenNum += getTokenNum(encodingForModel, Objects.nonNull(chatMessage.getContent()) ? chatMessage.getContent().toString():"");
            tokenNum += getTokenNum(encodingForModel, Objects.nonNull(chatMessage.getRole())?chatMessage.getRole():"");
        }
        tokenNum += 3; // Every reply is primed with <|start|>assistant<|message|>
        return tokenNum;
    }


    public static int countTokenPrompt(String prompt, String model){
        Encoding encodingForModel = modelMap.get(model);
        if(encodingForModel==null){
            log.info("countTokenMessages error 当前模型不能没计算token model={} ", model);
            encodingForModel= modelMap.get(ModelPriceEnum.GPT_4.getModel());
        }
        int tokensPerMessage;
        int tokensPerName;
        if ( "gpt-3.5-turbo-0301".equals(model)) {
            tokensPerMessage = 4;
            tokensPerName = -1; // If there's a name, the role is omitted
        } else {
            tokensPerMessage = 3;
            tokensPerName = 1;
        }
        int tokenNum = 0;
            tokenNum += tokensPerMessage;
            tokenNum += getTokenNum(encodingForModel, prompt);

        tokenNum += 3; // Every reply is primed with <|start|>assistant<|message|>
        return tokenNum;
    }


    public static int getTokenNum(Encoding tiktoken, String text){
        if(text.contains("image/jpeg;base64") || text.contains("image/png;base64") || text.contains("image/webp;base64")){
            return 200;
        }
        List<Integer> encode = tiktoken.encode(text);
        return encode.size();
    }


    public static int getBase64TokenNum(Collection<ChatMessage> chatMessages){

        for (ChatMessage chatMessage : chatMessages) {
            String text = chatMessage.getContent().toString();
            if(text.contains("image/jpeg;base64") || text.contains("image/png;base64") || text.contains("image/webp;base64")){
                return 30;
            }
        }
        return 0;
    }


}
