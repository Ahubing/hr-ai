package com.open.ai.eros.ai.model.processor.after;


import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.manager.ThreadPoolManager;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.processor.AIChatAfterProcessor;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.ai.service.impl.ModelConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 渠道的计费
 */
@Slf4j
@Order(30)
@Component
public class ModelConfigAfterProcessor implements AIChatAfterProcessor {


    @Autowired
    private ModelConfigServiceImpl modelConfigService;


    @Override
    public ResultVO<Void> aiChatAfter(AITextChatVo chatReq, ChatMessageResultVo messageResultVo, CacheUserInfoVo userInfoVo) {

        if(messageResultVo.getModelConfigVo()==null){
            return ResultVO.success();
        }
        Long modelConfigId = messageResultVo.getModelConfigVo().getId();
        long cost = messageResultVo.getCost();
        ThreadPoolManager.modelConfigPool.execute(()->{
            modelConfigService.updateUsedBalance(modelConfigId,cost);
        });
        return ResultVO.success();
    }

}
