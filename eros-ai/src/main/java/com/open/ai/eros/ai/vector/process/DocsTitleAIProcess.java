package com.open.ai.eros.ai.vector.process;

import com.open.ai.eros.ai.config.KnowledgeAIConfig;
import com.open.ai.eros.ai.model.bean.vo.gpt.ChatCompletionResult;
import com.open.ai.eros.ai.model.bean.vo.gpt.GptCompletionRequest;
import com.open.ai.eros.ai.util.GptChatModelUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * 方案内的，根据方案名称进行变种
 *
 */
@Slf4j
@Component
public class DocsTitleAIProcess {

    @Autowired
    private KnowledgeAIConfig knowledgeAIConfig;

    private final String prompt2 = "你是问题分析师,输出下面标题的五种变种表达.";


    /**
     * 根据标题 推理更多的标题
     * @param
     * @return
     */
    public List<String> getInferTitle(String title){

        GptCompletionRequest completionRequest = GptCompletionRequest.builder().stream(false)
                .temperature(1.0)
                .model(knowledgeAIConfig.getInferModel())
                .build();

        LinkedList<ChatMessage> messages = new LinkedList<>();

        ChatMessage system = new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(),prompt2);
        ChatMessage user = new ChatMessage(AIRoleEnum.USER.getRoleName(),title);

        messages.add(system);
        messages.add(user);
        completionRequest.setMessages(messages);
        ChatCompletionResult chatCompletionResult = GptChatModelUtil.startChatWithNoStream(completionRequest, knowledgeAIConfig.getToken(), knowledgeAIConfig.getUrl());
        if(chatCompletionResult==null){
            return Collections.EMPTY_LIST;
        }
        try {
            String titles = chatCompletionResult.getChoices().get(0).getMessage().getContent().toString();
            return extractTitles(titles);
        }catch (Exception e){
            log.error("getInferQuestion error content={}",title,e);
        }
        return Collections.EMPTY_LIST;
    }


    public static List<String> extractTitles(String content) {
        List<String> questions = new ArrayList<>();
        // 按行分割原始内容
        String[] lines = content.split("\n");
        // 提取问题并添加到列表中
        for (String line : lines) {
            if (line.matches("^\\d+\\.\\s+.*")) {
                questions.add(line.trim().replaceFirst("^\\d+\\. ", ""));
            }
        }
        return questions;
    }


}
