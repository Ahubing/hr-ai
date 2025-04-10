package com.open.ai.eros.ai.model.processor;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;

public interface AIChatAfterProcessor {


    /**
     * ai完成后的操作
     * @param messageResultVo
     * @return
     */
    ResultVO<Void> aiChatAfter(AITextChatVo chatReq, ChatMessageResultVo messageResultVo, CacheUserInfoVo userInfoVo);


}
