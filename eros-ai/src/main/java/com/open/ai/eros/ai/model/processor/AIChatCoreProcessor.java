package com.open.ai.eros.ai.model.processor;

import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.vo.ResultVO;

/**
 * @类名：AIChatCoreProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/18 9:27
 */
public interface AIChatCoreProcessor {

    /**
     * 返回一个空的 data 不会打破循环
     * @param request
     * @param sendMessageUtil
     * @return
     */
    ResultVO<ChatMessageResultVo> textChat(ModelProcessorRequest request, SendMessageUtil sendMessageUtil);
}
