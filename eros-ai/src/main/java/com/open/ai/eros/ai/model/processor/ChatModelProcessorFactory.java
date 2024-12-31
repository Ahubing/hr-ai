package com.open.ai.eros.ai.model.processor;

import com.open.ai.eros.ai.model.processor.ai.GeminiChatProcessor;
import com.open.ai.eros.ai.model.processor.ai.GptChatProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ChatModelProcessorFactory implements ApplicationContextAware {

    private static List<ChatModelProcessor> chatModelProcessors = new ArrayList<>();

    private static GptChatProcessor gptChatModelProcessor;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        gptChatModelProcessor = applicationContext.getBean(GptChatProcessor.class);
        GeminiChatProcessor geminiChatProcessor = applicationContext.getBean(GeminiChatProcessor.class);
        chatModelProcessors.add(gptChatModelProcessor);
        chatModelProcessors.add(geminiChatProcessor);
    }


    public static ChatModelProcessor getChatModelProcessor(String model,String template){
        for (ChatModelProcessor chatModelProcessor : chatModelProcessors) {
            if(chatModelProcessor.match(model, template)){
                return chatModelProcessor;
            }
        }
        return gptChatModelProcessor;
    }



}
