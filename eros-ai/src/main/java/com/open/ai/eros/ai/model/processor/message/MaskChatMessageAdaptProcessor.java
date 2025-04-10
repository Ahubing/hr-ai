package com.open.ai.eros.ai.model.processor.message;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.ChatMessageAdaptProcessor;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;
import com.open.ai.eros.ai.model.manager.MessageManager;
import com.open.ai.eros.common.exception.AIException;
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

@Order(10)
@Slf4j
@Component
public class MaskChatMessageAdaptProcessor implements ChatMessageAdaptProcessor {


    @Autowired
    private MessageManager messageManager;


    @Override
    public ModelProcessorRequest convertMessage(AITextChatVo req) {
        BMaskVo bMaskVo = req.getBMaskVo();
        if(bMaskVo==null){
            throw new AIException("该面具已经失效！");
        }
        MaskAIParamVo aiParam = bMaskVo.getAiParam();
        if(aiParam==null){
            throw new AIException("该面具未设置角色！");
        }


        String template = req.getTemplate();
        String model = req.getModel();
        if(!bMaskVo.getTemplateModel().contains(template+":"+model)){
            throw new AIException("该面具不支持此模型访问！");
        }

        String aiRequest = messageManager.getMessage(req, aiParam, model);
        ModelProcessorRequest request = new ModelProcessorRequest();
        request.setModel(model);
        request.setTemplate(template);
        request.setRequest(aiRequest);
        return request;
    }

    @Override
    public boolean match(AITextChatVo req) {
        return req.getMaskId()!=null;
    }
}
