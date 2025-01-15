package com.open.hr.ai.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.constant.RedisKyeConstant;
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
                        .eq(AmZpLocalAccouts::getState, "active")
                        .list();
                for (AmZpLocalAccouts localAccout : localAccouts) {
                    //查看账号是否开启打招呼
                    QueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new QueryWrapper<>();
                    greetConfigQueryWrapper.eq("account_id", localAccout.getId());
                    AmChatbotGreetConfig one = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper, false);
                    if (Objects.isNull(one) || one.getIsGreetOn() == 0) {
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
                                amChatbotGreetMessages.setTaskType(0);
                                amChatbotGreetMessages.setAccountId(amChatbotGreetTask.getAccountId());
                                amChatbotGreetMessages.setIsSystemSend(1);
                                amChatbotGreetMessages.setContent(GREET_MESSAGE);
                                amChatbotGreetMessages.setCreateTime(DateUtils.formatDate(new Date(), "Y-m-d"));
                                amChatbotGreetMessages.setCreateTimestamp(LocalDateTime.now().getSecond());
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
                                    continue;
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
                                amClientTasks.setTaskType("greet");
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


    /**
     * 处理临时任务
     * 处理定时任务, 每小时处理一次
     * 下面跟着php逻辑实现, 后续改造
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void run_tmp_timer() {
        Lock lock = DistributedLockUtils.getLock("run_tmp_timer", 30);
        if (lock.tryLock()) {
            try {
                LambdaQueryWrapper<AmZpLocalAccouts> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AmZpLocalAccouts::getState, "active");
                List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.list(queryWrapper);
                for (AmZpLocalAccouts localAccout : localAccouts) {
                    //查看账号是否开启打招呼
                    LambdaQueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new LambdaQueryWrapper<>();
                    greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getAccountId, localAccout.getId());
                    AmChatbotGreetConfig one = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper, false);
                    if (Objects.isNull(one) || one.getIsGreetOn() == 0) {
                        continue;
                    }
                    LambdaQueryWrapper<AmChatbotGreetTask> greetTaskQueryWrapper = new LambdaQueryWrapper<>();
                    greetTaskQueryWrapper.eq(AmChatbotGreetTask::getAccountId, localAccout.getId());
                    greetTaskQueryWrapper.eq(AmChatbotGreetTask::getTaskType, 1);
                    greetTaskQueryWrapper.eq(AmChatbotGreetTask::getStatus, 0);
                    List<AmChatbotGreetTask> amChatbotGreetTasks = amChatbotGreetTaskService.list(greetTaskQueryWrapper);
                    if (CollectionUtils.isEmpty(amChatbotGreetTasks)) {
                        continue;
                    }
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
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
                                amChatbotGreetMessages.setTaskType(0);
                                amChatbotGreetMessages.setAccountId(amChatbotGreetTask.getAccountId());
                                amChatbotGreetMessages.setIsSystemSend(1);
                                amChatbotGreetMessages.setContent(GREET_MESSAGE);
                                amChatbotGreetMessages.setCreateTime(DateUtils.formatDate(new Date(), "Y-m-d"));
                                amChatbotGreetMessages.setCreateTimestamp(LocalDateTime.now().getSecond());
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
                                amClientTasks.setTaskType("greet");
                                amClientTasks.setStatus(0);
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
        return false; // 假设未执行
    }


    /**
     * 复聊任务处理, 筛选出今天需要复聊的用户,并用redis存入队列,根据具体的执行时间进行复聊
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void run_rechat_timer() {

        Lock lock = DistributedLockUtils.getLock("run_tmp_timer", 30);
        if (lock.tryLock()) {
            try {
                // todo 先用代码实现逻辑, 后续慢慢优化
                // 查询出所有的账号
                List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.lambdaQuery().eq(AmZpLocalAccouts::getState, "active").list();
                for (AmZpLocalAccouts localAccout : localAccouts) {
                    // 查询复聊方案
                    AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(new QueryWrapper<AmChatbotGreetConfig>().eq("account_id", localAccout.getId()), false);
                    if (Objects.isNull(amChatbotGreetConfig) || amChatbotGreetConfig.getIsRechatOn() == 0) {
                        log.info("复聊任务处理失败,未找到复聊任务或者账号:{}, 未开启复聊", localAccout.getId());
                        continue;
                    }

                    //加载已经打过招呼的用户
                    List<AmChatbotGreetResult> amChatbotGreetResults = amChatbotGreetResultService.lambdaQuery().eq(AmChatbotGreetResult::getAccountId, localAccout.getId()).eq(AmChatbotGreetResult::getSuccess, 1).list();
                    for (AmChatbotGreetResult amChatbotGreetResult : amChatbotGreetResults) {

                        AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getById(amChatbotGreetResult.getTaskId());
                        if (Objects.isNull(amChatbotGreetTask)) {
                            log.error("复聊任务处理失败,未找到打招呼的任务任务:{}", amChatbotGreetResult.getTaskId());
                            continue;
                        }
                        log.info("复聊任务处理开始, 账号:{}", localAccout.getId());
                        AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(new LambdaQueryWrapper<AmChatbotPositionOption>().eq(AmChatbotPositionOption::getAccountId, localAccout.getId()).eq(AmChatbotPositionOption::getPositionId, amChatbotGreetTask.getPositionId()), false);
                        if (Objects.isNull(amChatbotPositionOption)) {
                            log.info("复聊任务处理开始, 账号:{}, 未找到对应的职位", localAccout.getId());
                            continue;
                        }
                        log.info("复聊任务处理开始, 账号:{}, 职位:{}", localAccout.getId(), amChatbotPositionOption.getPositionId());
                        HashMap<Integer, AmChatbotOptionsItems> amChatbotOptionsItemsHashMap = new HashMap<>();
                        List<AmChatbotOptionsItems> amChatbotOptionsItems = amChatbotOptionsItemsService.lambdaQuery().eq(AmChatbotOptionsItems::getOptionId, amChatbotPositionOption.getId()).list();
                        for (AmChatbotOptionsItems amChatbotOptionsItem : amChatbotOptionsItems) {
                            amChatbotOptionsItemsHashMap.put(amChatbotOptionsItem.getDayNum(), amChatbotOptionsItem);
                        }


                        LocalDateTime createTime = amChatbotGreetResult.getCreateTime();
                        // 判断当前时间比创建时间大于多少天
                        LocalDateTime now = LocalDateTime.now();

                        // 计算日期差
                        int daysBetween = Period.between(createTime.toLocalDate(), now.toLocalDate()).getDays();
                        AmChatbotOptionsItems todayNeed = amChatbotOptionsItemsHashMap.get(daysBetween);
                        if (Objects.isNull(todayNeed)) {
                            continue;
                        }
                        String execTime = todayNeed.getExecTime();
                        if (daysBetween <= 0) continue;
                        // 如果是第n天,则执行时间为今天的这个时间
                        // 将数据库返回的时间字符串转换为 LocalTime 对象
                        LocalTime targetTime = LocalTime.parse(execTime);
                        // 获取当前日期
                        LocalDate today = LocalDate.now();
                        // 将日期和时间组合成 LocalDateTime 对象
                        LocalDateTime targetDateTime = LocalDateTime.of(today, targetTime);
                        // 获取当前需要执行的时间戳
                        Long localDateTimeToTimestamp = DateUtils.convertLocalDateTimeToTimestamp(targetDateTime);
                        amChatbotGreetResult.setRechatItem(todayNeed.getId());
                        amChatbotGreetResultService.updateById(amChatbotGreetResult);
                        jedisClient.zadd(RedisKyeConstant.AmChatBotReChatTask, localDateTimeToTimestamp, JSONObject.toJSONString(amChatbotGreetResult));
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }


    /**
     * 处理复聊任务
     */
//    @Scheduled(fixedDelay = 10000)
    public void deal_rechat_timer() {
        Set<String> reChatTasks = jedisClient.zrangeByScore(RedisKyeConstant.AmChatBotReChatTask, 0, Double.valueOf(System.currentTimeMillis()));
        for (String reChatTask : reChatTasks) {
            AmChatbotGreetResult amChatbotGreetResult = JSONObject.parseObject(reChatTask, AmChatbotGreetResult.class);
            if (Objects.isNull(amChatbotGreetResult)) {
                continue;
            }
            AmChatbotGreetResult chatbotGreetResult = amChatbotGreetResultService.getById(amChatbotGreetResult.getId());
            if (chatbotGreetResult.getSuccess() == 2) {
                // 收到招聘者的回复, 任务已经结束,则不再进行复聊任务
                continue;
            }
            // 查询出对应的任务
            Integer rechatItem = amChatbotGreetResult.getRechatItem();
            AmChatbotOptionsItems amChatbotOptionsItems = amChatbotOptionsItemsService.getById(rechatItem);
            if (Objects.isNull(amChatbotOptionsItems)) {
                continue;
            }
            String accountId = amChatbotGreetResult.getAccountId();
            AmResume amResume = amResumeService.getOne(new QueryWrapper<AmResume>().eq("uid", amChatbotGreetResult.getUserId()), false);
            if (Objects.isNull(amResume)) {
                log.error("复聊任务处理失败,未找到用户:{}", amChatbotGreetResult.getUserId());
                continue;
            }
            AmClientTasks amClientTasks = new AmClientTasks();
            amClientTasks.setTaskType("send_message");
            amClientTasks.setBossId(accountId);
            JSONObject jsonObject = new JSONObject();
            JSONObject messageObject = new JSONObject();
            JSONObject searchObject = new JSONObject();
            searchObject.put("encrypt_friend_id", amResume.getEncryptGeekId());
            searchObject.put("name", amResume.getName());
            messageObject.put("content", amChatbotOptionsItems.getContent());
            jsonObject.put("user_id", amChatbotGreetResult.getUserId());
            jsonObject.put("message", messageObject);
            jsonObject.put("search_data", searchObject);
            amClientTasks.setData(jsonObject.toJSONString());
            amClientTasks.setStatus(0);
            amClientTasks.setCreateTime(LocalDateTime.now());
            amClientTasks.setBossId(accountId);
            boolean result = amClientTasksService.save(amClientTasks);
            log.info("复聊任务处理结果 amClientTask={} result={}", JSONObject.toJSONString(amClientTasks), result);
            jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
        }
    }


}
