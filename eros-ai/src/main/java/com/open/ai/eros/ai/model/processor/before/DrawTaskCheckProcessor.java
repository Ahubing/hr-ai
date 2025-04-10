package com.open.ai.eros.ai.model.processor.before;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.constatns.AIConstants;
import com.open.ai.eros.ai.model.processor.AIChatBeforeProcessor;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @类名：DrawTaskCheckProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/31 0:02
 */

@Order(10)
@Component
public class DrawTaskCheckProcessor  implements AIChatBeforeProcessor {


    @Autowired
    private RedisClient redisClient;

    @Override
    public ResultVO<Void> aiChatBefore(AITextChatVo chatReq, Long userId, SendMessageUtil sendMessageUtil) {
        Long chatId = chatReq.getChatId();
        String key = String.format(AIConstants.chatMessageTask, chatId);
        if(redisClient.exists(key)){
            // 当前对话已经使用 异步画图模型 无需二次调用文字模型
            return ResultVO.fail();
        }
        return ResultVO.success();
    }
}
