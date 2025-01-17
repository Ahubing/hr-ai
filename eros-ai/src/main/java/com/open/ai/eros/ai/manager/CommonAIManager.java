package com.open.ai.eros.ai.manager;

import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public ChatMessage aiNoStream(String templateModel, String content) {
        return aiNoStream(Collections.singletonList(new ChatMessage(AIRoleEnum.USER.getRoleName(), content)), null, templateModel, 0.8);
    }

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
                                  List<dev.langchain4j.data.message.ChatMessage> toolExecutionResultMessages,
                                  String templateModel,
                                  Double temperature) {

        try {
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
            if (CollectionUtils.isNotEmpty(toolExecutionResultMessages)) {
                newMessages.addAll(toolExecutionResultMessages);
            }

            String url = getUrl("https://bluecatai.net/");
            String token = "sk-7e3d932bef164aedb8f3a33a90a51e7f";

            OpenAiChatModel modelService = OpenAiChatModel.builder()
                    .apiKey(token)
                    .baseUrl(url)
                    .modelName(split[1])
                    .temperature(temperature)
                    .build();
            Response<AiMessage> generate = modelService.generate(newMessages);
            String text = generate.content().text();
            return new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), text);
        } catch (Exception e) {
            log.error("aiNoStream error templateModel={} ", templateModel, e);
        }
        return null;

    }

    public String getUrl(String cdnHost) {
        return cdnHost.endsWith("/") ? cdnHost + "v1" : cdnHost + "/v1";
    }


}
