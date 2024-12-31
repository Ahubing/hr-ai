package com.open.ai.eros.ai.lang.chain.test.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.tool.DefaultToolExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类名：FunctionCallByToolSpecification
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/13 15:59
 */
public class FunctionCallByToolSpecification {



    public static void main(String[] args) {

        Calculator calculator = new Calculator();
        Method[] methods = calculator.getClass().getMethods();


        Map<String,DefaultToolExecutor> toolExecutorMap = new HashMap<>();
        List<ToolSpecification> toolSpecifications = new ArrayList<>();
        for (Method method : methods) {
            if(method.isAnnotationPresent(Tool.class)){
                Tool annotation = method.getAnnotation(Tool.class);
                toolSpecifications.add(ToolSpecifications.toolSpecificationFrom(method));
                DefaultToolExecutor defaultToolExecutor = new DefaultToolExecutor(calculator, method);
                toolExecutorMap.put(annotation.name(),defaultToolExecutor);
            }
        }

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                .baseUrl("https://zen-vip.zeabur.app/v1")
                .modelName("gpt-4o-2024-05-13")
                .build();

        List<ChatMessage> messages = new ArrayList<>();
        String userMessage = "输出明天下午两点的时间时间? 和 1加2的和是多少";
        messages.add(new UserMessage("user",userMessage));

        messages.add(new UserMessage("assistant","现在的时间是 2024年10月15日 14:06:30。  \n" +
                "1加2的和是 3。"));
        messages.add(new UserMessage("user","你好,现在几点了"));
        Response<AiMessage> generate = model.generate(messages,toolSpecifications);
        AiMessage content = generate.content();
        boolean updated = content.hasToolExecutionRequests();
        if(updated){
            List<ToolExecutionRequest> toolExecutionRequests = content.toolExecutionRequests();
            for (ToolExecutionRequest toolExecutionRequest : toolExecutionRequests) {
                DefaultToolExecutor defaultToolExecutor = toolExecutorMap.get(toolExecutionRequest.name());
                if(defaultToolExecutor==null){
                    continue;
                }
                String aDefault = defaultToolExecutor.execute(toolExecutionRequest, "default");
                System.out.println(toolExecutionRequest.name()+" "+toolExecutionRequest.arguments()+" result:"+aDefault);
            }
        }else{
            System.out.println(content.text());
        }
        try {
            Thread.sleep(10000);
        }catch (Exception e){

        }

    }

}
