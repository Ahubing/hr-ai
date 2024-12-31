package com.open.ai.eros.ai.model.processor;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.vo.ResultVO;

public interface AIChatBeforeProcessor {


    /**
     * ai完成之前的操作
     * @param userId
     * @return
     */
    ResultVO<Void> aiChatBefore(AITextChatVo chatReq, Long userId, SendMessageUtil sendMessageUtil);


}
