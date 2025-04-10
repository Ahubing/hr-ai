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
 * 处理文章内容推理出用户的问题
 */
@Slf4j
@Component
public class ContentInferQuestionAIProcess {

    private final String prompt = "you are an expert in question reasoning. Below is the answer to the question. Based on the complete content of the answer, infer the most likely question the user will ask, and sort it by probability. Only the top three points are output. Please give the Chinese answer.";

    private final String prompt2 = "You are an expert in question reasoning. First, analyze the theme of the content. Then, based on the theme, infer the most likely questions that users will ask about the following content. Only output the questions that best match the first five points. Please answer in Chinese.";

    @Autowired
    private KnowledgeAIConfig knowledgeAIConfig;


    /**
     * 根据内容 推理出问题
     * @param content
     * @return
     */
    public List<String> getInferQuestion(String content){

        GptCompletionRequest completionRequest = GptCompletionRequest.builder().stream(false)
                .temperature(1.0)
                .model(knowledgeAIConfig.getInferModel())
                .build();

        LinkedList<ChatMessage> messages = new LinkedList<>();

        ChatMessage system = new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(),prompt2);
        ChatMessage user = new ChatMessage(AIRoleEnum.USER.getRoleName(),content);

        messages.add(system);
        messages.add(user);
        completionRequest.setMessages(messages);
        ChatCompletionResult chatCompletionResult = GptChatModelUtil.startChatWithNoStream(completionRequest, knowledgeAIConfig.getToken(), knowledgeAIConfig.getUrl());
        if(chatCompletionResult==null){
            return Collections.EMPTY_LIST;
        }
        try {
            String question = chatCompletionResult.getChoices().get(0).getMessage().getContent().toString();
            return extractQuestions(question);
        }catch (Exception e){
            log.error("getInferQuestion error content={}",content,e);
        }
        return Collections.EMPTY_LIST;
    }


    public static List<String> extractQuestions(String content) {
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
