package com.open.hr.ai.processor.bossNewMessage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.ai.manager.CommonAIManager;
import com.open.ai.eros.ai.tool.function.InterviewFunction;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.bean.req.AmNewMaskAddReq;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import com.open.hr.ai.util.AiReplyPromptUtil;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 用于分析当前用户的prompt
 */
@Order(3)
@Component
@Slf4j
public class ReplyUserMessageDataProcessor implements BossNewMessageProcessor {

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


    private Map<String,DefaultToolExecutor> toolExecutorMap = new HashMap<>();
    private List<ToolSpecification> toolSpecifications = new ArrayList<>();

    @PostConstruct
    public void init() {

        InterviewFunction calculator = new InterviewFunction();
        Method[] methods = calculator.getClass().getMethods();

        for (Method method : methods) {
            if(method.isAnnotationPresent(Tool.class)){
                Tool annotation = method.getAnnotation(Tool.class);
                toolSpecifications.add(ToolSpecifications.toolSpecificationFrom(method));
                DefaultToolExecutor defaultToolExecutor = new DefaultToolExecutor(calculator, method);
                toolExecutorMap.put(annotation.name(),defaultToolExecutor);
            }
        }
    }

    /**
     * 处理聊天内容,下面的代码有点🪨, 又要查询历史记录拼接, 又要去重, 又要考虑mock ai 回复内容给前端做处理
     * 先处理后续再优化
     */
    @Override
    public ResultVO dealBossNewMessage(AtomicInteger firstTime,String platform, AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {
        log.info("ReplyUserMessageDataProcessor dealBossNewMessage amResume={}, bossId={} req={}", amResume, amZpLocalAccouts.getId(), req);
        if (Objects.isNull(amResume) || StringUtils.isBlank(amResume.getEncryptGeekId())) {
            return ResultVO.fail(404, "用户信息异常");
        }
        String taskId = amZpLocalAccouts.getId() + "_" + req.getUser_id();

        LambdaQueryWrapper<AmChatMessage> messagesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messagesLambdaQueryWrapper.in(AmChatMessage::getConversationId, taskId);
        messagesLambdaQueryWrapper.eq(AmChatMessage::getType, 1);
        messagesLambdaQueryWrapper.orderByAsc(AmChatMessage::getCreateTime);
        List<AmChatMessage> amChatMessages = amChatMessageService.list(messagesLambdaQueryWrapper);

        // 过滤和保存
        List<ChatMessage> bossNewMessages = req.getMessages();
        // 过滤出消息id
        List<String> messageIds = bossNewMessages.stream().map(ChatMessage::getId).collect(Collectors.toList());
        // 查询数据库中是否存在消息id
        LambdaQueryWrapper<AmChatMessage> messageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageLambdaQueryWrapper.in(AmChatMessage::getChatId, messageIds);
        List<AmChatMessage> exitAmChatMessages = amChatMessageService.list(messageLambdaQueryWrapper);

        if (messageIds.size() == exitAmChatMessages.size()) {
            log.info("ReplyUserMessageDataProcessor dealBossNewMessage messageIds.size() == exitAmChatMessages.size() messageIds={}", messageIds);
            return ResultVO.success();
        }
        //buildMessage 用于拼接用户的新消息
        StringBuilder buildNewUserMessage = new StringBuilder();
        StringBuilder buildSystemUserMessage = new StringBuilder();
        // 过滤出已经存在的消息id
        for (ChatMessage message : bossNewMessages) {
            // 判断是否存在消息id, 不存在则构建插入
            if (exitAmChatMessages.stream().noneMatch(amChatbotGreetMessage -> amChatbotGreetMessage.getChatId().equals(message.getId()))) {
                AmChatMessage greetMessages = new AmChatMessage();
                greetMessages.setContent(message.getContent().toString());
                greetMessages.setCreateTime(LocalDateTime.now());
                if (message.getRole().equals("user")) {
                    buildNewUserMessage.append(message.getContent().toString()).append("\n\n");
                    greetMessages.setUserId(Long.parseLong(req.getUser_id()));
                    greetMessages.setRole(AIRoleEnum.USER.getRoleName());
                } else {
                    buildSystemUserMessage.append(message.getContent().toString()).append("\n\n");
                    greetMessages.setUserId(Long.parseLong(req.getRecruiter_id()));
                    greetMessages.setRole(AIRoleEnum.ASSISTANT.getRoleName());
                }
                //  招聘账号id + 用户id作为任务id
                greetMessages.setConversationId(taskId);
                greetMessages.setChatId(message.getId());
                greetMessages.setCreateTime(LocalDateTime.now());
                boolean result = amChatMessageService.save(greetMessages);
                log.info("SaveMessageDataProcessor dealBossNewMessage greetMessages={} save result={}", JSONObject.toJSONString(greetMessages), result);
            }
        }

        if (firstTime.get() == 1){
            log.info("用户第一次发送消息 status={}",firstTime.get());
            return ResultVO.success();
        }

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

        if (StringUtils.isNotBlank(buildSystemUserMessage.toString())) {
            messages.add(new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), buildSystemUserMessage.toString()));
        }
        if (StringUtils.isNotBlank(buildNewUserMessage.toString())) {
            messages.add(new ChatMessage(AIRoleEnum.USER.getRoleName(), buildNewUserMessage.toString()));
        }
        log.info("ReplyUserMessageDataProcessor dealBossNewMessage messages={}", JSONObject.toJSONString(messages));
        //告诉ai所有相关参数信息
        String sbParams = "请记住下列参数和数据，后续会用到。当前角色的面具id maskId:" + amNewMask.getId() +
                                ",当前管理员/hr的id adminId:" + amZpLocalAccouts.getAdminId() +
                                ",当前求职者uid employeeUid:" + amResume.getUid() +
                                ",当前招聘的职位id positionId:" + amResume.getPostId() +
                                ",当前角色所登录的平台账号的id accountId:" + amResume.getAccountId() +
                                ",当前的时间是:" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        log.info("ai pre params:" + sbParams);
        messages.add(new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(), sbParams));
        // 如果content为空 重试10次
        String content = "";
        AtomicInteger statusCode = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            ChatMessage chatMessage = commonAIManager.aiNoStream(messages, Arrays.asList("set_status","get_spare_time","appoint_interview","cancel_interview","modify_interview_time"), "OpenAI:gpt-4o-2024-05-13", 0.8,statusCode);
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
        hashMap.put("user_id", req.getUser_id());
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
            aiMockMessages.setUserId(Long.parseLong(req.getRecruiter_id()));
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
            // 请求微信和手机号
            generateRequestInfo(status,amNewMask,amZpLocalAccouts,amResume,req);
            // 根据状态发起request_info
            boolean updateResume = amResumeService.updateById(amResume);
            log.info("ReplyUserMessageDataProcessor dealBossNewMessage updateResume={} save result={}", JSONObject.toJSONString(amResume), updateResume);
        }

        return ResultVO.success();
    }

    // 生成 request_info
    private void generateRequestInfo(Integer status,AmNewMask amNewMask,AmZpLocalAccouts amZpLocalAccouts,AmResume amResume,ClientBossNewMessageReq req){
        String aiRequestParam = amNewMask.getAiRequestParam();
        if (StringUtils.isNotBlank(aiRequestParam)) {
            AmNewMaskAddReq amNewMaskAddReq = JSONObject.parseObject(aiRequestParam, AmNewMaskAddReq.class);

            Integer code = amNewMaskAddReq.getCode();
            if (Objects.isNull(code)) {
                log.info("用户:{} ,没有设置获取微信和手机号码", req.getUser_id());
                return;
            }

            if (!code.equals(status)) {
                log.info("用户:{} ,请求用户信息,但是状态不匹配 code={}, status={}", req.getUser_id(),code,status);
                return;
            }


            if (!amNewMaskAddReq.getOpenInterviewSwitch() && !amNewMaskAddReq.getOpenExchangePhone()) {
                log.info("用户:{} ,请求用户信息,但是没有开启面试或者交换电话号码", req.getUser_id());
                return;
            }

            LambdaQueryWrapper<AmClientTasks> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmClientTasks::getBossId, amZpLocalAccouts.getId());
            queryWrapper.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.REQUEST_INFO.getType());
            queryWrapper.like(AmClientTasks::getData, "phone");
            // 或者包含weixin
            queryWrapper.or().like(AmClientTasks::getData, "wechat");

            AmClientTasks tasksServiceOne = amClientTasksService.getOne(queryWrapper, false);
            if (Objects.isNull(tasksServiceOne)) {
                AmClientTasks amClientTasks = new AmClientTasks();
                amClientTasks.setBossId(amZpLocalAccouts.getId());
                amClientTasks.setTaskType(ClientTaskTypeEnums.REQUEST_INFO.getType());
                amClientTasks.setOrderNumber(ClientTaskTypeEnums.REQUEST_INFO.getOrder());
                HashMap<String, Object> hashMap = new HashMap<>();
                HashMap<String, Object> searchDataMap = new HashMap<>();
                hashMap.put("user_id", req.getUser_id());
                List<String> infoType = new ArrayList<>();
                if (amNewMaskAddReq.getOpenExchangePhone()) {
                    infoType.add("phone");
                }
                if (amNewMaskAddReq.getOpenExchangeWeChat()) {
                    infoType.add("wechat");
                }
                hashMap.put("info_type", infoType);
                if (Objects.nonNull(amResume.getEncryptGeekId())) {
                    searchDataMap.put("encrypt_geek_id", amResume.getEncryptGeekId());
                }
                if (Objects.nonNull(amResume.getName())) {
                    searchDataMap.put("name", amResume.getName());
                }
                hashMap.put("search_data", searchDataMap);
                amClientTasks.setData(JSONObject.toJSONString(hashMap));
                amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
                amClientTasks.setCreateTime(LocalDateTime.now());
                amClientTasks.setUpdateTime(LocalDateTime.now());
                amClientTasksService.save(amClientTasks);
                log.info("用户:{} 主动打招呼,请求用户信息", req.getUser_id());
            }   else {
                log.info("用户:{} 主动打招呼,请求用户信息,但是已经存在请求信息任务", req.getUser_id());
            }

        }
    }



}
