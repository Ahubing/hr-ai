package com.open.ai.eros.ai.processor;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.common.vo.CacheUserInfoVo;

public interface MaskChatAfterProcessor {



    void action(AITextChatVo chatReq, ChatMessageResultVo messageResultVo, CacheUserInfoVo userInfoVo);


}
