package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.ai.manager.CommonAIManager;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.util.AIJsonUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.ai.eros.common.constants.ClientTaskTypeEnums;
import com.open.hr.ai.processor.bossNewMessage.ReplyUserMessageDataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private IcRecordServiceImpl recordService;

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

    @Resource
    private ReplyUserMessageDataProcessor replyUserMessageDataProcessor;

    /**
     * 处理临时任务,一次性塞到队列里面执行
     */
    public ResultVO dealBossNewMessage(AmResume amResume, AmZpLocalAccouts amZpLocalAccouts) {
        log.info("DealUserFirstSendMessageUtil dealBossNewMessage uid={}, bossId={}", amResume.getUid(), amZpLocalAccouts.getId());
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
            log.info("DealUserFirstSendMessageUtil postId is null,amResume={}", amResume);
            return ResultVO.fail(404, "未找到对应的岗位配置,不继续走下一个流程");
        }
        LambdaQueryWrapper<AmPosition> positionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        positionLambdaQueryWrapper.eq(AmPosition::getId, postId);
        AmPosition amPosition = amPositionService.getOne(positionLambdaQueryWrapper, false);
        if (Objects.isNull(amPosition)) {
            log.info("DealUserFirstSendMessageUtil amPosition is null,postId={}", postId);
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
                log.info("DealUserFirstSendMessageUtil optionsConfigServiceOne is open,amPosition={}", amPosition);
            } else {
                log.info("DealUserFirstSendMessageUtil optionsConfigServiceOne is not open,amPosition={}", amPosition);
                return ResultVO.fail(404, "岗位未开放,且关闭岗位未开发ai继续跟进, 不继续走下一个流程");
            }
        }


        LambdaQueryWrapper<AmChatbotPositionOption> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AmChatbotPositionOption::getPositionId, postId);
        lambdaQueryWrapper.eq(AmChatbotPositionOption::getAccountId, amZpLocalAccouts.getId());
        AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(lambdaQueryWrapper, false);

        IcRecord icRecord = recordService.getOneNormalIcRecord(amResume.getUid(),amZpLocalAccouts.getAdminId(),amResume.getAccountId(),amResume.getPostId());
        log.info("DealUserFirstSendMessageUtil icRecord={}", JSONObject.toJSONString(icRecord));
        log.info("DealUserFirstSendMessageUtil icRecord query params adminId:{} positionId:{} accountId:{} employeeUid:{}", amZpLocalAccouts.getAdminId(), amResume.getPostId(), amResume.getAccountId(), amResume.getUid());

        List<ChatMessage> messages = new ArrayList<>();
        AmNewMask amNewMask = null;
        if (Objects.nonNull(amChatbotPositionOption) && Objects.nonNull(amChatbotPositionOption.getAmMaskId())) {
            // 如果有绑定ai角色,则获取ai角色进行回复
            amNewMask = amNewMaskService.getById(amChatbotPositionOption.getAmMaskId());

            if (Objects.nonNull(amNewMask)) {
                String aiPrompt = AiReplyPromptUtil.buildBasePrompt(amResume, amNewMask, icRecord);
                if (StringUtils.isBlank(aiPrompt)) {
                    log.info("DealUserFirstSendMessageUtil aiPrompt is null,amNewMask ={}", JSONObject.toJSONString(amNewMask));
                    return ResultVO.fail(404, "提取ai提示词失败,不继续下一个流程");
                }
                ChatMessage chatMessage = new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(), aiPrompt);
                messages.add(chatMessage);
                String candidateBasePrompt = AiReplyPromptUtil.buildCandidateBasePrompt(amResume, amNewMask, icRecord);
                ChatMessage candidateBaseChatMessage = new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(), candidateBasePrompt);
                messages.add(candidateBaseChatMessage);
                String formatAndICRecordPrompt = AiReplyPromptUtil.buildFormatAndICRecordPrompt(amResume, amNewMask, icRecord);
                ChatMessage formatAndICRecordPromptChatMessage = new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(), formatAndICRecordPrompt);
                messages.add(formatAndICRecordPromptChatMessage);
            } else {
                log.info("DealUserFirstSendMessageUtil amMask is null,amChatbotPositionOption ={}", JSONObject.toJSONString(amChatbotPositionOption));
                return ResultVO.fail(404, "未找到对应的amMask配置,不继续下一个流程");
            }
        } else {
            log.info("DealUserFirstSendMessageUtil amChatbotPositionOption is null,amChatbotPositionOption ={}", JSONObject.toJSONString(amChatbotPositionOption));
            return ResultVO.fail(404, "未找到对应的amChatbotPositionOption配置,不继续下一个流程");
        }
        //相关参数信息
        JSONObject params = new JSONObject();
        params.put("maskId", amNewMask.getId());
        params.put("adminId", amZpLocalAccouts.getAdminId());
        params.put("employeeUid", amResume.getUid());
        params.put("positionId", amResume.getPostId());
        params.put("accountId", amResume.getAccountId());
        params.put("interviewId",icRecord != null ? icRecord.getId() : null);
//        String preParams = "请记住下列参数和数据，后续会用到。当前角色的面具id maskId(String类型):" + amNewMask.getId() +
//                                ",当前管理员/hr的id adminId(String类型):" + amZpLocalAccouts.getAdminId() +
//                                ",当前求职者uid employeeUid(String类型):" + amResume.getUid() +
//                                ",当前招聘的职位id positionId(String类型):" + amResume.getPostId() +
//                                ",当前角色所登录的平台账号的id accountId:(String类型)" + amResume.getAccountId() +
//                                ",当前的时间是:" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String preParams = "当前的时间是:" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        log.info("DealUserFirstSendMessageUtil ai pre params:" + preParams);
        messages.add(new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(), preParams));
        for (AmChatMessage message : amChatMessages) {
            if (message.getRole().equals(AIRoleEnum.ASSISTANT.getRoleName())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message",Collections.singletonList(message.getContent()));
                messages.add(new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), jsonObject.toJSONString()));
            } else {
                messages.add(new ChatMessage(AIRoleEnum.USER.getRoleName(), message.getContent()));
            }
        }

        log.info("DealUserFirstSendMessageUtil dealBossNewMessage messages={}", JSONObject.toJSONString(messages));
        // 如果content为空 重试10次
        String content = "";
        AtomicInteger needToReply = new AtomicInteger(1);
        AtomicInteger statusCode = new AtomicInteger(amResume.getType());
        AtomicBoolean isAiSetStatus = new AtomicBoolean(false);
        for (int i = 0; i < 10; i++) {
            //AmNewMask amNewMask = maskMapper.selectById(maskId);
            //AmModel amModel = amModelMapper.selectById(amNewMask.getModelId());
            ChatMessage chatMessage = commonAIManager.aiNoStream(messages, Arrays.asList("set_status","get_spare_time","appoint_interview","cancel_interview","modify_interview_time"),
                    "OpenAI:deepseek-r1", 0.8,
                    statusCode,needToReply,isAiSetStatus,params);
            if (Objects.isNull(chatMessage)) {
                continue;
            }
            log.info("ReplyUserMessageDataProcessor printf chatMessage={}", JSONObject.toJSONString(chatMessage));
            content = chatMessage.getContent().toString();
            if (StringUtils.isNotBlank(content)) {
                break;
            }
        }
        if (needToReply.get() == 0){
            log.info("DealUserFirstSendMessageUtil dealBossNewMessage aiNoStream needToReply is 0");
            return ResultVO.success();
        }
        if (StringUtils.isBlank(content)) {
            log.info("DealUserFirstSendMessageUtil dealBossNewMessage aiNoStream content is null");
            return ResultVO.fail(404, "ai回复内容为空");
        }
        // 对content 消息内容 删除包含</think>之前的内容
        if (content.contains("</think>")) {
            content = content.substring(content.indexOf("</think>") + 8);
        }
        AmClientTasks amClientTasks = new AmClientTasks();
        String clientTaskId = UUID.randomUUID().toString();
        amClientTasks.setId(clientTaskId);
        amClientTasks.setBossId(amZpLocalAccouts.getId());
        amClientTasks.setTaskType(ClientTaskTypeEnums.SEND_MESSAGE.getType());
        amClientTasks.setOrderNumber(ClientTaskTypeEnums.SEND_MESSAGE.getOrder());
        amClientTasks.setSubType(ClientTaskTypeEnums.SEND_MESSAGE.getType());
        amClientTasks.setCreateTime(LocalDateTime.now());
        amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
        HashMap<String, Object> hashMap = new HashMap<>();
        HashMap<String, Object> searchDataMap = new HashMap<>();
        hashMap.put("user_id", amResume.getUid());
        if (Objects.nonNull(amResume.getEncryptGeekId())) {
            searchDataMap.put("encrypt_friend_id", amResume.getEncryptGeekId());
        }
        if (Objects.nonNull(amResume.getName())) {
            searchDataMap.put("name", amResume.getName());
        }

        List<AmChatMessage> aiMessages = new ArrayList<>();
        try {
            if (Objects.nonNull(params.get("reason"))) {
                String reason = params.get("reason").toString();
                log.info("DealUserFirstSendMessageUtil  reason={}", reason);
                amClientTasks.setDetail("用户不符合的原因: "+reason+"\n");
            }

            log.info("DealUserFirstSendMessageUtil  content={}", content);
            String jsonContent = AIJsonUtil.getJsonContent(content);
            JSONObject jsonObject = JSONArray.parseObject(jsonContent);
            if (Objects.isNull(jsonObject.get("messages")) || jsonObject.get("messages").toString().equals("[]")) {
                log.error("DealUserFirstSendMessageUtil dealBossNewMessage messages is null content={}",content);
                return ResultVO.fail(404, "ai回复内容解析错误");
            }

            hashMap.put("messages", jsonObject.get("messages"));
            JSONArray jsonArray = jsonObject.getJSONArray("messages");
            StringBuilder stringBuilder = new StringBuilder();
            for (Object object : jsonArray) {
                AmChatMessage aiMessage = new AmChatMessage();
                stringBuilder.append(object.toString()).append("\n");
                aiMessage.setContent(object.toString());
                aiMessage.setCreateTime(LocalDateTime.now());
                aiMessage.setUserId(Long.parseLong(amZpLocalAccouts.getExtBossId()));
                aiMessage.setRole(AIRoleEnum.ASSISTANT.getRoleName());
                aiMessage.setConversationId(taskId);
                aiMessage.setChatId(clientTaskId);
                aiMessage.setType(-1);
                aiMessages.add(aiMessage);
            }
            String detail = amClientTasks.getDetail();
            amClientTasks.setDetail(detail+"\n"+String.format("回复用户: %s , 回复内容为: %s", amResume.getName(), stringBuilder.toString()));
        }catch (Exception e){
            log.error("DealUserFirstSendMessageUtil dealBossNewMessage content parse error content={}",content);
            return ResultVO.fail(404, "ai回复内容解析错误");
        }
        hashMap.put("search_data", searchDataMap);
        amClientTasks.setData(JSONObject.toJSONString(hashMap));
        boolean result = amClientTasksService.save(amClientTasks);
        log.info("DealUserFirstSendMessageUtil dealBossNewMessage  amClientTasks ={} result={}", JSONObject.toJSONString(amClientTasks), result);

        if (result) {
            //保存ai的回复
            if (CollectionUtils.isNotEmpty(aiMessages)) {
                boolean mockSaveResult = amChatMessageService.saveBatch(aiMessages);
                log.info("DealUserFirstSendMessageUtil dealBossNewMessage save result={}", mockSaveResult);
            }
            amResumeService.updateType(amResume,isAiSetStatus.get(),ReviewStatusEnums.getEnumByStatus(statusCode.get()),false);
            // 请求微信和手机号
            replyUserMessageDataProcessor.generateRequestInfo(statusCode.get(),amNewMask,amZpLocalAccouts,amResume,amResume.getUid());
            boolean updateResume = amResumeService.updateById(amResume);
            log.info("DealUserFirstSendMessageUtil dealBossNewMessage updateResume={} save result={}", JSONObject.toJSONString(amResume), updateResume);
        }

        return ResultVO.success();
    }

}
