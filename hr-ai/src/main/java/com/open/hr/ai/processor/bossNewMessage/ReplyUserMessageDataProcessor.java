package com.open.hr.ai.processor.bossNewMessage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.ai.manager.AIManager;
import com.open.ai.eros.ai.manager.CommonAIManager;
import com.open.ai.eros.ai.tool.config.ToolConfig;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.bean.vo.AmMaskVo;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import com.open.hr.ai.convert.AmMaskConvert;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import com.open.hr.ai.util.BuildPromptUtil;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
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
    private CommonAIManager commonAIManager;

    private static final String prompt = "你是一名杰出的招聘专员助理，负责为求职者提供清晰、专业和友好的回复。当你准备回复求职者的申请或问题时，请使用以下模板：\n" +
            "1. 别人说你好的时候，你也回复，你好\n" +
            "2. 阐明职位的基本信息。\n" +
            "3. 提供位置信息。\n" +
            "4. 显示薪资范围。\n" +
            "5. 列出职位要求。\n" +
            "7. 提供联系方式以获取更多信息。\n" +
            "8. 用礼貌的语气结束语。\n" +
            "9.当别人问你是否机器人相关问题时候，你表示简短的疑惑，比如?或者什么？\n" +
            "10.回复别人的内容，尽量简短凝炼\n" +
            "在合适的地方使用以下变量";

    /**
     * 处理聊天内容,下面的代码有点🪨, 又要查询历史记录拼接, 又要去重, 又要考虑mock ai 回复内容给前端做处理
     * 先处理后续再优化
     */
    @Override
    public ResultVO dealBossNewMessage(String platform, AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {
        log.info("ReplyUserMessageDataProcessor dealBossNewMessage amResume={}, bossId={} req={}", amResume, amZpLocalAccouts.getId(), req);
        if (Objects.isNull(amResume) || StringUtils.isBlank(amResume.getEncryptGeekId())) {
            return ResultVO.fail(404, "用户信息异常");
        }

        String taskId = req.getRecruiter_id() + "_" + req.getUser_id();

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

        LambdaQueryWrapper<AmChatbotGreetConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatbotGreetConfig::getAccountId, amZpLocalAccouts.getId());
        queryWrapper.eq(AmChatbotGreetConfig::getIsAiOn, 1);
        AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(queryWrapper, false);
        if (Objects.isNull(amChatbotGreetConfig)) {
            log.error("未找到ai跟进对应的配置,bossId={}", amZpLocalAccouts.getId());
            return ResultVO.fail(404, "未找到对应的配置");
        }
        Integer postId = amResume.getPostId();


        if (Objects.isNull(postId)) {
            // 如果postId为空,则尝试通过bossId 和 encryptGeekId 查询对应的岗位
            if (StringUtils.isNotBlank(amResume.getEncryptGeekId())) {
                LambdaQueryWrapper<AmPosition> positionLambdaQueryWrapper = new LambdaQueryWrapper<>();
                positionLambdaQueryWrapper.eq(AmPosition::getBossId, amZpLocalAccouts.getId());
                positionLambdaQueryWrapper.eq(AmPosition::getEncryptId, 1);
                AmPosition amPosition = amPositionService.getOne(positionLambdaQueryWrapper, false);
                if (Objects.nonNull(amPosition)) {
                    postId = amPosition.getId();
                }
            }
            if (Objects.isNull(postId)) {
                log.info("postId is null,amResume={}", amResume);
                return ResultVO.fail(404, "未找到对应的岗位配置,不继续走下一个流程");
            }
        }


        LambdaQueryWrapper<AmChatbotPositionOption> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AmChatbotPositionOption::getPositionId, postId);
        lambdaQueryWrapper.eq(AmChatbotPositionOption::getAccountId, amZpLocalAccouts.getId());
        AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(lambdaQueryWrapper, false);
        List<ChatMessage> messages = new ArrayList<>();
        if (Objects.nonNull(amChatbotPositionOption) && Objects.nonNull(amChatbotPositionOption.getAmMaskId())) {
            // 如果有绑定ai角色,则获取ai角色进行回复
            AmNewMask amNewMask = amNewMaskService.getById(amChatbotPositionOption.getAmMaskId());

            if (Objects.nonNull(amNewMask)) {
                String aiPrompt = BuildPromptUtil.buildPrompt(amResume, amNewMask);
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
        // 如果content为空 重试10次
        String content = "";
        for (int i = 0; i < 10; i++) {
            ChatMessage chatMessage = commonAIManager.aiNoStream(messages, Arrays.asList("setStatus"), "OpenAI:gpt-4o-2024-05-13", 0.8);
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
        }

        return ResultVO.success();
    }


}
