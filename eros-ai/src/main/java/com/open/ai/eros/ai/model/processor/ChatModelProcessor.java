package com.open.ai.eros.ai.model.processor;


import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;
import com.open.ai.eros.ai.util.SendMessageUtil;

import java.io.IOException;


public interface ChatModelProcessor {

    /**
     * ai聊天
     * @param os
     * @param modelConfigVo
     * @return
     */
    ChatMessageResultVo startAIModel(ModelProcessorRequest request, SendMessageUtil sendMessageUtil, ModelConfigVo modelConfigVo) throws IOException;

    /**
     * 模型匹配规则
     * @param model
     * @param template
     * @return
     */
    boolean match(String model, String template);


    String getUrl(String cdnHost);


    /**
     * 越小的越优先
     * @return
     */
    default int order(){
        return -1;
    }

}
