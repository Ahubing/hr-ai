package com.open.ai.eros.ai.model.processor.core;

import com.alibaba.fastjson.JSON;
import com.open.ai.eros.ai.manager.ModelConfigManager;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;
import com.open.ai.eros.ai.model.processor.AIChatCoreProcessor;
import com.open.ai.eros.ai.model.processor.ChatModelProcessor;
import com.open.ai.eros.ai.model.processor.ChatModelProcessorFactory;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @类名：AIReplyChatCoreProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/18 9:29
 */
@Slf4j
@Order(20)
@Component
public class AIReplyChatCoreProcessor implements AIChatCoreProcessor {


    @Autowired
    private ModelConfigManager modelConfigManager;

    /**
     * ai文字聊天
     */
    @Override
    public ResultVO<ChatMessageResultVo> textChat(ModelProcessorRequest request, SendMessageUtil sendMessageUtil) {
        String template = request.getTemplate();
        try {
            ChatModelProcessor modelProcessor = ChatModelProcessorFactory.getChatModelProcessor(request.getModel(), template);
            if(modelProcessor==null){
                return ResultVO.fail("不支持该AI调用");
            }
            ModelConfigVo modelConfig = modelConfigManager.getModelConfig(template,request.getModel());
            if(modelConfig==null){
                log.error("textChat 无可用渠道 error template ={},model={} ",template,request.getModel());
                return ResultVO.fail("无可用渠道");
            }
            ChatMessageResultVo resultVo = modelProcessor.startAIModel(request, sendMessageUtil, modelConfig);
            if(resultVo==null){
                return ResultVO.fail("访问AI错误");
            }
            //sendMessageUtil.sendMessage(SendMessageUtil.END_FLAG);
            resultVo.setModelConfigVo(modelConfig);
            return ResultVO.success(resultVo);
        }catch (Exception e){
            log.error("gptTextChat error request={},template={}", JSON.toJSONString(request), template,e);
            //sendMessageUtil.sendMessage(SendMessageUtil.END_FLAG);
        }
        return ResultVO.fail("ai访问错误！");
    }


}
