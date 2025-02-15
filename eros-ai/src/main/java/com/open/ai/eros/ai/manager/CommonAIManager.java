package com.open.ai.eros.ai.manager;

import com.open.ai.eros.ai.tool.config.ToolConfig;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @类名：CommonAIManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/12/6 11:56
 */
@Slf4j
@Component
public class CommonAIManager {


    @Autowired
    private ModelConfigManager modelConfigManager;


    /**
     * 非流获取ai结果
     *
     * @param templateModel
     * @param content
     * @return
     */
//    public ChatMessage aiNoStream(String templateModel, String content) {
//        return aiNoStream(Collections.singletonList(new ChatMessage(AIRoleEnum.USER.getRoleName(), content)), null, templateModel, 0.8);
//    }

    /**
     * 非流获取ai结果
     *
     * @param messages
     * @param toolExecutionResultMessages
     * @param templateModel
     * @param temperature
     * @return
     */
    public ChatMessage aiNoStream(List<ChatMessage> messages,
                                  List<String> tools,
                                  String templateModel,
                                  Double temperature, AtomicInteger statusCode ) {


        try {

            Map<String, ToolSpecification> methodMap = ToolConfig.methodMap;
            Map<ToolSpecification, DefaultToolExecutor> toolExecutorMap = ToolConfig.toolExecutorMap;


            Map<String,DefaultToolExecutor> executorMap = new HashMap<>();
            List<ToolSpecification> toolSpecifications = new ArrayList<>();
            for (String tool : tools) {
                ToolSpecification toolSpecification = methodMap.get(tool);
                if(toolSpecification==null){
                    log.error("未发现 tool ={}",tool);
                    continue;
                }
                DefaultToolExecutor defaultToolExecutor = toolExecutorMap.get(toolSpecification);
                if(defaultToolExecutor==null){
                    log.error("未发现 tool功能提供者 ={}",tool);
                    continue;
                }
                toolSpecifications.add(toolSpecification);
                executorMap.put(toolSpecification.name(),defaultToolExecutor);
            }
            //ModelConfigVo modelConfig = modelConfigManager.getModelConfig(templateModel);
            //if (modelConfig == null) {
            //    log.error("aiNoStream  error  无可用的渠道 templateModel={}", templateModel);
            //    throw new AIException("无可用的渠道  templateModel={} " + templateModel);
            //}
            String[] split = templateModel.split(":");

            List<dev.langchain4j.data.message.ChatMessage> newMessages = new ArrayList<>();
            for (ChatMessage message : messages) {
                if (message.getRole().equals(AIRoleEnum.SYSTEM.getRoleName())) {
                    SystemMessage systemMessage = new SystemMessage(message.getContent().toString());
                    newMessages.add(systemMessage);
                    continue;
                }
                if (message.getRole().equals(AIRoleEnum.USER.getRoleName())) {
                    UserMessage user = new UserMessage("user", message.getContent().toString());
                    newMessages.add(user);
                    continue;
                }
                if (message.getRole().equals(AIRoleEnum.ASSISTANT.getRoleName())) {
                    AiMessage aiMessage = new AiMessage(message.getContent().toString());
                    newMessages.add(aiMessage);
                }
            }

            String url = getUrl("https://bluecatai.net/");
            String token = "sk-7e3d932bef164aedb8f3a33a90a51e7f";

            OpenAiChatModel modelService = OpenAiChatModel.builder()
                    .apiKey(token)
                    .baseUrl(url)
                    .modelName(split[1])
                    .temperature(temperature)
                    .build();
            Response<AiMessage> generate = modelService.generate(newMessages, toolSpecifications);
            AiMessage content = generate.content();
            List<ToolExecutionRequest> toolExecutionRequests = content.toolExecutionRequests();
            for (ToolExecutionRequest toolExecutionRequest : toolExecutionRequests) {
                // tool的名称
                String name = toolExecutionRequest.name();
                try {
                    DefaultToolExecutor defaultToolExecutor = executorMap.get(name);
                    if (defaultToolExecutor == null) {
                        continue;
                    }
                    if (name.equals("set_status")){
                        String status = defaultToolExecutor.execute(toolExecutionRequest, "default");
                        ReviewStatusEnums enums = ReviewStatusEnums.getEnumByKey(status);
                        statusCode.set(enums.getStatus());
                        log.info("useTool tool={},aDefault={},statusCode={}", name, enums.getDesc(),statusCode);
                    }
                } catch (Exception e) {
                    log.error("useTool error toolName={}", name, e);
                }
            }
            String text = content.text();
            if (text == null) {
                // 命中函数,重新生成回答
                Response<AiMessage> newGenerate = modelService.generate(newMessages);
                AiMessage aiMessage = newGenerate.content();
                text = aiMessage.text();
                return new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), text);
            }
            return new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), text);

        } catch (Exception e) {
            log.error("aiNoStream error templateModel={} ", templateModel, e);
        }
        return null;

    }

    public String getUrl(String cdnHost) {
        return cdnHost.endsWith("/") ? cdnHost + "v1" : cdnHost + "/v1";
    }



    private static String name = "setStatus";

    public DefaultToolExecutor getToolExecutor(List<String> tools) {

        List<DefaultToolExecutor> defaultToolExecutors = new ArrayList<>();
        Map<String, ToolSpecification> methodMap = ToolConfig.methodMap;
        ToolSpecification toolSpecification = methodMap.get(name);
        Map<ToolSpecification, DefaultToolExecutor> toolExecutorMap = ToolConfig.toolExecutorMap;
        return toolExecutorMap.get(toolSpecification);
    }

}
