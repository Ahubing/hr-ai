package com.open.ai.eros.ai.model.processor.before;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.processor.AIChatBeforeProcessor;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.creator.manager.MaskManager;
import com.open.ai.eros.db.constants.ConversationTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @类名：MaskBeforeProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/24 12:37
 */

@Order(40)
@Component
@Slf4j
public class MaskBeforeProcessor  implements AIChatBeforeProcessor {


    @Autowired
    private MaskManager maskManager;

    @Override
    public ResultVO<Void> aiChatBefore(AITextChatVo chatReq, Long userId,SendMessageUtil sendMessageUtil) {

        if(!ConversationTypeEnum.MASK.getType().equals(chatReq.getConversationType())){
            return ResultVO.success();
        }

        Long maskId = chatReq.getMaskId();
        if(maskId==null){
            return ResultVO.success();
        }
        BMaskVo bMaskVo = maskManager.getCacheBMaskById(maskId);
        if(bMaskVo==null){
            throw new BizException("该面具已经失效！");
        }
        chatReq.setBMaskVo(bMaskVo);
        chatReq.setContentNumber(bMaskVo.getContentsNumber());
        return ResultVO.success();
    }

}
