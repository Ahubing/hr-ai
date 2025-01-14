package com.open.ai.eros.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.bean.vo.DocsSource;
import com.open.ai.eros.ai.model.ChatMessageAdaptProcessor;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;
import com.open.ai.eros.ai.model.bean.vo.gpt.GptCompletionRequest;
import com.open.ai.eros.ai.model.processor.AIChatAfterProcessor;
import com.open.ai.eros.ai.model.processor.AIChatBeforeProcessor;
import com.open.ai.eros.ai.model.processor.AIChatCoreProcessor;
import com.open.ai.eros.ai.model.processor.core.AIReplyChatCoreProcessor;
import com.open.ai.eros.ai.processor.MaskChatAfterProcessor;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.config.CustomIdGenerator;
import com.open.ai.eros.common.exception.AIException;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.creator.manager.MaskManager;
import dev.ai4j.openai4j.chat.UserMessage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @类名：AIManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 14:22
 */

@Service
@Component
@Slf4j
public class AIManager {

    @Autowired
    private List<ChatMessageAdaptProcessor> chatMessageAdaptProcessors;

    @Autowired
    private List<AIChatCoreProcessor> aiChatCoreProcessors;

    @Autowired
    private List<AIChatAfterProcessor> aiChatAfterProcessors;

    @Autowired
    private List<AIChatBeforeProcessor> aiChatBeforeProcessors;

    @Autowired
    private MaskManager maskManager;

    @Autowired
    private List<MaskChatAfterProcessor> maskChatAfterProcessors;

    @Autowired
    private AIReplyChatCoreProcessor aiReplyChatCoreProcessor;

    @Autowired
    private CustomIdGenerator customIdGenerator;


    public void apiStartAIChat(String token, GptCompletionRequest completionRequest, SendMessageUtil sendMessageUtil) {

        // todo  利用token转化为 userId
        Long userId = 1820845198201081857L;
        AITextChatVo chatVo = AITextChatVo.builder()
                .template(completionRequest.getTemplate())
                .messages(completionRequest.getMessages())
                .model(completionRequest.getModel())
                .tool(completionRequest.getTool())
                .stream(completionRequest.getStream())
                .build();

        for (ChatMessageAdaptProcessor adaptProcessor : chatMessageAdaptProcessors) {
            boolean match = adaptProcessor.match(chatVo);
            if (match) {
                for (AIChatBeforeProcessor aiChatBeforeProcessor : aiChatBeforeProcessors) {
                    ResultVO<Void> aiChatBefore = aiChatBeforeProcessor.aiChatBefore(chatVo, userId, sendMessageUtil);
                    if (!aiChatBefore.isOk()) {
                        log.error("startAIChat aiChatBefore error chatAfterProcessor={} userId={} msg={} ", aiChatBeforeProcessor, userId, aiChatBefore.getMsg());
                        if (StringUtils.isNoneEmpty(aiChatBefore.getMsg())) {
                            throw new AIException(aiChatBefore.getCode(), aiChatBefore.getMsg());
                        }
                        return;
                    }
                }
                // 适配不同ai模型的消息结构
                ModelProcessorRequest modelProcessorRequest = adaptProcessor.convertMessage(chatVo);
                modelProcessorRequest.setChatVo(chatVo);

                ResultVO<ChatMessageResultVo> textChatResult = aiReplyChatCoreProcessor.textChat(modelProcessorRequest, sendMessageUtil);
                if (textChatResult.isOk() && textChatResult.getData() == null) {
                    break;
                }
                if (!textChatResult.isOk()) {
                    throw new AIException("ai核心聊天链条失败！");
                }
                // todo 待新增计费
                break;
            }
        }

    }


    /**
     * ai聊天
     *
     * @param chatVo
     */
    public void startAIChat(CacheUserInfoVo userInfoVo, AITextChatVo chatVo, SendMessageUtil sendMessageUtil) throws IOException {
        Long userId = userInfoVo.getId();
        try {
            for (ChatMessageAdaptProcessor adaptProcessor : chatMessageAdaptProcessors) {
                boolean match = adaptProcessor.match(chatVo);
                if (match) {

                    for (AIChatBeforeProcessor aiChatBeforeProcessor : aiChatBeforeProcessors) {
                        ResultVO<Void> aiChatBefore = aiChatBeforeProcessor.aiChatBefore(chatVo, userId, sendMessageUtil);
                        if (!aiChatBefore.isOk()) {
                            log.error("startAIChat aiChatBefore error chatAfterProcessor={} userId={} msg={} ", aiChatBeforeProcessor, userId, aiChatBefore.getMsg());
                            if (StringUtils.isNoneEmpty(aiChatBefore.getMsg())) {
                                throw new AIException(aiChatBefore.getCode(), aiChatBefore.getMsg());
                            }
                            return;
                        }
                    }
                    // 适配不同ai模型的消息结构
                    ModelProcessorRequest modelProcessorRequest = adaptProcessor.convertMessage(chatVo);
                    modelProcessorRequest.setChatVo(chatVo);

                    // 聊天交互
                    ChatMessage last = chatVo.getMessages().getLast();

                    if (StringUtils.isEmpty(last.getContent().toString().trim())) {
                        break;
                    }

                    // 开启ai回复之前 插入回复和当前消息id
                    sendMessageUtil.chatIdInfo(chatVo.getChatId(),chatVo.getAiChatMessageId(),chatVo.getMaskId());

                    ResultVO<ChatMessageResultVo> textChatResult = aiReplyChatCoreProcessor.textChat(modelProcessorRequest, sendMessageUtil);
                    if (textChatResult.isOk() && textChatResult.getData() == null) {
                        break;
                    }
                    if (!textChatResult.isOk()) {
                        throw new AIException("ai核心聊天链条失败！");
                    }

                    pushContent(chatVo, sendMessageUtil);

                    ChatMessageResultVo textChatResultData = textChatResult.getData();
                    // 开始计费 保存聊天记录
                    for (AIChatAfterProcessor chatAfterProcessor : aiChatAfterProcessors) {
                        ResultVO<Void> voidResultVO = chatAfterProcessor.aiChatAfter(chatVo, textChatResultData, userInfoVo);
                        if (!voidResultVO.isOk()) {
                            log.error("startAIChat aiChatAfter error chatAfterProcessor={} userId={} textChatResult={} msg={} ", chatAfterProcessor, userId, JSONObject.toJSONString(textChatResult.getData()), voidResultVO.getMsg());
                            throw new AIException(voidResultVO.getMsg());
                        }
                    }

                    if (chatVo.getBMaskVo() != null) {
                        for (MaskChatAfterProcessor maskChatAfterProcessor : maskChatAfterProcessors) {
                            maskChatAfterProcessor.action(chatVo, textChatResult.getData(), userInfoVo);
                        }
                    }
                    break;
                }
            }
        }finally {
            sendMessageUtil.sendMessage(sendMessageUtil.formatAIStr("",true));
        }
    }


    public void pushContent(AITextChatVo chatVo, SendMessageUtil sendMessageUtil) {
        List<DocsSource> docsSources = chatVo.getDocsSources();
        if (CollectionUtils.isNotEmpty(docsSources)) {
            for (DocsSource docsSource : docsSources) {
                // 推送文件来源
                String name = docsSource.getName();
                String url = docsSource.getUrl();
                if (StringUtils.isEmpty(name)) {
                    continue;
                }
                sendMessageUtil.sendMessage(sendMessageUtil.formatAIStr("\n\n检索来源：[" + name + "](" + url + ")",false));
            }
        }
    }

    /**
     * 调试面具
     *
     * @param userInfoVo
     * @param aiTextChatVo
     * @param response
     */
    public void testMask(CacheUserInfoVo userInfoVo, AITextChatVo aiTextChatVo, SendMessageUtil sendMessageUtil) throws IOException {
        Long userId = userInfoVo.getId();
        BMaskVo bMaskVo = aiTextChatVo.getBMaskVo();
        // 禁用词的禁用
        ResultVO resultVO = maskManager.bandWord(bMaskVo.getBannedWords(), aiTextChatVo.getMessages().getLast().getContent().toString());
        if (!resultVO.isOk()) {
            throw new AIException(resultVO.getMsg());
        }
        for (ChatMessageAdaptProcessor adaptProcessor : chatMessageAdaptProcessors) {
            boolean match = adaptProcessor.match(aiTextChatVo);
            if (match) {
                // 适配不同ai模型的消息结构
                ModelProcessorRequest modelProcessorRequest = adaptProcessor.convertMessage(aiTextChatVo);

                for (AIChatCoreProcessor aiChatCoreProcessor : aiChatCoreProcessors) {
                    //回一个空的 data 不会打破循环
                    ChatMessage last = aiTextChatVo.getMessages().getLast();
                    if (StringUtils.isEmpty(last.getContent().toString())) {
                        continue;
                    }
                    ResultVO<ChatMessageResultVo> textChatResult = aiChatCoreProcessor.textChat(modelProcessorRequest, sendMessageUtil);
                    if (textChatResult.isOk() && textChatResult.getData() == null) {
                        continue;
                    }
                    if (!textChatResult.isOk()) {
                        // 如果返回非正常的状态码 直接退出循环
                        break;
                    }
                    ChatMessageResultVo textChatResultData = textChatResult.getData();

                    // 开始计费 保存聊天记录
                    for (AIChatAfterProcessor chatAfterProcessor : aiChatAfterProcessors) {
                        ResultVO<Void> voidResultVO = chatAfterProcessor.aiChatAfter(aiTextChatVo, textChatResultData, userInfoVo);
                        if (!voidResultVO.isOk()) {
                            log.error("testMask chatAfterProcessor error chatAfterProcessor={} userId={} textChatResult={} msg={} ", chatAfterProcessor, userId, JSONObject.toJSONString(textChatResult.getData()), voidResultVO.getMsg());
                        }
                    }
                    if (aiTextChatVo.getBMaskVo() != null) {
                        for (MaskChatAfterProcessor maskChatAfterProcessor : maskChatAfterProcessors) {
                            maskChatAfterProcessor.action(aiTextChatVo, textChatResult.getData(), userInfoVo);
                        }
                    }
                }
                break;
            }
        }
    }

    public String aiChatMessFunction(List<dev.langchain4j.data.message.ChatMessage> messages) {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                .baseUrl("https://zen-vip.zeabur.app/v1")
                .modelName("gpt-4o-2024-05-13")
                .build();


        Response<AiMessage> generate = model.generate(messages);
        AiMessage content = generate.content();
        return content.text();
    }


}
