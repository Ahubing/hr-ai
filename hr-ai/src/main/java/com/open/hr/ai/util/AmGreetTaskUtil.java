package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import com.open.hr.ai.constant.MessageTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * 处理临时打招呼数据任务添加
 *
 * @Date 2025/1/18 00:17
 */
@Slf4j
@Component
public class AmGreetTaskUtil {

    @Resource
    private AmChatbotGreetTaskServiceImpl amChatbotGreetTaskService;

    @Resource
    private AmChatbotGreetMessagesServiceImpl amChatbotGreetMessagesService;

    @Resource
    private AmChatbotGreetConditionServiceImpl amChatbotGreetConditionService;

    @Resource
    private AmPositionServiceImpl amPositionService;

    @Resource
    private AmClientTasksServiceImpl amClientTasksService;

    @Resource
    private AmChatbotGreetConfigServiceImpl amChatbotGreetConfigService;


    private static final String GREET_MESSAGE = "你好";

    /**
     * 处理临时任务,一次性塞到队列里面执行
     */
    public void dealGreetTask(AmChatbotGreetTask amChatbotGreetTask) {
        try {
            // 执行任务
            LambdaQueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new LambdaQueryWrapper<>();
            greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getAccountId, amChatbotGreetTask.getAccountId());
            greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getIsAllOn, 1);
            AmChatbotGreetConfig one = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper, false);
            if (Objects.isNull(one) || one.getIsGreetOn() == 0) {
                log.info("打招呼任务跳过: 账号:{}, 未找到打招呼任务配置 或总开关关闭 或未开启打招呼", amChatbotGreetTask.getAccountId());
                return;
            }

            AmChatbotGreetMessages amChatbotGreetMessages = new AmChatbotGreetMessages();
            amChatbotGreetMessages.setTaskId(amChatbotGreetTask.getId());
            amChatbotGreetMessages.setTaskType(MessageTypeEnums.temporary_greet.getCode());
            amChatbotGreetMessages.setAccountId(amChatbotGreetTask.getAccountId());
            amChatbotGreetMessages.setIsSystemSend(1);
            amChatbotGreetMessages.setContent(GREET_MESSAGE);
            amChatbotGreetMessages.setCreateTime(DateUtils.formatDate(new Date(), "Y-m-d"));
            amChatbotGreetMessagesService.save(amChatbotGreetMessages);
            //更新task临时status的状态
            amChatbotGreetTask.setStatus(1);
            amChatbotGreetTask.setUpdateTime(LocalDateTime.now());
            amChatbotGreetTaskService.updateById(amChatbotGreetTask);

            AmChatbotGreetCondition condition = amChatbotGreetConditionService.lambdaQuery()
                    .eq(AmChatbotGreetCondition::getAccountId, amChatbotGreetTask.getAccountId())
                    .eq(AmChatbotGreetCondition::getPositionId, amChatbotGreetTask.getPositionId())
                    .last("LIMIT 1") // 限制查询结果为一条
                    .one(); // 禁止抛出异常


            if (Objects.isNull(condition)) {
                condition = amChatbotGreetConditionService.getById(1);
                AmPosition amPosition = amPositionService.getById(amChatbotGreetTask.getPositionId());
                if (Objects.isNull(amPosition)) {
                    condition.setRecruitPosition("不限");
                } else {
                    if (amPosition.getIsDeleted() == 1 || amPosition.getIsOpen() ==0) {
                        log.error("职位已删除: amPosition={}", amPosition);
                        return;
                    }
                    condition.setRecruitPosition(amPosition.getName());
                }
            }

            // 创建筛选条件
            JSONObject conditions = new JSONObject();
            conditions.put("曾就职单位", condition.getPreviousCompany() != null ? condition.getPreviousCompany() : "");
            conditions.put("招聘职位", condition.getRecruitPosition() != null ? condition.getRecruitPosition() : "不限");
            conditions.put("年龄", condition.getAge() != null ? condition.getAge() : "不限");
            conditions.put("性别", condition.getGender() != null ? condition.getGender() : "不限");
            conditions.put("经验要求", condition.getExperience() != null ? condition.getExperience() : "不限");
            conditions.put("学历要求", condition.getEducation() != null ? condition.getEducation() : "不限");
            conditions.put("薪资待遇[单选]", condition.getSalary() != null ? condition.getSalary() : "不限");
            conditions.put("求职意向", condition.getJobIntention() != null ? condition.getJobIntention() : "不限");

            // 创建任务
            AmClientTasks amClientTasks = new AmClientTasks();
            amClientTasks.setId(UUID.randomUUID().toString());
            amClientTasks.setBossId(amChatbotGreetTask.getAccountId());
            amClientTasks.setTaskType(ClientTaskTypeEnums.GREET.getType());
            amClientTasks.setOrderNumber(ClientTaskTypeEnums.GREET.getOrder());
            amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("conditions", conditions);
            jsonObject.put("times", amChatbotGreetTask.getTaskNum());
            jsonObject.put("greetId", amChatbotGreetTask.getId());
            JSONObject messageObject = new JSONObject();
            messageObject.put("content", GREET_MESSAGE);
            jsonObject.put("message", messageObject);
            amClientTasks.setData(jsonObject.toJSONString());
            amClientTasks.setCreateTime(LocalDateTime.now());
            amClientTasks.setUpdateTime(LocalDateTime.now());
            boolean result = amClientTasksService.save(amClientTasks);
            log.info("处理临时任务结果={}", result);
        } catch (Exception e) {
            log.error("处理临时任务异常", e);
        }
    }

}
