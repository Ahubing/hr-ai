package com.open.hr.ai.processor.bossNewMessage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatMessageServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatbotGreetMessagesServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmClientTasksServiceImpl;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import com.open.hr.ai.constant.MessageTypeEnums;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用于分析当前用户的prompt
 */
@Order(0)
@Component
@Slf4j
public class SaveMessageDataProcessor implements BossNewMessageProcessor {

    @Resource
    private AmClientTasksServiceImpl amClientTasksService;

    @Resource
    private AmChatMessageServiceImpl amChatMessageService;
    /**
     * 过滤保存用户的消息
     */
    @Override
    public ResultVO dealBossNewMessage(String platform,AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {
//        // 过滤和保存
//        List<ChatMessage> messages = req.getMessages();
//        // 过滤出消息id
//        List<String> messageIds = messages.stream().map(ChatMessage::getId).collect(Collectors.toList());
//        // 查询数据库中是否存在消息id
//        LambdaQueryWrapper<AmChatMessage> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.in(AmChatMessage::getChatId, messageIds);
//        List<AmChatMessage> amChatMessages = amChatMessageService.list(queryWrapper);
//        // 过滤出已经存在的消息id
//        for (ChatMessage message : messages) {
//            // 判断是否存在消息id, 不存在则构建插入
//            if (amChatMessages.stream().noneMatch(amChatbotGreetMessage -> amChatbotGreetMessage.getChatId().equals(message.getId()))) {
//                AmChatMessage greetMessages = new AmChatMessage();
//                greetMessages.setContent(message.getContent().toString());
//                greetMessages.setCreateTime(LocalDateTime.now());
//                if (message.getRole().equals("user")) {
//                    greetMessages.setUserId(Long.parseLong(req.getUser_id()));
//                    greetMessages.setRole(AIRoleEnum.USER.getRoleName());
//                } else {
//                    greetMessages.setUserId(Long.parseLong(req.getRecruiter_id()));
//                    greetMessages.setRole(AIRoleEnum.ASSISTANT.getRoleName());
//                }
//                //  招聘账号id + 用户id作为任务id
//                String taskId = req.getRecruiter_id() +"_"+ req.getUser_id();
//                greetMessages.setConversationId(taskId);
//                greetMessages.setChatId(message.getId());
//                greetMessages.setCreateTime(LocalDateTime.now());
//                boolean result = amChatMessageService.save(greetMessages);
//                log.info("SaveMessageDataProcessor dealBossNewMessage greetMessages={} save result={}",JSONObject.toJSONString(greetMessages), result);
//            }
//        }

        String conversationId = amZpLocalAccouts.getId() +"_"+ req.getUser_id();
        // 先清空mock的数据
        LambdaQueryWrapper<AmChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatMessage::getConversationId, conversationId);
        queryWrapper.eq(AmChatMessage::getType,-1);
        amChatMessageService.remove(queryWrapper);
        return ResultVO.success();
    }


}
