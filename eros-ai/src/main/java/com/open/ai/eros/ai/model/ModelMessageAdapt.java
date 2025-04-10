package com.open.ai.eros.ai.model;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;

public interface ModelMessageAdapt {


    /**
     * @param maskAIParamVo 面具的prompt
     * @return
     */
    String modelMessage(AITextChatVo req, MaskAIParamVo maskAIParamVo, String model);


    boolean match(String model);

}
