package com.open.ai.eros.ai.lang.chain.test.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @类名：ToolExecutorTest
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/14 21:09
 */
public class ToolExecutorTest {

    public static void main(String[] args) {

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                .baseUrl("https://zen-vip.zeabur.app/v1")
                .modelName("gpt-4o-2024-05-13")
                .build();

        Calculator calculator = new Calculator();
        Method[] methods = calculator.getClass().getMethods();
        List<Method> methodList = new ArrayList<>();
        for (Method method : methods) {
            if(method.isAnnotationPresent(Tool.class)){
                methodList.add(method);
            }
        }



    }
}
