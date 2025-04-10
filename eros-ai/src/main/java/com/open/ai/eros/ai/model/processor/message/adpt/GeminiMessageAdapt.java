package com.open.ai.eros.ai.model.processor.message.adpt;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.ModelMessageAdapt;
import com.open.ai.eros.ai.model.bean.vo.gemini.GeminiMessage;
import com.open.ai.eros.ai.model.bean.vo.gemini.GeminiParts;
import com.open.ai.eros.ai.model.bean.vo.gemini.request.GeminiRequest;
import com.open.ai.eros.ai.model.bean.vo.gemini.request.GenerationConfig;
import com.open.ai.eros.ai.model.bean.vo.gemini.request.SafetySettings;
import com.open.ai.eros.ai.model.bean.vo.gemini.request.SystemInstructionParts;
import com.open.ai.eros.ai.util.ChatMessageCacheUtil;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;
import com.open.ai.eros.db.constants.AIRoleEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * @类名：GptMessageAdapt
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/8 22:08
 */

@Order(10)
@Component
public class GeminiMessageAdapt implements ModelMessageAdapt {

    @Override
    public String modelMessage(AITextChatVo req, MaskAIParamVo maskAIParamVo, String model) {
        LinkedList<ChatMessage> userHistoryMessages = req.getMessages();
        ChatMessageCacheUtil.getOkUserChatMessages(userHistoryMessages, model);
        if(CollectionUtils.isEmpty(userHistoryMessages)){
            throw new BizException("用户的输入消息太大！");
        }
        GeminiRequest.GeminiRequestBuilder builder = GeminiRequest.builder().safetySettings(getDefaultSafe());
        GenerationConfig defaultConfig = getDefaultConfig();
        LinkedList<GeminiMessage> messages = new LinkedList<>();
        if(maskAIParamVo !=null){
            LinkedList<ChatMessage> maskMessages = maskAIParamVo.getMessages();
            if(CollectionUtils.isNotEmpty(maskMessages)){
                for (ChatMessage maskMessage : maskMessages) {
                    messages.add(convertGeminiMessage(maskMessage));
                }
            }
            Double temperature = maskAIParamVo.getTemperature();
            if(temperature!=null){
                defaultConfig.setTemperature(temperature);
            }
        }
        builder.generationConfig(defaultConfig);
        // 将用户的上下文 读取
        for (ChatMessage userHistoryMessage : userHistoryMessages) {
            messages.add(convertGeminiMessage(userHistoryMessage));
        }

        GeminiMessage first = messages.getFirst();
        String role = first.getRole();
        if(role.equals(AIRoleEnum.SYSTEM.getRoleName())){
            messages.removeFirst();
            builder.systemInstruction(buildSystemInstruction(first));
        }
        if(messages.isEmpty()){
            throw new BizException("消息格式问题！");
        }

        if(messages.size()%2==0){
            messages.removeFirst();
        }
        int i = 0;
        for (GeminiMessage message : messages) {
            if(i%2==1){
                message.setRole(AIRoleEnum.MODEL.getRoleName());
            }else{
                message.setRole(AIRoleEnum.USER.getRoleName());
            }
            i++;
        }
        builder.contents(messages);
        return JSONObject.toJSONString(builder.build());
    }

    private SystemInstructionParts buildSystemInstruction(GeminiMessage system){
        SystemInstructionParts systemInstructionParts = new SystemInstructionParts();
        GeminiParts geminiParts = system.getParts().get(0);
        systemInstructionParts.setParts(geminiParts);
        return systemInstructionParts;
    }

    private GeminiMessage convertGeminiMessage(ChatMessage chatMessage){
        GeminiMessage message = new GeminiMessage();
        message.setRole(chatMessage.getRole());
        LinkedList<GeminiParts> parts = new LinkedList<>();
        parts.add(new GeminiParts(chatMessage.getContent().toString()));
        message.setParts(parts);
        return message;
    }


    private LinkedList<SafetySettings> getDefaultSafe(){
        LinkedList<SafetySettings> safetySettings = new LinkedList<>();
        SafetySettings safetySetting = new SafetySettings();
        safetySetting.setCategory("HARM_CATEGORY_SEXUALLY_EXPLICIT");
        safetySetting.setThreshold("BLOCK_NONE");
        safetySettings.add(safetySetting);
        return safetySettings;
    }

    private GenerationConfig getDefaultConfig(){
        GenerationConfig config = new GenerationConfig();
        config.setMaxOutputTokens(4096);
        return config;
    }



    @Override
    public boolean match(String model) {
        return model.contains("gemini");
    }
}
