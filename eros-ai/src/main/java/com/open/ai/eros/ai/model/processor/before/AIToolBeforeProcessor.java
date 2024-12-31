package com.open.ai.eros.ai.model.processor.before;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.lang.chain.bean.UseToolResult;
import com.open.ai.eros.ai.lang.chain.service.ToolService;
import com.open.ai.eros.ai.model.processor.AIChatBeforeProcessor;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：AIToolBeforeProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/15 16:21
 */

@Slf4j
@Order(80)
@Component
public class AIToolBeforeProcessor implements AIChatBeforeProcessor {


    @Autowired
    private ToolService toolService;


    @Override
    public ResultVO<Void> aiChatBefore(AITextChatVo chatReq, Long userId, SendMessageUtil sendMessageUtil) {
        BMaskVo bMaskVo = chatReq.getBMaskVo();

        List<String> tool = new ArrayList<>();

        if (bMaskVo != null && !CollectionUtils.isEmpty(bMaskVo.getTool())) {
            tool.addAll(bMaskVo.getTool());
        }
        if (!CollectionUtils.isEmpty(chatReq.getTool())) {
            tool.addAll(chatReq.getTool());
        }

        if (CollectionUtils.isEmpty(tool)) {
            return ResultVO.success();
        }
        ChatMessage userMessage = chatReq.getMessages().getLast();
        try {
            String user = userMessage.getContent().toString();
            if (StringUtils.isEmpty(user)) {
                return ResultVO.success();
            }
            ResultVO<UseToolResult> result = toolService.useTool(user, tool, chatReq.getTemplate(), chatReq.getModel());
            if (!result.isOk() || CollectionUtils.isEmpty(result.getData().getChatMessages())) {
                return ResultVO.success();
            }
            List<dev.langchain4j.data.message.ChatMessage> data = result.getData().getChatMessages();
            chatReq.setToolExecutionResultMessages(data);
            chatReq.getTokenUsages().add(result.getData().getTokenUsage());
        } catch (Exception e) {
            log.error("AIToolBeforeProcessor error userMessage={} ", JSONObject.toJSON(userMessage), e);
        }
        return ResultVO.success();
    }

}
