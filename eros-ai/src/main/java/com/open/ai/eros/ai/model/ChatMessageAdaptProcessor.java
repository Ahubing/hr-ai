package com.open.ai.eros.ai.model;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;

public interface ChatMessageAdaptProcessor {


    /**
     * 转化为消息
     * @param req
     * @return
     */
    ModelProcessorRequest convertMessage(AITextChatVo req);


    /**
     * @param req
     * @return
     */
    boolean match(AITextChatVo req);


}
