package com.open.ai.eros.ai.model.processor.message;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.ChatMessageAdaptProcessor;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;
import com.open.ai.eros.ai.model.manager.MessageManager;
import com.open.ai.eros.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

@Order(20)
@Slf4j
@Component
public class UserChatMessageAdaptProcessor implements ChatMessageAdaptProcessor {


    @Autowired
    private MessageManager messageManager;


    @Override
    public ModelProcessorRequest convertMessage(AITextChatVo req) {
        // 如何是常规聊天：传入是这个  aws-claude-3.5-sonnet-20240610
        String model = req.getModel();
        String template = req.getTemplate();

        if(StringUtils.isNoneEmpty(req.getUserPrefix()) || StringUtils.isNoneEmpty(req.getUserSuffix())){
            // 用户的前缀用语
            String format = "%s%s%s";
            com.open.ai.eros.common.vo.ChatMessage last = req.getMessages().getLast();
            last.setContent(
                    String.format(format, getStr(req.getUserPrefix()), last.getContent().toString(), getStr(req.getUserSuffix()))
            );
        }

        String message = messageManager.getMessage(req, null, model);
        if(StringUtils.isEmpty(message)){
            throw new BizException("用户聊天消息转化报错，请联系管理员！");
        }
        ModelProcessorRequest modelProcessorRequest = new ModelProcessorRequest();
        modelProcessorRequest.setModel(model);
        modelProcessorRequest.setTemplate(template);
        modelProcessorRequest.setRequest(message);
        return modelProcessorRequest;
    }

    private String getStr(String prefix){
        if(StringUtils.isEmpty(prefix)){
            return "";
        }
        return prefix;
    }


    @Override
    public boolean match(AITextChatVo req) {
        return req.getMaskId()==null && req.getBMaskVo()==null;
    }
}
