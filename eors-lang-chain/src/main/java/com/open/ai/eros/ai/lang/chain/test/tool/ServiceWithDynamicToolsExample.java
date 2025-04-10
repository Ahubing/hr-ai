package com.open.ai.eros.ai.lang.chain.test.tool;

import com.open.ai.eros.common.util.DateUtils;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderResult;

import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * @类名：ServiceWithDynamicToolsExample
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/10 9:10
 */
public class ServiceWithDynamicToolsExample {

    static class Calculator {

        @Tool("Calculates the length of a string")
        int stringLength(String s) {
            System.out.println("Called stringLength with s='" + s + "'");
            return s.length();
        }

        @Tool("Calculates the sum of two numbers")
        int add(int a, int b) {
            System.out.println("Called add with a=" + a + ", b=" + b);
            return a + b;
        }

        @Tool("Calculates the square root of a number")
        double sqrt(int x) {
            System.out.println("Called sqrt with x=" + x);
            return Math.sqrt(x);
        }

        @Tool(value = {"当前时间","现在时间"})
        public String nowTime(){
            return DateUtils.formatDate(new Date(),DateUtils.FORMAT_YYYY_MM_DD_HHMMSS);
        }

    }

    interface Assistant {

        String chat(String message);
    }

    public static void main(String[] args) {

        //                .apiKey("sk-08076fe365224ce0ad9bd9c0aea015d6")
        //                .baseUrl("http://43.153.57.148/v1")

        List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(Calculator.class);

        ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
                .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                .baseUrl("https://zen-vip.zeabur.app/v1")
                .modelName("gpt-4o-2024-05-13")
                .temperature(0.0)
                .timeout(Duration.ofSeconds(60))
                .build();

        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            Calculator calculator = new Calculator();
            return calculator.nowTime();
        };


        ToolProvider toolProvider = (toolProviderRequest) -> {
            ToolProviderResult.Builder builder = ToolProviderResult.builder();
            if (toolProviderRequest.userMessage().singleText().contains("时间")) {
                for (ToolSpecification toolSpecification : toolSpecifications) {
                    if(toolSpecification.description().contains("时间")){
                        builder.add(toolSpecification,toolExecutor);
                    }
                }
            } else {
                return null;
            }
            return  builder.build();
        };

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(toolProvider)
                .build();
        interact(assistant, "输出明天下午两点的时间格式? 和 1加2的和是多少");
    }

    private static void interact(Assistant assistant, String userMessage) {
        System.out.println("[User]: " + userMessage);
        String answer = assistant.chat(userMessage);
        System.out.println("[Assistant]: " + answer);
        System.out.println();
        System.out.println();
    }

}
