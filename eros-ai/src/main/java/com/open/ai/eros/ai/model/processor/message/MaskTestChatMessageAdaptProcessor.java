package com.open.ai.eros.ai.model.processor.message;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.ChatMessageAdaptProcessor;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;
import com.open.ai.eros.ai.model.manager.MessageManager;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @类名：UserChatMessageAdaptProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/8 23:12
 */

@Order(-1)
@Slf4j
@Component
public class MaskTestChatMessageAdaptProcessor implements ChatMessageAdaptProcessor {


    @Autowired
    private MessageManager messageManager;


    @Override
    public ModelProcessorRequest convertMessage(AITextChatVo req) {
        BMaskVo bMaskVo = req.getBMaskVo();
        MaskAIParamVo aiParam = bMaskVo.getAiParam();
        if(aiParam==null){
            throw new BizException("该面具未设置角色！");
        }
        String model = req.getModel();
        String template = req.getTemplate();

        ChatMessage last = req.getMessages().getLast();

        String aiRequest = messageManager.getMessage(req, aiParam, model);
        ModelProcessorRequest request = new ModelProcessorRequest();
        request.setModel(model);
        request.setTemplate(template);
        request.setRequest(aiRequest);
        return request;
    }

    @Override
    public boolean match(AITextChatVo req) {
        return req.getMaskId() == null && req.getBMaskVo()!=null;
    }
}
