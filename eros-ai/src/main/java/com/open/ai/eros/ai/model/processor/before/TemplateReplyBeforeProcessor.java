package com.open.ai.eros.ai.model.processor.before;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.processor.AIChatBeforeProcessor;
import com.open.ai.eros.ai.util.NewMessageVo;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.config.CustomIdGenerator;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.ai.service.impl.ChatMessageServiceImpl;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.HitTextDetail;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.TextMatchResult;
import com.open.ai.eros.text.match.model.filterWord.service.FilterWordTextMatchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @类名：TemplateReplyBeforeProessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/23 10:36
 */
@Order(60)
@Component
@Slf4j
public class TemplateReplyBeforeProcessor  implements AIChatBeforeProcessor {


    @Autowired
    private ChatMessageServiceImpl chatMessageService;

    @Autowired
    private CustomIdGenerator customIdGenerator;


    @Autowired
    private FilterWordTextMatchService filterWordTextMatchService;


    @Override
    public ResultVO<Void> aiChatBefore(AITextChatVo chatReq, Long userId, SendMessageUtil sendMessageUtil) {

        try {
            BMaskVo bMaskVo = chatReq.getBMaskVo();
            if(bMaskVo==null || bMaskVo.getTextMatchChannel()==null){
                return ResultVO.success();
            }
            LinkedList<ChatMessage> messages = chatReq.getMessages();
            String userMessage = messages.getLast().getContent().toString();
            TextMatchResult textMatchResult = filterWordTextMatchService.filterWordTextMatch(bMaskVo.getTextMatchChannel(), userMessage);
            if(textMatchResult!=null  && textMatchResult.isHit() && CollectionUtils.isNotEmpty(textMatchResult.getHitTextDetails())){
                List<HitTextDetail> hitTextDetails = textMatchResult.getHitTextDetails();
                Set<String> sendMessage = new HashSet<>();
                List<com.open.ai.eros.db.mysql.ai.entity.ChatMessage> chatMessages = new ArrayList<>();
                for (int i = 0; i < hitTextDetails.size(); i++) {
                    HitTextDetail hitTextDetail = hitTextDetails.get(i);
                    String replyTemplate = hitTextDetail.getReplyTemplate();
                    if(!userMessage.equals(hitTextDetail.getHitText())){
                        continue;
                    }
                    userMessage = userMessage.replace(hitTextDetail.getHitText(),"");

                    if(sendMessage.contains(replyTemplate)){
                        continue;
                    }
                    sendMessage.add(replyTemplate);

                    NewMessageVo.NewMessageVoBuilder builder = NewMessageVo.builder();
                    builder.chatId(chatReq.getChatId());
                    builder.text(replyTemplate);
                    builder.replyId(customIdGenerator.nextId(TemplateReplyBeforeProcessor.class));

                    com.open.ai.eros.db.mysql.ai.entity.ChatMessage chatMessage = new com.open.ai.eros.db.mysql.ai.entity.ChatMessage();
                    long id = customIdGenerator.nextId(com.open.ai.eros.db.mysql.ai.entity.ChatMessage.class);
                    chatMessage.setId(id);
                    chatMessage.setRole(AIRoleEnum.ASSISTANT.getRoleName());
                    chatMessage.setUserId(userId);
                    chatMessage.setModel(chatReq.getModel());
                    chatMessage.setParentId(chatReq.getChatId());
                    chatMessage.setMaskId(bMaskVo.getId());
                    chatMessage.setCreateTime(LocalDateTime.now());
                    chatMessage.setConversationId(chatReq.getConversationId());
                    chatMessage.setContent(replyTemplate);

                    chatMessages.add(chatMessage);
                }
                boolean saveBatch = chatMessageService.saveBatch(chatMessages);
                log.info("aiChatBefore userMessage={} saveBatch={}",userMessage,saveBatch);
                if(saveBatch){
                    //保存成功 开始推送消息
                    for (com.open.ai.eros.db.mysql.ai.entity.ChatMessage chatMessage : chatMessages) {
                        sendMessageUtil.chatIdInfo(chatMessage.getParentId(),chatMessage.getId(),bMaskVo.getId());
                        sendMessageUtil.sendMessage(sendMessageUtil.formatAIStr(chatMessage.getContent(),false));
                    }
                }
                messages.getLast().setContent(userMessage);
            }
        }catch (Exception e){
            log.error("aiChatBefore error chatReq={}",JSONObject.toJSONString(chatReq),e);
        }
        return ResultVO.success();
    }
}
