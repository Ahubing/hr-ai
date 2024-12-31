package com.open.ai.eros.ai.model.processor.message.adpt;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.ModelMessageAdapt;
import com.open.ai.eros.ai.model.bean.vo.gpt.GptCompletionRequest;
import com.open.ai.eros.ai.util.ChatMessageCacheUtil;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * 暂时 gpt + claude + command-r 都走该模版
 *
 *
 * @类名：GptMessageAdapt
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/8 22:08
 */

@Order(10)
@Component
public class GptMessageAdapt implements ModelMessageAdapt {


    @Override
    public String modelMessage(AITextChatVo req, MaskAIParamVo maskAIParamVo, String model) {
        LinkedList<ChatMessage> userHistoryMessages = req.getMessages();
        ChatMessageCacheUtil.getOkUserChatMessages(userHistoryMessages, model);
        if(CollectionUtils.isEmpty(userHistoryMessages)){
            throw new BizException("用户的输入消息太大！");
        }

        LinkedList<ChatMessage> messages = new LinkedList<>();
        GptCompletionRequest.GptCompletionRequestBuilder builder = GptCompletionRequest.builder()
                .stream(req.getStream())
                .model(model);
        if(maskAIParamVo !=null){
            List<ChatMessage> maskMessages = maskAIParamVo.getMessages();
            if(CollectionUtils.isNotEmpty(maskMessages)){
                messages.addAll(maskMessages);
            }
            Double temperature = maskAIParamVo.getTemperature();
            builder.temperature(temperature);
        }
        messages.addAll(userHistoryMessages);
        builder.messages(messages);
        return JSONObject.toJSONString(builder.build());
    }

    @Override
    public boolean match(String model) {
        return model.contains("gpt") || model.contains("grok") || model.contains("command-r") || model.contains("claude") || model.contains("eros") ;
    }
}
