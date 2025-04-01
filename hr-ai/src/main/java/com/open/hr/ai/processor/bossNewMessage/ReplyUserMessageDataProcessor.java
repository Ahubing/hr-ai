package com.open.hr.ai.processor.bossNewMessage;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.ai.manager.CommonAIManager;
import com.open.ai.eros.ai.tool.function.InterviewFunction;
import com.open.ai.eros.common.constants.InterviewStatusEnum;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.bean.req.AmNewMaskAddReq;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import com.open.hr.ai.constant.RedisKyeConstant;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import com.open.hr.ai.util.AiReplyPromptUtil;
import com.open.hr.ai.util.AmClientTaskUtil;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

    @Resource
    private IcRecordServiceImpl recordService;

    @Resource
    private AmChatbotGreetResultServiceImpl amChatbotGreetResultService;

    @Resource
    private AmChatbotOptionsItemsServiceImpl amChatbotOptionsItemsService;

    @Resource
    private AmChatbotGreetTaskServiceImpl amChatbotGreetTaskService;

    @Resource
    private AmClientTaskUtil amClientTaskUtil;
    @Resource
    private JedisClientImpl jedisClient;


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
            log.info("用户第一次发送消息,需要获取简历信息后再进行下面流程 status={}",firstTime.get());
            return ResultVO.success();
        }
        if (Objects.equals(amResume.getType(), ReviewStatusEnums.ABANDON.getStatus())){
            log.info("不符合的用户,不进行回答问题  uid={} status={}",amResume.getUid(),amResume.getType());
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
                IcRecord icRecord = recordService.getOneNormalIcRecord(amResume.getUid(),amZpLocalAccouts.getAdminId(),amResume.getAccountId(),amResume.getPostId());
                log.info("icRecord query params adminId:{} positionId:{} accountId:{} employeeUid:{}", amZpLocalAccouts.getAdminId(), amResume.getPostId(), amResume.getAccountId(), amResume.getUid());
                log.info("icRecord={}", JSONUtil.toJsonStr(icRecord));
                String aiPrompt = AiReplyPromptUtil.buildPrompt(amResume, amNewMask, icRecord);
                log.info("aiPrompt={}", aiPrompt);
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message",Collections.singletonList(message.getContent()));
                messages.add(new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), jsonObject.toJSONString()));
            } else {
                messages.add(new ChatMessage(AIRoleEnum.USER.getRoleName(), message.getContent()));
            }
        }

        if (StringUtils.isNotBlank(buildSystemUserMessage.toString())) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message",Collections.singletonList(buildSystemUserMessage.toString()));
            messages.add(new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), jsonObject.toJSONString()));
        }
        if (StringUtils.isNotBlank(buildNewUserMessage.toString())) {
            messages.add(new ChatMessage(AIRoleEnum.USER.getRoleName(), buildNewUserMessage.toString()));
        }
        log.info("ReplyUserMessageDataProcessor dealBossNewMessage messages={}", JSONObject.toJSONString(messages));
        //告诉ai所有相关参数信息
        String preParams = "请记住下列参数和数据，后续会用到。当前角色的面具id maskId(String类型):" + amNewMask.getId() +
                                ",当前管理员/hr的id adminId(String类型):" + amZpLocalAccouts.getAdminId() +
                                ",当前求职者uid employeeUid(String类型):" + amResume.getUid() +
                                ",当前招聘的职位id positionId(String类型):" + amResume.getPostId() +
                                ",当前角色所登录的平台账号的id accountId:(String类型)" + amResume.getAccountId() +
                                ",当前的时间是:" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        log.info("ai pre params:" + preParams);
        messages.add(new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(), preParams));
        // 如果content为空 重试10次
        String content = "";
        AtomicInteger needToReply = new AtomicInteger(1);
        AtomicInteger statusCode = new AtomicInteger(amResume.getType());
        AtomicBoolean isAiSetStatus = new AtomicBoolean(false);
        for (int i = 0; i < 10; i++) {
            ChatMessage chatMessage = commonAIManager.aiNoStream(messages, Arrays.asList("set_status","get_spare_time","appoint_interview","cancel_interview","modify_interview_time","no_further_reply"), "OpenAI:gpt-4o-2024-05-13", 0.8,statusCode,needToReply,isAiSetStatus);
           if (Objects.isNull(chatMessage)) {
              continue;
           }
            content = chatMessage.getContent().toString();
            if (StringUtils.isNotBlank(content)) {
                break;
            }
        }
        if (needToReply.get() == 0){
            //本次不回答用户
            log.info("ReplyUserMessageDataProcessor dealBossNewMessage aiNoStream needToReply is 0");
            return ResultVO.success();
        }
        if (StringUtils.isBlank(content)) {
            log.info("ReplyUserMessageDataProcessor dealBossNewMessage aiNoStream content is null");
            return ResultVO.fail(404, "ai回复内容为空");
        }
        // 对content 消息内容 删除包含</think>之前的内容
        if (content.contains("</think>")) {
            content = content.substring(content.indexOf("</think>") + 8);
        }

        // 保存任务
        AmClientTasks amClientTasks = new AmClientTasks();
        amClientTasks.setBossId(amZpLocalAccouts.getId());
        amClientTasks.setTaskType(ClientTaskTypeEnums.SEND_MESSAGE.getType());
        amClientTasks.setOrderNumber(ClientTaskTypeEnums.SEND_MESSAGE.getOrder());
        amClientTasks.setSubType(ClientTaskTypeEnums.SEND_MESSAGE.getType());
        amClientTasks.setCreateTime(LocalDateTime.now());
        amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
        HashMap<String, Object> hashMap = new HashMap<>();
        HashMap<String, Object> searchDataMap = new HashMap<>();
        hashMap.put("user_id", req.getUser_id());
        if (Objects.nonNull(amResume.getEncryptGeekId())) {
            searchDataMap.put("encrypt_friend_id", amResume.getEncryptGeekId());
        }
        if (Objects.nonNull(amResume.getName())) {
            searchDataMap.put("name", amResume.getName());
        }
        hashMap.put("search_data", searchDataMap);
        List<AmChatMessage> aiMessages = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONArray.parseObject(content);
            if (Objects.isNull(jsonObject.get("messages"))){
                log.error("ReplyUserMessageDataProcessor dealBossNewMessage messages is null content={}",content);
                return ResultVO.fail(404, "ai回复内容解析错误");
            }
            hashMap.put("messages", jsonObject.get("messages"));
            JSONArray jsonArray = jsonObject.getJSONArray("messages");
            for (Object object : jsonArray) {
                AmChatMessage aiMessage = new AmChatMessage();
                aiMessage.setContent(object.toString());
                aiMessage.setCreateTime(LocalDateTime.now());
                aiMessage.setUserId(Long.parseLong(amZpLocalAccouts.getExtBossId()));
                aiMessage.setRole(AIRoleEnum.ASSISTANT.getRoleName());
                aiMessage.setConversationId(taskId);
                aiMessage.setChatId(UUID.randomUUID().toString());
                aiMessage.setType(-1);
                aiMessages.add(aiMessage);
            }
        }catch (Exception e){
            log.error("ReplyUserMessageDataProcessor dealBossNewMessage content parse error content={}",content);
            return ResultVO.fail(404, "ai回复内容解析错误");
        }
        amClientTasks.setData(JSONObject.toJSONString(hashMap));
        boolean result = amClientTasksService.save(amClientTasks);
        log.info("ReplyUserMessageDataProcessor dealBossNewMessage  amClientTasks ={} result={}", JSONObject.toJSONString(amClientTasks), result);

        if (result) {
            //保存ai的回复
            if (CollectionUtils.isNotEmpty(aiMessages)) {
                boolean mockSaveResult = amChatMessageService.saveBatch(aiMessages);
                log.info("DealUserFirstSendMessageUtil dealBossNewMessage save result={}", mockSaveResult);
            }
            // 更新简历状态
            amResumeService.updateType(amResume,isAiSetStatus.get(),ReviewStatusEnums.getEnumByStatus(statusCode.get()));

            // 请求微信和手机号
            generateRequestInfo(statusCode.get(),amNewMask,amZpLocalAccouts,amResume,req.getUser_id());
            // 根据状态发起request_info
            boolean updateResume = amResumeService.updateById(amResume);
            log.info("ReplyUserMessageDataProcessor dealBossNewMessage updateResume={} save result={}", JSONObject.toJSONString(amResume), updateResume);
        }
        return ResultVO.success();
    }

    /**
     * 生成 request_info
      */
    public void generateRequestInfo(Integer status,AmNewMask amNewMask,AmZpLocalAccouts amZpLocalAccouts,AmResume amResume,String userId){
        String aiRequestParam = amNewMask.getAiRequestParam();
        if (StringUtils.isNotBlank(aiRequestParam)) {
            AmNewMaskAddReq amNewMaskAddReq = JSONObject.parseObject(aiRequestParam, AmNewMaskAddReq.class);

            // 判断是否要获取附件简历信息
            dealAttchmentResume(amResume, amZpLocalAccouts, Integer.parseInt(userId),amNewMaskAddReq.getOpenExchangeAttachmentResume());

            Integer code = amNewMaskAddReq.getCode();
            if (Objects.isNull(code)) {
                log.info("用户:{} ,没有设置获取微信和手机号码", userId);
                return;
            }

            //当前状态在1, 2 , 3范围 且当前状态>=目标状态
            if ( status < ReviewStatusEnums.BUSINESS_SCREENING.getStatus() || status > ReviewStatusEnums.INTERVIEW_ARRANGEMENT.getStatus() || status < code) {
                log.info("用户:{} ,请求用户信息,但是状态不匹配 code={}, status={}", userId,code,status);
                return;
            }

            if (!amNewMaskAddReq.getOpenInterviewSwitch() && !amNewMaskAddReq.getOpenExchangePhone()) {
                log.info("用户:{} ,请求用户信息,但是没有开启面试或者交换电话号码", userId);
                return;
            }

            LambdaQueryWrapper<AmClientTasks> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmClientTasks::getBossId, amZpLocalAccouts.getId());
            queryWrapper.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.REQUEST_INFO.getType());
            queryWrapper.like(AmClientTasks::getData, userId);
            queryWrapper.and(wrapper ->
                    wrapper.like(AmClientTasks::getData, "phone")
                            .or()
                            .like(AmClientTasks::getData, "wechat")
            );

            AmClientTasks tasksServiceOne = amClientTasksService.getOne(queryWrapper, false);
            if (Objects.isNull(tasksServiceOne)) {
                AmClientTasks amClientTasks = new AmClientTasks();
                amClientTasks.setBossId(amZpLocalAccouts.getId());
                amClientTasks.setTaskType(ClientTaskTypeEnums.REQUEST_INFO.getType());
                amClientTasks.setOrderNumber(ClientTaskTypeEnums.REQUEST_INFO.getOrder());
                HashMap<String, Object> hashMap = new HashMap<>();
                HashMap<String, Object> searchDataMap = new HashMap<>();
                hashMap.put("user_id", userId);
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
                log.info("用户:{} 主动打招呼,请求用户信息",userId);
            }   else {
                log.info("用户:{} 主动打招呼,请求用户信息,但是已经存在请求信息任务",userId);
            }

        }
    }

    /**
     * 处理附件简历逻辑
     */
    private void dealAttchmentResume(AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, Integer uid,Boolean openExchangeAttachmentResume) {

        if (Objects.isNull(amResume) || StringUtils.isBlank(amResume.getEncryptGeekId())) {
            log.error("用户信息异常 amResume is null");
            return ;
        }

        if (Objects.equals(amResume.getType(), ReviewStatusEnums.ABANDON.getStatus())){
            log.info("用户:{} 主动打招呼,用户状态为不符合", amResume.getEncryptGeekId());
            return ;
        }

        // 从未对此用户发起本请求时请求一次
        LambdaQueryWrapper<AmClientTasks> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmClientTasks::getBossId, amZpLocalAccouts.getId());
        queryWrapper.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.REQUEST_INFO.getType());
        queryWrapper.like(AmClientTasks::getData, "attachment_resume");
        queryWrapper.like(AmClientTasks::getData, uid);
        AmClientTasks tasksServiceOne = amClientTasksService.getOne(queryWrapper, false);
        if (Objects.isNull(tasksServiceOne)) {
            if (Objects.nonNull(openExchangeAttachmentResume) && openExchangeAttachmentResume){
                amClientTaskUtil.buildRequestTask(amZpLocalAccouts, uid, amResume,true);
                log.info("用户:{} 主动打招呼,请求用户附件简历信息", uid);
            }
        }else {
            log.info("用户:{} 主动打招呼,请求用户附件简历信息,但是已经存在请求信息任务,taskId={}", uid,tasksServiceOne.getId());
        }
    }



    public void dealReChatTask(AmResume amResume,AmZpLocalAccouts amZpLocalAccouts){
        Integer postId = amResume.getPostId();
        if (Objects.isNull(postId)) {
            log.error("用户:{} 主动打招呼,岗位id为空, 不生成打招呼任务,请求用户信息 postId is null", amResume.getEncryptGeekId());
            return;
        }

        String accoutsId = amZpLocalAccouts.getId();
        LambdaQueryWrapper<AmChatbotGreetTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatbotGreetTask::getAccountId,accoutsId);
        queryWrapper.eq(AmChatbotGreetTask::getPositionId, postId);
        AmChatbotGreetTask one = amChatbotGreetTaskService.getOne(queryWrapper, false);
        if (Objects.isNull(one)){
            log.info("打招呼任务为空,不支持复聊 bossId={},postId={}",accoutsId,postId);
            return;
        }

        AmChatbotGreetResult amChatbotGreetResult = new AmChatbotGreetResult();
        amChatbotGreetResult.setRechatItem(0);
        amChatbotGreetResult.setSuccess(1);
        amChatbotGreetResult.setAccountId(accoutsId);
        amChatbotGreetResult.setCreateTime(LocalDateTime.now());
        amChatbotGreetResult.setTaskId(one.getId());
        amChatbotGreetResult.setUserId(amResume.getUid());
        /**
         * 3、生成复聊任务, 如果存在复聊方案
         */
        AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(new LambdaQueryWrapper<AmChatbotPositionOption>().eq(AmChatbotPositionOption::getAccountId, amZpLocalAccouts.getId()).eq(AmChatbotPositionOption::getPositionId, postId), false);
        if (Objects.isNull(amChatbotPositionOption)) {
            log.info("复聊任务处理开始, 账号:{}, 未找到对应的职位", amZpLocalAccouts.getId());
            return;
        }
        // 查询第一天的复聊任务
        List<AmChatbotOptionsItems> amChatbotOptionsItems = amChatbotOptionsItemsService.lambdaQuery().eq(AmChatbotOptionsItems::getOptionId, amChatbotPositionOption.getInquiryRechatOptionId()).eq(AmChatbotOptionsItems::getDayNum, 1).list();
        if (Objects.isNull(amChatbotOptionsItems) || amChatbotOptionsItems.isEmpty()) {
            log.info("复聊任务处理开始, 账号:{}, 未找到对应的复聊方案", amZpLocalAccouts.getId());
            return;
        }

        for (AmChatbotOptionsItems amChatbotOptionsItem : amChatbotOptionsItems) {
            // 处理复聊任务, 存入队列里面, 用于定时任务处理
            amChatbotGreetResult.setRechatItem(amChatbotOptionsItem.getId());
            amChatbotGreetResult.setTaskId(one.getId());
            amChatbotGreetResultService.updateById(amChatbotGreetResult);
            Long operateTime = System.currentTimeMillis() + Integer.parseInt(amChatbotOptionsItem.getExecTime())* 1000L;
            Long zadd = jedisClient.zadd(RedisKyeConstant.AmChatBotReChatTask, operateTime, JSONObject.toJSONString(amChatbotGreetResult));
            log.info("复聊任务处理开始, 账号:{}, 复聊任务添加结果:{}", amZpLocalAccouts.getId(), zadd);
        }
    }




}
