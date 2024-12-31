package com.open.ai.eros.ai.vector.process;

import com.open.ai.eros.ai.model.bean.vo.gpt.ChatCompletionResult;
import com.open.ai.eros.ai.model.bean.vo.gpt.GptCompletionRequest;
import com.open.ai.eros.ai.util.GptChatModelUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.knowledge.config.KnowledgeAIConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * 处理文章内容推理出用户的问题
 */
@Slf4j
@Component
public class CheckInferQuestionAIProcess {

    private final String prompt = "You are a judge. Can you rate the following answer as the answer to the question [ %s ]?  if it is very in line with the answer, output 90, if it is not in line with the answer, output 0,  just output a number.";


    @Autowired
    private KnowledgeAIConfig knowledgeAIConfig;


    /**
     * 检测该内容是否可以回答下面的问题
     *
     * @param content
     * @return
     */
    public boolean checkInferQuestion(String question,String content){

        GptCompletionRequest completionRequest = GptCompletionRequest.builder().stream(false)
                .temperature(1.0)
                .model(knowledgeAIConfig.getCheckInferModel())
                .build();

        LinkedList<ChatMessage> messages = new LinkedList<>();

        ChatMessage system = new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(),String.format(prompt,question));
        ChatMessage user = new ChatMessage(AIRoleEnum.USER.getRoleName(),content);

        messages.add(system);
        messages.add(user);
        completionRequest.setMessages(messages);
        ChatCompletionResult chatCompletionResult = GptChatModelUtil.startChatWithNoStream(completionRequest, knowledgeAIConfig.getToken(), knowledgeAIConfig.getUrl());
        if(chatCompletionResult==null){
            return false;
        }
        try {
            String answer = chatCompletionResult.getChoices().get(0).getMessage().getContent().toString();
            if(answer.contains("90")){
                return true;
            }
        }catch (Exception e){
            log.error("checkInferQuestion error content={}",content,e);
        }
        return false;
    }

}
