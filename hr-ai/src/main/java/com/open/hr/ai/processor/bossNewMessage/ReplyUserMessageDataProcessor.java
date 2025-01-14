package com.open.hr.ai.processor.bossNewMessage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.ai.manager.AIManager;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    private AmSquareRolesServiceImpl amSquareRolesService;

    @Resource
    private AIManager aiManager;

    private static final  String prompt =  "你是一名杰出的招聘专员助理，负责为求职者提供清晰、专业和友好的回复。当你准备回复求职者的申请或问题时，请使用以下模板：\n" +
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
     * 根据聊天内容,用来提取用户手机和微信号
     */
    @Override
    public ResultVO dealBossNewMessage(AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {
        if (Objects.isNull(amResume)) {
            return ResultVO.fail(404, "用户信息异常");
        }

        LambdaQueryWrapper<AmChatbotGreetConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatbotGreetConfig::getAccountId, amZpLocalAccouts.getId());
        queryWrapper.eq(AmChatbotGreetConfig::getIsAiOn,1);
        AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(queryWrapper, false);
        if (Objects.isNull(amChatbotGreetConfig)) {
            return ResultVO.fail(404, "未找到对应的配置");
        }
        Integer postId = amResume.getPostId();
        String  content = "你好";
        if (Objects.isNull(postId)) {
           log.info("postId is null,amResume={}",amResume);
        }else {
            LambdaQueryWrapper<AmChatbotPositionOption> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AmChatbotPositionOption::getPositionId,postId);
            lambdaQueryWrapper.eq(AmChatbotPositionOption::getAccountId,amZpLocalAccouts.getId());
            AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(lambdaQueryWrapper, false);
            if (Objects.nonNull(amChatbotPositionOption.getSquareRoleId())){
                // 如果有绑定ai角色,则获取ai角色进行回复
                AmSquareRoles amSquareRoles = amSquareRolesService.getById(amChatbotPositionOption.getSquareRoleId());
                if (Objects.nonNull(amSquareRoles)){
                    // todo 目前找不到这个ai角色的用处, 先不处理, 先拿一个prompt
                }
            }
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new UserMessage("system", prompt));
            for (com.open.ai.eros.common.vo.ChatMessage message : req.getMessages()) {
                messages.add(new UserMessage(message.getRole(), message.getContent().toString()));
            }

           content = aiManager.aiChatMessFunction(messages);

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
        searchDataMap.put("encrypt_friend_id", amResume.getEncryptGeekId());
        searchDataMap.put("name", amResume.getName());
        hashMap.put("search_data", searchDataMap);
        messageMap.put("content", content);
        hashMap.put("message", messageMap);
        amClientTasks.setData(JSONObject.toJSONString(hashMap));
        amClientTasksService.save(amClientTasks);

        return ResultVO.success();
    }


}
