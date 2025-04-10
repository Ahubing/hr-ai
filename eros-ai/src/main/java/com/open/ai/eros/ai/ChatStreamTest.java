package com.open.ai.eros.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：ChatStreamTest
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/9 13:06
 */
public class ChatStreamTest {


    public static void main(String[] args) {
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                .baseUrl("https://zen-vip.zeabur.app/v1")
                .modelName("gpt-4o-2024-05-13")
                .build();

        List<ChatMessage> messages = new ArrayList<>();
        String userMessage = "你好";
        messages.add(new UserMessage("user",userMessage));
        model.generate(messages, new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                System.out.println("onNext: " + token);
            }
            @Override
            public void onComplete(Response<AiMessage> response) {
                System.out.println("onComplete: " + response);
            }
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
        try {
            Thread.sleep(10000);
        }catch (Exception e){

        }
    }


}
