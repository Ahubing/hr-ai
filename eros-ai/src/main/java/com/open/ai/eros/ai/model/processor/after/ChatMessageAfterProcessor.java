package com.open.ai.eros.ai.model.processor.after;


import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.processor.AIChatAfterProcessor;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.CommonStatusEnum;
import com.open.ai.eros.db.mysql.ai.service.impl.ChatMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 保存用户的聊天记录
 */
@Slf4j
@Order(20)
@Component
public class ChatMessageAfterProcessor implements AIChatAfterProcessor {


    @Autowired
    private ChatMessageServiceImpl chatMessageService;


    @Override
    public ResultVO<Void> aiChatAfter(AITextChatVo chatReq, ChatMessageResultVo messageResultVo, CacheUserInfoVo userInfoVo) {

        if(chatReq.getConversationId()==null || messageResultVo.getChatMessage()==null){
            return ResultVO.success();
        }

        Long userId = userInfoVo.getId();
        try {
            com.open.ai.eros.db.mysql.ai.entity.ChatMessage oldMessage = chatMessageService.getById(chatReq.getAiChatMessageId());
            Long maskId = chatReq.getMaskId();
            String conversationId = chatReq.getConversationId();
            String model = chatReq.getModel();

            ChatMessage chatMessage = messageResultVo.getChatMessage();
            String aiRelyContent = chatMessage.getContent().toString();

            com.open.ai.eros.db.mysql.ai.entity.ChatMessage entity = new com.open.ai.eros.db.mysql.ai.entity.ChatMessage();
            boolean result = false;
            if(oldMessage==null){
                entity.setId(chatReq.getAiChatMessageId());
                entity.setMaskId(maskId);
                entity.setModel(model);
                entity.setContent(aiRelyContent);
                entity.setCreateTime(LocalDateTime.now());
                entity.setUserId(userId);
                entity.setParentId(chatReq.getChatId());
                entity.setRole(chatMessage.getRole());
                entity.setConversationId(conversationId);
                result = chatMessageService.save(entity);
            }else{
                entity.setContent(aiRelyContent);
                entity.setCreateTime(LocalDateTime.now());
                entity.setId(oldMessage.getId());
                entity.setStatus(CommonStatusEnum.OK.getStatus());
                result = chatMessageService.updateById(entity);
            }
            log.info("aiChatAfter userId={} result={}",userId,result);
        }catch (Exception e){
            log.error("ChatMessageProcessor aiChatAfter error userId={}",userId,e);
        }
        return ResultVO.success();
    }
}
