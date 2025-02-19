package com.open.hr.ai.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmChatMessage;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatMessageServiceImpl;
import com.open.hr.ai.bean.vo.AmChatMessageVo;
import com.open.hr.ai.convert.AmChatMessageConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Date 2025/2/5 17:12
 */
@Component
@Slf4j
public class AmMessageManager {

    @Resource
    private AmChatMessageServiceImpl amChatMessageService;

    public ResultVO<List<AmChatMessageVo>> queryChatMessage(String bossId,String userId){
        String conversationId = bossId + "_" +userId;
        LambdaQueryWrapper<AmChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatMessage::getConversationId,conversationId);
        queryWrapper.orderByAsc(AmChatMessage::getCreateTime);
        List<AmChatMessage> amChatMessages = amChatMessageService.list(queryWrapper);
        List<AmChatMessageVo> collect = amChatMessages.stream().map(AmChatMessageConvert.I::convertAmChatMessageVo).collect(Collectors.toList());
        return ResultVO.success(collect);
    }
}
