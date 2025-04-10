package com.open.ai.eros.ai.processor.message;

import com.open.ai.eros.ai.processor.ChatMessageSaveProcessor;
import com.open.ai.eros.ai.processor.message.bean.ChatMessageSaveParam;
import com.open.ai.eros.common.vo.ResultVO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * 用于分析当前用户的prompt
 */
@Order(10)
@Component
public class DrawChatMessageSaveProcessor implements ChatMessageSaveProcessor {

    @Override
    public ResultVO after(ChatMessageSaveParam param, HttpServletResponse response) {
        return ResultVO.success();
    }
}
