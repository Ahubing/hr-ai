package com.open.hr.ai.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmChatMessage;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author liuzilin
 * @Date 2025/2/5 17:12
 */
@Component
@Slf4j
public class AmMessageManager {

    @Resource
    private AmChatMessageServiceImpl amChatMessageService;

    public ResultVO queryChatMessage(String recruiterId,String userId){
        String conversationId = recruiterId + userId;
        LambdaQueryWrapper<AmChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatMessage::getConversationId,conversationId);
        queryWrapper.orderByAsc(AmChatMessage::getCreateTime);
        return ResultVO.success(amChatMessageService.list(queryWrapper));
    }
}
