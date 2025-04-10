package com.open.ai.eros.ai.lang.chain.test;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiImageModelName;
import dev.langchain4j.model.output.Response;

/**
 * @类名：ImageTest
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/10 12:44
 */
public class ImageTest {

    public static void main(String[] args) {

        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                .baseUrl("https://zen-vip.zeabur.app/v1")
                .modelName("gpt-4o-2024-05-13")
                .maxTokens(50)
                .build();

        UserMessage userMessage = UserMessage.from(
                TextContent.from("What do you see?"),
                ImageContent.from("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png")
        );

        Response<AiMessage> response = model.generate(userMessage);
        System.out.println(response.content().text());



        ImageModel imageModel = OpenAiImageModel.builder()
                .apiKey("sk-R2rCl3JhjqlOl2ILE4676a8b502f42E0B4896810A909Dc75")
                .baseUrl("https://api.open-proxy.cn/v1")
                .modelName(OpenAiImageModelName.DALL_E_3)
                .build();

        Response<Image> imageResponse = imageModel.generate("a cat");
        System.out.println(imageResponse.content().url()); // Donald Duck is here :)


    }
}
