package com.open.ai.eros.ai.model.processor.ai;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.constatns.ModelTemplateEnum;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;
import com.open.ai.eros.ai.model.bean.vo.gpt.GptCompletionChunk;
import com.open.ai.eros.ai.model.bean.vo.gpt.GptCompletionRequest;
import com.open.ai.eros.ai.model.processor.BaseModelProcessor;
import com.open.ai.eros.ai.model.processor.ChatModelProcessor;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.ai.util.TokenUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @类名：GptChatProcossor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/3 15:01
 */

@Component
@Slf4j
public class GptChatProcessor  extends BaseModelProcessor implements ChatModelProcessor {


    @Override
    public ChatMessageResultVo startAIModel(ModelProcessorRequest modelProcessorRequest, SendMessageUtil sendMessageUtil, ModelConfigVo modelConfigVo) throws IOException {
        String url= getUrl(modelConfigVo.getBaseUrl());
        String token= modelConfigVo.getToken();
        GptCompletionRequest gptCompletionRequest = JSONObject.parseObject(modelProcessorRequest.getRequest(),GptCompletionRequest.class);
        LinkedList<ChatMessage> messages = gptCompletionRequest.getMessages();
        String model = gptCompletionRequest.getModel();
        gptCompletionRequest.setStream(true);

        //AITextChatVo chatVo = modelProcessorRequest.getChatVo();
        //if(chatVo.getChatId()!=null){
        //    sendMessageUtil.sendMessage(String.format(SendMessageUtil.CHAT_ID,chatVo.getChatId()));
        //}
        AtomicReference<Long> firstRTime = new AtomicReference<>();

        try {
            long currentTime = System.currentTimeMillis();
            final String[] message2 = new String[1];
            List<dev.langchain4j.data.message.ChatMessage> newMessages = new ArrayList<>();
            for (ChatMessage message : messages) {
                if(message.getRole().equals(AIRoleEnum.SYSTEM.getRoleName())){
                    SystemMessage systemMessage = new SystemMessage(message.getContent().toString());
                    newMessages.add(systemMessage);
                    continue;
                }
                if(message.getRole().equals(AIRoleEnum.USER.getRoleName())){
                    UserMessage user = new UserMessage("user", message.getContent().toString());
                    newMessages.add(user);
                    continue;
                }
                if(message.getRole().equals(AIRoleEnum.ASSISTANT.getRoleName())){
                    AiMessage aiMessage = new AiMessage(message.getContent().toString());
                    newMessages.add(aiMessage);
                }
            }

            List<dev.langchain4j.data.message.ChatMessage> toolExecutionResultMessages = modelProcessorRequest.getChatVo().getToolExecutionResultMessages();
            if(CollectionUtils.isNotEmpty(toolExecutionResultMessages)){
                newMessages.addAll(toolExecutionResultMessages);
            }

            if(!gptCompletionRequest.getStream()){
                OpenAiChatModel modelService = OpenAiChatModel.builder()
                        .apiKey(token)
                        .baseUrl(url)
                        .modelName(model)
                        .build();

                Response<AiMessage> generate = modelService.generate(newMessages);
                String text = generate.content().text();
                sendMessageUtil.sendMessage(sendMessageUtil.formatAIStr(text,false));
                message2[0] = text;
            }else{
                StreamingChatLanguageModel modelService = OpenAiStreamingChatModel.builder()
                        .apiKey(token)
                        .baseUrl(url)
                        .modelName(model)
                        .build();

                CountDownLatch countDown  = new CountDownLatch(1);
                modelService.generate(newMessages, new StreamingResponseHandler<AiMessage>() {
                    @Override
                    public void onNext(String token) {
                        sendMessageUtil.sendMessage(sendMessageUtil.formatAIStr(token,false));
                    }
                    @Override
                    public void onComplete(dev.langchain4j.model.output.Response<AiMessage> response) {
                        log.info("onComplete text={}",response.content().text());
                        message2[0] = response.content().text();
                        countDown.countDown();
                    }
                    @Override
                    public void onError(Throwable error) {
                        log.error("gpt startAIModel error ",error);
                        countDown.countDown();
                    }
                });
                countDown.await(500, TimeUnit.SECONDS);
            }

            //Long aiChatMessageId = chatVo.getAiChatMessageId();
            //if(aiChatMessageId!=null){
            //    sendMessageUtil.sendMessage(String.format(SendMessageUtil.REPLY_ID,aiChatMessageId));
            //}
            if(StringUtils.isEmpty(message2[0])){
                log.error("gpt chat message is null ");
                return null;
            }

            ChatMessage chatMessage = new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), message2[0]);
            int promptTokenNumber = TokenUtil.countTokenMessages(messages, model);;
            int relyTokenNumber = TokenUtil.countTokenText(Objects.nonNull(chatMessage.getContent()) ? chatMessage.getContent().toString() : "", model);;

            return ChatMessageResultVo.builder()
                    .chatMessage(chatMessage)
                    .model(model)
                    .promptTokenNumber(promptTokenNumber)
                    .relyTokenNumber(relyTokenNumber)
                    .firstRelyTime(firstRTime.get())
                    .costTime(System.currentTimeMillis() - currentTime)
                    .build();
        }catch (Exception e){
            log.error("gptChat error messages={} ", JSONObject.toJSONString(gptCompletionRequest),e);
        }
        return null;
    }

    /**
     * 目前除去 谷歌模型都是走 open ai 结构
     * @param model
     * @param template
     * @return
     */
    @Override
    public boolean match(String model, String template) {
        return  ModelTemplateEnum.AZURE_API_GPT.getTemplate().equals(template)
                ||
                ModelTemplateEnum.OPEN_AI_API_GPT.getTemplate().equals(template)
                ||
                ModelTemplateEnum.COHERE_API_GPT.getTemplate().equals(template)
                ||
                model.contains("claude")
                ||
                ModelTemplateEnum.EROS_AI.getTemplate().equals(template)
                ;
    }

    @Override
    public String getUrl(String cdnHost) {
        return cdnHost.endsWith("/") ? cdnHost + "v1" : cdnHost + "/v1";
        //return cdnHost.endsWith("/") ? cdnHost + "v1/chat/completions" : cdnHost + "/v1/chat/completions";
    }




    public static GptCompletionChunk convert(String answer) {
        if (StringUtils.isNoneEmpty(answer) && !"data: [DONE]".equals(answer)) {
            String beanStr = answer.replaceFirst("data: ", "");
            try {
                return JSONObject.parseObject(beanStr, GptCompletionChunk.class);
            } catch (Exception e) {
                log.error("convert error answer={}", answer, e);
            }
        }
        return null;
    }


}
