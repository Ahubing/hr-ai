package com.open.hr.ai.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.constant.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Component
@Slf4j
@EnableScheduling
public class AmChatBotGreetJob {


    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;
    @Resource
    private AmChatbotGreetConfigServiceImpl amChatbotGreetConfigService;

    @Resource
    private AmChatbotPositionOptionServiceImpl amChatbotPositionOptionService;

    @Resource
    private AmChatbotGreetTaskServiceImpl amChatbotGreetTaskService;

    @Resource
    private AmChatbotGreetResultServiceImpl amChatbotGreetResultService;

    @Resource
    private AmChatbotGreetMessagesServiceImpl amChatbotGreetMessagesService;
    @Resource
    private AmChatbotGreetConditionServiceImpl amChatbotGreetConditionService;

    @Resource
    private AmChatbotOptionsItemsServiceImpl amChatbotOptionsItemsService;

    @Resource
    private AmClientTasksServiceImpl amClientTasksService;
    @Resource
    private AmPositionServiceImpl amPositionService;

    @Resource
    private AmResumeServiceImpl amResumeService;

    @Resource
    private JedisClientImpl jedisClient;

    @Resource
    private AmChatMessageServiceImpl amChatMessageService;


    private static final String GREET_MESSAGE = "你好";


    /**
     * 处理定时任务
     * 下面跟着php逻辑实现, 后续改造
     */

    @Scheduled(cron = "0 0 * * * ?")
    public void run_scheduled_timer() {
        Lock lock = DistributedLockUtils.getLock("run_scheduled_timer", 30);
        if (lock.tryLock()) {
            try {
                List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.lambdaQuery()
                        .ne(AmZpLocalAccouts::getState, AmLocalAccountStatusEnums.OFFLINE.getStatus())
                        .list();
                for (AmZpLocalAccouts localAccout : localAccouts) {
                    //查看账号是否开启打招呼
                    LambdaQueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new LambdaQueryWrapper<>();
                    greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getAccountId, localAccout.getId());
                    greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getIsAllOn, 1);
                    AmChatbotGreetConfig one = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper, false);
                    if (Objects.isNull(one) || one.getIsGreetOn() == 0) {
                        log.info("打招呼任务跳过: 账号:{}, 未找到打招呼任务配置 或总开关关闭 或未开启打招呼", localAccout.getId());
                        continue;
                    }

                    //查看每日任务 并加入message
                    LambdaQueryWrapper<AmChatbotGreetTask> greetTaskQueryWrapper = new LambdaQueryWrapper<>();
                    greetTaskQueryWrapper.eq(AmChatbotGreetTask::getAccountId, localAccout.getId());
                    greetTaskQueryWrapper.eq(AmChatbotGreetTask::getTaskType, 0);
                    List<AmChatbotGreetTask> amChatbotGreetTasks = amChatbotGreetTaskService.list(greetTaskQueryWrapper);
                    if (CollectionUtils.isEmpty(amChatbotGreetTasks)) {
                        continue;
                    }
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:m");
                    String curTime = now.format(timeFormatter);

                    // 计算前一个小时的时间
                    LocalDateTime preHourTime = now.minusHours(1);
                    String preTime = preHourTime.format(timeFormatter);
                    for (AmChatbotGreetTask amChatbotGreetTask : amChatbotGreetTasks) {
                        Integer taskNum = amChatbotGreetTask.getTaskNum();
                        if (Objects.isNull(taskNum) || taskNum <= 0) {
                            continue;
                        }
                        String execTime = amChatbotGreetTask.getExecTime();
                        if (execTime.compareTo(preTime) > 0 && preTime.compareTo(curTime) <= 0) {
                            // 检查是否已执行
                            boolean isExecuted = checkIfTaskExecuted(amChatbotGreetTask.getId());
                            if (!isExecuted) {
                                // 执行任务
                                AmChatbotGreetMessages amChatbotGreetMessages = new AmChatbotGreetMessages();
                                amChatbotGreetMessages.setTaskId(amChatbotGreetTask.getId());
                                amChatbotGreetMessages.setTaskType(MessageTypeEnums.daily_greet.getCode());
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
                                        log.error("未找到对应的职位:{}", amChatbotGreetTask.getPositionId());
                                        continue;
                                    } else {
                                        if (amPosition.getIsDeleted() == 1 || amPosition.getIsOpen() ==0) {
                                            log.error("职位已删除: amPosition={}", amPosition);
                                            continue;
                                        }
                                        condition.setRecruitPosition(amPosition.getName());
                                    }
                                }
                                // 创建 JSON 对象并根据逻辑填充数据
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
                                amClientTasks.setBossId(localAccout.getId());
                                amClientTasks.setTaskType(ClientTaskTypeEnums.GREET.getType());
                                amClientTasks.setOrderNumber(ClientTaskTypeEnums.GREET.getOrder());
                                amClientTasks.setStatus(0);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("conditions", conditions);
                                jsonObject.put("times", amChatbotGreetTask.getTaskNum());
                                JSONObject messageObject = new JSONObject();
                                messageObject.put("content", GREET_MESSAGE);
                                jsonObject.put("message", messageObject);
                                amClientTasks.setData(jsonObject.toJSONString());
                                amClientTasks.setCreateTime(LocalDateTime.now());
                                amClientTasks.setUpdateTime(LocalDateTime.now());
                                amClientTasksService.save(amClientTasks);
                            }
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }


    private boolean checkIfTaskExecuted(int taskId) {
        AmChatbotGreetMessages amChatbotGreetMessages = amChatbotGreetMessagesService.lambdaQuery()
                .eq(AmChatbotGreetMessages::getTaskId, taskId)
                .eq(AmChatbotGreetMessages::getCreateTime, DateUtils.formatDate(new Date(), "Y-m-d"))
                .one();
        if (Objects.nonNull(amChatbotGreetMessages)) {
            return true;
        }
        return false;
    }


    /**
     * 复聊任务处理, 筛选出今天需要复聊的用户,并用redis存入队列,根据具体的执行时间进行复聊
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void runRechatTimer() {
        Lock lock = DistributedLockUtils.getLock("run_tmp_timer", 30);
        if (lock.tryLock()) {
            try {
                log.info("复聊定时任务开始执行...");
                // 获取当天的 Redis Key
                String todayKey = "rechat_executed_accounts:" + LocalDate.now();

                // 查询所有活跃账号
                List<AmZpLocalAccouts> localAccounts = amZpLocalAccoutsService.lambdaQuery()
                        .ne(AmZpLocalAccouts::getState, AmLocalAccountStatusEnums.OFFLINE.getStatus())
                        .list();

                Map<String, AmChatbotGreetConfig> greetConfigMap = amChatbotGreetConfigService.lambdaQuery()
                        .in(AmChatbotGreetConfig::getAccountId, localAccounts.stream().map(AmZpLocalAccouts::getId).collect(Collectors.toList()))
                        .list().stream().collect(Collectors.toMap(AmChatbotGreetConfig::getAccountId, config -> config));

                for (AmZpLocalAccouts localAccount : localAccounts) {
                    String accountId = localAccount.getId();

                    // 判断今天该账号是否已经执行过
                    if (jedisClient.sismember(todayKey, accountId)) {
                        log.info("复聊任务跳过: 账号:{}，今天已经执行过", accountId);
                        continue;
                    }

                    // 获取复聊方案
                    AmChatbotGreetConfig greetConfig = greetConfigMap.get(accountId);
                    if (greetConfig == null || greetConfig.getIsAllOn() ==0 || greetConfig.getIsRechatOn() == 0) {
                        log.info("复聊任务跳过: 账号:{}, 未找到复聊任务配置 或总开关关闭 或未开启复聊", accountId);
                        continue;
                    }

                    // 查询打过招呼的用户
                    List<AmChatbotGreetResult> greetResults = amChatbotGreetResultService.lambdaQuery()
                            .eq(AmChatbotGreetResult::getAccountId, accountId)
                            .eq(AmChatbotGreetResult::getSuccess, 1).list();

                    // 批量查询任务和职位
                    Set<Integer> taskIds = greetResults.stream().map(AmChatbotGreetResult::getTaskId).collect(Collectors.toSet());
                    Map<Integer, AmChatbotGreetTask> taskMap = amChatbotGreetTaskService.lambdaQuery()
                            .in(AmChatbotGreetTask::getId, taskIds).list().stream()
                            .collect(Collectors.toMap(AmChatbotGreetTask::getId, task -> task));

                    Map<Integer, List<AmChatbotOptionsItems>> optionsItemsMap = new HashMap<>();
                    for (AmChatbotGreetResult greetResult : greetResults) {
                        AmChatbotGreetTask greetTask = taskMap.get(greetResult.getTaskId());
                        if (greetTask == null) {
                            log.error("复聊任务跳过: 未找到打招呼的任务: {}", greetResult.getTaskId());
                            continue;
                        }

                        // 获取职位选项
                        AmChatbotPositionOption positionOption = amChatbotPositionOptionService.lambdaQuery()
                                .eq(AmChatbotPositionOption::getAccountId, accountId)
                                .eq(AmChatbotPositionOption::getPositionId, greetTask.getPositionId())
                                .one();

                        if (positionOption == null) {
                            log.info("复聊任务跳过: 账号:{}, 未找到对应的职位", accountId);
                            continue;
                        }

                        if (!optionsItemsMap.containsKey(positionOption.getId())) {
                            List<AmChatbotOptionsItems> optionsItems = amChatbotOptionsItemsService.lambdaQuery()
                                    .eq(AmChatbotOptionsItems::getOptionId, positionOption.getId()).list();
                            optionsItemsMap.put(positionOption.getId(), optionsItems);
                        }

                        // 构建选项映射
                        Map<Integer, AmChatbotOptionsItems> dayToItemMap = optionsItemsMap.get(positionOption.getId())
                                .stream().collect(Collectors.toMap(AmChatbotOptionsItems::getDayNum, item -> item));

                        // 判断与创建时间的日期差, 下面是打招呼时间
                        LocalDateTime createTime = greetResult.getCreateTime();
                        int daysBetween = Period.between(createTime.toLocalDate(), LocalDate.now()).getDays();

                        if (daysBetween <= 0) continue;

                        AmChatbotOptionsItems todayNeed = dayToItemMap.get(daysBetween);
                        if (todayNeed == null) continue;

                        // 计算执行时间
                        LocalTime targetTime = LocalTime.parse(todayNeed.getExecTime());
                        LocalDateTime targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime);
                        long timestamp = DateUtils.convertLocalDateTimeToTimestamp(targetDateTime);

                        // 更新复聊项并存入Redis
                        greetResult.setRechatItem(todayNeed.getId());
                        amChatbotGreetResultService.updateById(greetResult);

                        jedisClient.zadd(RedisKyeConstant.AmChatBotReChatTask, timestamp, JSONObject.toJSONString(greetResult));
                        log.info("复聊任务已加入队列: 账号:{}, 职位:{}, 时间戳:{}", accountId, positionOption.getPositionId(), timestamp);
                    }

                    // 将账号 ID 添加到 Redis 的 SET 中，标记为已执行
                    jedisClient.sadd(todayKey, accountId);
                }

                // 设置 Redis Key 过期时间为 1 天
                jedisClient.expire(todayKey, 24 * 60 * 60);

                log.info("复聊定时任务执行完成");
            } catch (Exception e) {
                log.error("复聊定时任务执行失败: {}", e.getMessage(), e);
            } finally {
                lock.unlock();
            }
        } else {
            log.warn("复聊定时任务未执行，因锁未获取成功");
        }
    }


    /**
     * 处理复聊任务
     */
    @Scheduled(cron = "0/20 * * * * ?")
    public void deal_rechat_timer() {
        Set<String> reChatTasks = jedisClient.zrangeByScore(RedisKyeConstant.AmChatBotReChatTask, 0, Double.valueOf(System.currentTimeMillis()));
        for (String reChatTask : reChatTasks) {

            try {
                AmChatbotGreetResult amChatbotGreetResult = JSONObject.parseObject(reChatTask, AmChatbotGreetResult.class);
                if (Objects.isNull(amChatbotGreetResult)) {
                    log.error("复聊任务处理失败,amChatbotGreetResult解析:{}", reChatTask);
                    continue;
                }

                // 查询出对应的任务
                Integer rechatItem = amChatbotGreetResult.getRechatItem();
                AmChatbotOptionsItems amChatbotOptionsItems = amChatbotOptionsItemsService.getById(rechatItem);
                if (Objects.isNull(amChatbotOptionsItems)) {
                    log.error("复聊任务处理失败,未找到对应的任务:{}", rechatItem);
                    continue;
                }

                String accountId = amChatbotGreetResult.getAccountId();
                AmResume amResume = amResumeService.getOne(new LambdaQueryWrapper<AmResume>().eq(AmResume::getUid, amChatbotGreetResult.getUserId()), false);
                if (Objects.isNull(amResume)) {
                    log.error("复聊任务处理失败,未找到用户:{}", amChatbotGreetResult.getUserId());
                    continue;
                }

                AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(accountId);

                //判断是否在线
                if (Objects.isNull(amZpLocalAccouts) || amZpLocalAccouts.getState() == AmLocalAccountStatusEnums.OFFLINE.getStatus()) {
                    log.error("复聊任务处理失败,未找到账号或账号已下线:{}", accountId);
                    continue;
                }

                LambdaQueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new LambdaQueryWrapper<>();
                greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getAccountId, accountId);
                greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getIsAllOn, 1);
                AmChatbotGreetConfig greetConfig = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper,false);
                if (greetConfig == null  ||  greetConfig.getIsRechatOn() == 0) {
                    log.info("复聊任务跳过: 账号:{}, 未找到复聊任务配置 或 全部开关关闭中 或 未开启复聊 greetConfig={}", accountId,greetConfig);
                    continue;
                }

                String conversationId = amZpLocalAccouts.getExtBossId() + "_" + amResume.getUid();

                // 查询用户是否已经回复消息
                LambdaQueryWrapper<AmChatMessage> chatMessageQueryWrapper = new LambdaQueryWrapper<>();
                chatMessageQueryWrapper.eq(AmChatMessage::getConversationId, conversationId);
                AmChatMessage amChatMessage = amChatMessageService.getOne(chatMessageQueryWrapper, false);

                //
                if (Objects.nonNull(amChatMessage)) {
                    log.info("用户已经回复过消息:{}, conversationId={}", amChatMessage,conversationId);
                    chatMessageQueryWrapper.eq(AmChatMessage::getType, -1);
                    AmChatMessage chatMessage = amChatMessageService.getOne(chatMessageQueryWrapper, false);
                    if (Objects.isNull(chatMessage)) {
                        log.info("用户已经回复过消息:{}", chatMessage);
                        continue;
                    }
                }

                buildReChatTask(amResume, amChatbotOptionsItems, amChatbotGreetResult, amZpLocalAccouts);
                jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
            } catch (Exception e) {
                log.error("复聊任务处理失败,未找到打招呼的任务任务:{}", reChatTask);
            }
        }
    }


    private void buildReChatTask(AmResume amResume, AmChatbotOptionsItems amChatbotOptionsItems, AmChatbotGreetResult amChatbotGreetResult, AmZpLocalAccouts amZpLocalAccouts) {

        AmClientTasks amClientTasks = new AmClientTasks();
        JSONObject jsonObject = new JSONObject();
        JSONObject messageObject = new JSONObject();
        JSONObject searchObject = new JSONObject();
        searchObject.put("encrypt_friend_id", amResume.getEncryptGeekId());
        searchObject.put("name", amResume.getName());
        messageObject.put("content", amChatbotOptionsItems.getContent());
        jsonObject.put("user_id", amChatbotGreetResult.getUserId());
        jsonObject.put("message", messageObject);
        jsonObject.put("search_data", searchObject);

        amClientTasks.setTaskType(ClientTaskTypeEnums.SEND_MESSAGE.getType());
        amClientTasks.setOrderNumber(ClientTaskTypeEnums.SEND_MESSAGE.getOrder());
        amClientTasks.setBossId(amZpLocalAccouts.getId());
        amClientTasks.setData(jsonObject.toJSONString());
        amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
        amClientTasks.setCreateTime(LocalDateTime.now());
        boolean result = amClientTasksService.save(amClientTasks);
        log.info("生成复聊任务处理结果 amClientTask={} result={}", JSONObject.toJSONString(amClientTasks), result);
        if (result) {
            // 生成聊天记录
            AmChatMessage amChatMessage = new AmChatMessage();
            amChatMessage.setConversationId(amChatbotGreetResult.getAccountId() + "_" + amResume.getUid());
            amChatMessage.setUserId(Long.parseLong(amZpLocalAccouts.getExtBossId()));
            amChatMessage.setRole(AIRoleEnum.ASSISTANT.getRoleName());
            amChatMessage.setType(-1);
            amChatMessage.setContent(amChatbotOptionsItems.getContent());
            amChatMessage.setCreateTime(LocalDateTime.now());
            boolean save = amChatMessageService.save(amChatMessage);
            log.info("生成聊天记录结果 amChatMessage={} result={}", JSONObject.toJSONString(amChatMessage), save);
        }
    }


}
