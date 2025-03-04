package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.ai.manager.CommonAIManager;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理临时打招呼数据任务添加
 *
 * @Date 2025/1/18 00:17
 */
@Slf4j
@Component
public class DealUserFirstSendMessageUtil {

    @Resource
    private AmClientTasksServiceImpl amClientTasksService;

    @Resource
    private AmChatbotGreetConfigServiceImpl amChatbotGreetConfigService;

    @Resource
    private AmChatbotPositionOptionServiceImpl amChatbotPositionOptionService;


    @Resource
    private AmChatMessageServiceImpl amChatMessageService;


    @Resource
    private AmNewMaskServiceImpl amNewMaskService;
    @Resource
    private AmPositionServiceImpl amPositionService;


    @Resource
    private AmResumeServiceImpl amResumeService;


    @Resource
    private CommonAIManager commonAIManager;


    @Resource
    private AmChatbotOptionsConfigServiceImpl amChatbotOptionsConfigService;

    /**
     * 处理临时任务,一次性塞到队列里面执行
     */
    public ResultVO dealBossNewMessage(AmResume amResume, AmZpLocalAccouts amZpLocalAccouts) {
        log.info("ReplyUserMessageDataProcessor dealBossNewMessage amResume={}, bossId={}", amResume, amZpLocalAccouts.getId());
        if (Objects.isNull(amResume) || StringUtils.isBlank(amResume.getEncryptGeekId())) {
            return ResultVO.fail(404, "用户信息异常");
        }
        String taskId = amZpLocalAccouts.getId() + "_" + amResume.getUid();

        LambdaQueryWrapper<AmChatMessage> messagesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messagesLambdaQueryWrapper.in(AmChatMessage::getConversationId, taskId);
        messagesLambdaQueryWrapper.eq(AmChatMessage::getType, 1);
        messagesLambdaQueryWrapper.orderByAsc(AmChatMessage::getCreateTime);
        List<AmChatMessage> amChatMessages = amChatMessageService.list(messagesLambdaQueryWrapper);

        LambdaQueryWrapper<AmChatbotGreetConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatbotGreetConfig::getAccountId, amZpLocalAccouts.getId());
        queryWrapper.eq(AmChatbotGreetConfig::getIsAiOn, 1);
        AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(queryWrapper, false);
        if (Objects.isNull(amChatbotGreetConfig)) {
            log.error("未找到ai跟进对应的配置 或 未开启总开关,bossId={}", amZpLocalAccouts.getId());
            return ResultVO.fail(404, "未找到对应的配置");
        }
        Integer postId = amResume.getPostId();

        if (Objects.isNull(postId)) {
            log.info("postId is null,amResume={}", amResume);
            return ResultVO.fail(404, "未找到对应的岗位配置,不继续走下一个流程");
        }
        LambdaQueryWrapper<AmPosition> positionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        positionLambdaQueryWrapper.eq(AmPosition::getId, postId);
        AmPosition amPosition = amPositionService.getOne(positionLambdaQueryWrapper, false);
        if (Objects.isNull(amPosition)) {
            log.info("amPosition is null,postId={}", postId);
            // 先注释,让流程继续下去
            return ResultVO.fail(404, "未找到对应的岗位配置,不继续走下一个流程");
        }
        // 判断岗位状态
        if (amPosition.getIsOpen() == 0) {
            // 查询岗位岗位关闭继续ai跟进
            LambdaQueryWrapper<AmChatbotOptionsConfig> optionsConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
            optionsConfigLambdaQueryWrapper.eq(AmChatbotOptionsConfig::getAdminId, amZpLocalAccouts.getAdminId());
            AmChatbotOptionsConfig optionsConfigServiceOne = amChatbotOptionsConfigService.getOne(optionsConfigLambdaQueryWrapper, false);
            if (Objects.nonNull(optionsConfigServiceOne) && optionsConfigServiceOne.getIsContinueFollow() == 1) {
                log.info("optionsConfigServiceOne is open,amPosition={}", amPosition);
            } else {
                log.info("optionsConfigServiceOne is not open,amPosition={}", amPosition);
                return ResultVO.fail(404, "岗位未开放,且关闭岗位未开发ai继续跟进, 不继续走下一个流程");
            }
        }


        LambdaQueryWrapper<AmChatbotPositionOption> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AmChatbotPositionOption::getPositionId, postId);
        lambdaQueryWrapper.eq(AmChatbotPositionOption::getAccountId, amZpLocalAccouts.getId());
        AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(lambdaQueryWrapper, false);
        List<ChatMessage> messages = new ArrayList<>();
        AmNewMask amNewMask = null;
        if (Objects.nonNull(amChatbotPositionOption) && Objects.nonNull(amChatbotPositionOption.getAmMaskId())) {
            // 如果有绑定ai角色,则获取ai角色进行回复
            amNewMask = amNewMaskService.getById(amChatbotPositionOption.getAmMaskId());

            if (Objects.nonNull(amNewMask)) {
                String aiPrompt = AiReplyPromptUtil.buildPrompt(amResume, amNewMask);
                if (StringUtils.isBlank(aiPrompt)) {
                    log.info("aiPrompt is null,amNewMask ={}", JSONObject.toJSONString(amNewMask));
                    return ResultVO.fail(404, "提取ai提示词失败,不继续下一个流程");
                }
                ChatMessage chatMessage = new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), aiPrompt);
                messages.add(chatMessage);
            } else {
                log.info("amMask is null,amChatbotPositionOption ={}", JSONObject.toJSONString(amChatbotPositionOption));
                return ResultVO.fail(404, "未找到对应的amMask配置,不继续下一个流程");
            }
        }
        else {
            log.info("amChatbotPositionOption is null,amChatbotPositionOption ={}", JSONObject.toJSONString(amChatbotPositionOption));
            return ResultVO.fail(404, "未找到对应的amChatbotPositionOption配置,不继续下一个流程");
        }

        for (AmChatMessage message : amChatMessages) {
            if (message.getRole().equals(AIRoleEnum.ASSISTANT.getRoleName())) {
                messages.add(new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), message.getContent().toString()));
            } else {
                messages.add(new ChatMessage(AIRoleEnum.USER.getRoleName(), message.getContent().toString()));
            }
        }

        log.info("ReplyUserMessageDataProcessor dealBossNewMessage messages={}", JSONObject.toJSONString(messages));
        // 如果content为空 重试10次
        String content = "";
        AtomicInteger statusCode = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            ChatMessage chatMessage = commonAIManager.aiNoStream(messages, Arrays.asList("set_status"), "OpenAI:gpt-4o-2024-05-13", 0.8,statusCode);
            content = chatMessage.getContent().toString();
            if (StringUtils.isNotBlank(content)) {
                break;
            }
        }
        if (StringUtils.isBlank(content)) {
            log.info("ReplyUserMessageDataProcessor dealBossNewMessage aiNoStream content is null");
            return ResultVO.fail(404, "ai回复内容为空");
        }
        AmClientTasks amClientTasks = new AmClientTasks();
        amClientTasks.setBossId(amZpLocalAccouts.getId());
        amClientTasks.setTaskType(ClientTaskTypeEnums.SEND_MESSAGE.getType());
        amClientTasks.setOrderNumber(ClientTaskTypeEnums.SEND_MESSAGE.getOrder());
        amClientTasks.setCreateTime(LocalDateTime.now());
        amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
        HashMap<String, Object> hashMap = new HashMap<>();
        HashMap<String, Object> searchDataMap = new HashMap<>();
        HashMap<String, Object> messageMap = new HashMap<>();
        hashMap.put("user_id", amResume.getUid());
        if (Objects.nonNull(amResume.getEncryptGeekId())) {
            searchDataMap.put("encrypt_friend_id", amResume.getEncryptGeekId());
        }
        if (Objects.nonNull(amResume.getName())) {
            searchDataMap.put("name", amResume.getName());
        }
        hashMap.put("search_data", searchDataMap);
        messageMap.put("content", content);
        hashMap.put("message", messageMap);
        amClientTasks.setData(JSONObject.toJSONString(hashMap));
        boolean result = amClientTasksService.save(amClientTasks);
        log.info("ReplyUserMessageDataProcessor dealBossNewMessage  amClientTasks ={} result={}", JSONObject.toJSONString(amClientTasks), result);

        if (result) {
            //保存ai的回复
            AmChatMessage aiMockMessages = new AmChatMessage();
            aiMockMessages.setContent(content);
            aiMockMessages.setCreateTime(LocalDateTime.now());
            aiMockMessages.setUserId(Long.parseLong(amZpLocalAccouts.getExtBossId()));
            aiMockMessages.setRole(AIRoleEnum.ASSISTANT.getRoleName());
            aiMockMessages.setConversationId(taskId);
            aiMockMessages.setChatId(UUID.randomUUID().toString());
            // 虚拟的消息数据
            aiMockMessages.setType(-1);
            boolean mockSaveResult = amChatMessageService.save(aiMockMessages);
            log.info("ReplyUserMessageDataProcessor dealBossNewMessage aiMockMessages={} save result={}", JSONObject.toJSONString(aiMockMessages), mockSaveResult);

            // 更新简历状态
            int status = statusCode.get();
            amResume.setType(status);

            // 根据状态发起request_info
            boolean updateResume = amResumeService.updateById(amResume);
            log.info("ReplyUserMessageDataProcessor dealBossNewMessage updateResume={} save result={}", JSONObject.toJSONString(amResume), updateResume);
        }

        return ResultVO.success();
    }

}
