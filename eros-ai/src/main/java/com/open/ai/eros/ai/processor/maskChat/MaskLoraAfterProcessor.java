package com.open.ai.eros.ai.processor.maskChat;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.processor.MaskChatAfterProcessor;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @类名：MaskLoraProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/29 19:29
 */

/**
 * 面具的彩蛋的
 */
@Order(1)
@Slf4j
@Component
public class MaskLoraAfterProcessor implements MaskChatAfterProcessor {


    @Override
    public void action(AITextChatVo chatReq, ChatMessageResultVo messageResultVo, CacheUserInfoVo userInfoVo) {
        BMaskVo bMaskVo = chatReq.getBMaskVo();
        // todo 彩蛋逻辑
//        String lora = bMaskVo.getLora().toString();
//        if(StringUtils.isEmpty(lora)){
//            return;
//        }

    }
}
