package com.open.hr.ai.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.ai.eros.db.mysql.hr.vo.AmGreetConditionVo;
import com.open.hr.ai.constant.*;
import com.open.hr.ai.convert.AmChatBotGreetNewConditionConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
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
    private AmChatbotGreetConditionNewServiceImpl amChatbotGreetConditionNewService;

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

    @Resource
    private AmNewMaskServiceImpl amNewMaskService;


    private static final String GREET_MESSAGE = "你好";


    /**
     * 处理打招呼的定时任务
     * 下面是java 优化后的数据
     */

    @Scheduled(cron = "0/5 * * * * ?")
//    @Scheduled(cron = "0/20 * * * * ?")
    public void run_scheduled_greet_timer() {
        Lock lock = DistributedLockUtils.getLock("run_scheduled_greet_timer", 20);
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
                    String todayDate = RedisKyeConstant.AmChatBotGreetTask +":"+DateUtils.getTodayDate();
                    for (AmChatbotGreetTask amChatbotGreetTask : amChatbotGreetTasks) {
                        Integer taskNum = amChatbotGreetTask.getTaskNum();
                        if (Objects.isNull(taskNum) || taskNum <= 0) {
                            continue;
                        }
                        String execTime = amChatbotGreetTask.getExecTime();
                        // 将execTime 转化成时间戳
                        long taskExecTimeStamp = DateUtils.convertToTimestamp(execTime);
                        if (taskExecTimeStamp >= System.currentTimeMillis()) {
                            if (jedisClient.sismember(todayDate, amChatbotGreetTask.getId().toString())) {
                                log.info("打招呼任务跳过: id:{}，今天已经执行过", amChatbotGreetTask.getId());
                                continue;
                            }

                            jedisClient.zadd(RedisKyeConstant.AmChatBotGreetTask, taskExecTimeStamp, JSONObject.toJSONString(amChatbotGreetTask));
                            jedisClient.sadd(todayDate, amChatbotGreetTask.getId().toString());
                            //设置一天缓存时间
                            jedisClient.expire(todayDate, 24 * 60 * 60);
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        else {
            log.warn("打招呼定时任务未执行，因锁未获取成功");
        }
    }





    /**
     * 复聊任务处理, 筛选出今天需要复聊的用户,并用redis存入队列,根据具体的执行时间进行复聊
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void runRechatTimer() {
        Lock lock = DistributedLockUtils.getLock("run_tmp_timer", 30);
        if (lock.tryLock()) {
            try {
                log.info("复聊定时任务开始执行...");
                // 获取当天的 Redis Key
                String todayKey = "rechat_executed_accounts:" + DateUtils.getTodayDate();

                // 查询所有活跃账号
                List<AmZpLocalAccouts> localAccounts = amZpLocalAccoutsService.lambdaQuery()
                        .ne(AmZpLocalAccouts::getState, AmLocalAccountStatusEnums.OFFLINE.getStatus())
                        .list();

                if (CollectionUtils.isEmpty(localAccounts)) {
                    log.info("复聊任务跳过: 没有活跃账号");
                    return;
                }

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
                    if (CollectionUtils.isEmpty(taskIds)) {
                        log.info("复聊任务跳过: 账号:{},taskIds 为null 未找到打招呼的任务", accountId);
                        continue;
                    }
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
        Lock lock = DistributedLockUtils.getLock("deal_rechat_timer", 30);
        if (lock.tryLock()) {
            try {
                Set<String> reChatTasks = jedisClient.zrangeByScore(RedisKyeConstant.AmChatBotReChatTask, 0, Double.valueOf(System.currentTimeMillis()));
                for (String reChatTask : reChatTasks) {
                    try {
                        AmChatbotGreetResult amChatbotGreetResult = JSONObject.parseObject(reChatTask, AmChatbotGreetResult.class);
                        if (Objects.isNull(amChatbotGreetResult)) {
                            log.error("复聊任务处理失败,amChatbotGreetResult解析:{}", reChatTask);
                            jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                            continue;
                        }

                        // 查询出对应的任务
                        Integer rechatItem = amChatbotGreetResult.getRechatItem();
                        AmChatbotOptionsItems amChatbotOptionsItems = amChatbotOptionsItemsService.getById(rechatItem);
                        if (Objects.isNull(amChatbotOptionsItems)) {
                            log.error("复聊任务处理失败,未找到对应的任务:{}", rechatItem);
                            jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                            continue;
                        }

                        String accountId = amChatbotGreetResult.getAccountId();
                        AmResume amResume = amResumeService.getOne(new LambdaQueryWrapper<AmResume>().eq(AmResume::getUid, amChatbotGreetResult.getUserId()).eq(AmResume::getAccountId,accountId), false);
                        if (Objects.isNull(amResume)) {
                            log.error("复聊任务处理失败,未找到用户:{}", amChatbotGreetResult.getUserId());
                            jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                            continue;
                        }

                        //判断简历状态
                        if (ReviewStatusEnums.INTERVIEW_ARRANGEMENT.getStatus().equals(amResume.getType())
                                || ReviewStatusEnums.OFFER_ISSUED.getStatus().equals(amResume.getType())
                                || ReviewStatusEnums.ONBOARD.getStatus().equals(amResume.getType())
                                || ReviewStatusEnums.ABANDON.getStatus().equals(amResume.getType())) {
                            log.error("复聊任务处理失败,简历状态不是初筛 Uid:{}", amResume.getUid());
                            jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                            continue;
                        }

                        AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(accountId);

                        //判断是否在线
                        if (Objects.isNull(amZpLocalAccouts) || amZpLocalAccouts.getState() == AmLocalAccountStatusEnums.OFFLINE.getStatus()) {
                            log.error("复聊任务处理失败,未找到账号或账号已下线:{}", accountId);
                            jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                            continue;
                        }

                        LambdaQueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new LambdaQueryWrapper<>();
                        greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getAccountId, accountId);
                        greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getIsAllOn, 1);
                        AmChatbotGreetConfig greetConfig = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper, false);
                        if (greetConfig == null || greetConfig.getIsRechatOn() == 0) {
                            log.info("复聊任务跳过: 账号:{}, 未找到复聊任务配置 或 全部开关关闭中 或 未开启复聊 greetConfig={}", accountId, greetConfig);
                            jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                            continue;
                        }

                        AmChatbotPositionOption positionOption = amChatbotPositionOptionService.lambdaQuery()
                                .eq(AmChatbotPositionOption::getAccountId, accountId)
                                .eq(AmChatbotPositionOption::getPositionId, amResume.getPostId())
                                .one();

                        if (positionOption != null) {
                            Long amMaskId = positionOption.getAmMaskId();
                            AmNewMask amNewMask = amNewMaskService.getById(amMaskId);
                            if (Objects.isNull(amNewMask)) {
                                log.error("复聊任务处理失败,未找到对应的maskId:{}", amMaskId);
                                jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                                continue;
                            }
                        }else {
                            log.info("复聊任务处理失败,未找到对应positionOption配置 bossId={},positionId={}",accountId,amResume.getPostId());
                            jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                            continue;
                        }

                        String conversationId = amZpLocalAccouts.getId() + "_" + amResume.getUid();

                        // 查询今天是否已经回复过消息
//                        LambdaQueryWrapper<AmChatMessage> chatMessageQueryWrapper = new LambdaQueryWrapper<>();
//                        chatMessageQueryWrapper.eq(AmChatMessage::getConversationId, conversationId);
//                        chatMessageQueryWrapper.ne(AmChatMessage::getType, -1);
//                        chatMessageQueryWrapper.ge(AmChatMessage::getCreateTime, LocalDate.now().atStartOfDay());
//                        AmChatMessage amChatMessage = amChatMessageService.getOne(chatMessageQueryWrapper, false);


//                        if (Objects.nonNull(amChatMessage)) {
//                            log.info("用户已经回复过消息:{}, conversationId={}", amChatMessage, conversationId);
//                            LambdaQueryWrapper<AmChatMessage> aiChatMessageQueryWrapper = new LambdaQueryWrapper<>();
//                            aiChatMessageQueryWrapper.orderByDesc(AmChatMessage::getCreateTime);
//                            // 如果最后一条消息是 assistant 或者 没有 assistant 的数据,则需要发送复聊任务
//                            aiChatMessageQueryWrapper.last("limit 1");
//                            AmChatMessage chatMessage = amChatMessageService.getOne(aiChatMessageQueryWrapper, false);
//                            if (Objects.nonNull(chatMessage) ) {
//                                log.info("AI今天已经回复过消息:{}", chatMessage);
//                                jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
//                                continue;
//                            }
//                        }
                        // 获取最后一条消息
                        LambdaQueryWrapper<AmChatMessage> lastMessageQueryWrapper = new LambdaQueryWrapper<>();
                        lastMessageQueryWrapper.eq(AmChatMessage::getConversationId, conversationId);
                        lastMessageQueryWrapper.orderByDesc(AmChatMessage::getCreateTime);
                        lastMessageQueryWrapper.last("limit 1");
                        AmChatMessage lastMessage = amChatMessageService.getOne(lastMessageQueryWrapper, false);

                        String chatId = "";
                        if (Objects.nonNull(lastMessage) &&!"0".equals(lastMessage.getChatId())){
                            chatId = lastMessage.getChatId();
                        }
                        buildReChatTask(amResume, amChatbotOptionsItems, amChatbotGreetResult, amZpLocalAccouts,chatId);
                        jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                    } catch (Exception e) {
                        jedisClient.zrem(RedisKyeConstant.AmChatBotReChatTask, reChatTask);
                        log.error("复聊任务处理异常:{},清除复聊缓存", reChatTask,e);
                    }
                }
            } catch (Exception e) {
                log.error("复聊任务处理异常");
            } finally {
                lock.unlock();
            }
        } else {
            log.info("处理复聊任务未执行，因锁未获取成功");
        }
    }


    /**
     * 处理定时打招呼任务
     */
    @Transactional
    @Scheduled(cron = "0 * * * * ?")
    public void deal_greet_timer() {
        Lock lock = DistributedLockUtils.getLock("deal_greet_timer", 30);
        if (lock.tryLock()) {
            try {
                Set<String> greetTasks = jedisClient.zrangeByScore(RedisKyeConstant.AmChatBotGreetTask, 0, Double.valueOf(System.currentTimeMillis()));
                for (String greetTask : greetTasks) {
                    try {
                        jedisClient.zrem(RedisKyeConstant.AmChatBotGreetTask, greetTask);
                        AmChatbotGreetTask amChatbotGreetTask = JSONObject.parseObject(greetTask, AmChatbotGreetTask.class);
                        if (Objects.isNull(amChatbotGreetTask)) {
                            log.error("打招呼任务处理失败,amChatbotGreetResult解析:{}", greetTask);
                            continue;
                        }
                        AmChatbotGreetTask chatbotGreetTask = amChatbotGreetTaskService.getById(amChatbotGreetTask.getId());
                        if (Objects.isNull(chatbotGreetTask)) {
                            log.error("打招呼任务处理失败,未找到打招呼的任务任务:{}", amChatbotGreetTask.getId());
                            continue;
                        }

                        AmZpLocalAccouts zpLocalAccouts = amZpLocalAccoutsService.getById(amChatbotGreetTask.getAccountId());
                        if (Objects.isNull(zpLocalAccouts)) {
                            log.error("打招呼任务处理失败,未找到账号:{}", amChatbotGreetTask.getAccountId());
                            continue;
                        }
                        //判断用户是否在线
                        if (zpLocalAccouts.getState().equals( AmLocalAccountStatusEnums.OFFLINE.getStatus())) {
                            log.error("打招呼任务处理失败,账号已下线:{}", zpLocalAccouts.getId());
                            continue;
                        }

                        LambdaQueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new LambdaQueryWrapper<>();
                        greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getAccountId, zpLocalAccouts.getId());
                        AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper, false);
                        if (Objects.isNull(amChatbotGreetConfig) || amChatbotGreetConfig.getIsAllOn() == 0 || amChatbotGreetConfig.getIsGreetOn() == 0) {
                            log.info("打招呼任务跳过: 账号:{}, 未找到打招呼任务配置 或总开关关闭 或未开启打招呼 greetConfig={}", zpLocalAccouts.getId(), amChatbotGreetConfig);
                            continue;
                        }


                        // 执行任务
                        AmChatbotGreetMessages amChatbotGreetMessages = new AmChatbotGreetMessages();
                        amChatbotGreetMessages.setTaskId(amChatbotGreetTask.getId());
                        amChatbotGreetMessages.setTaskType(MessageTypeEnums.daily_greet.getCode());
                        amChatbotGreetMessages.setAccountId(amChatbotGreetTask.getAccountId());
                        amChatbotGreetMessages.setIsSystemSend(1);
                        amChatbotGreetMessages.setContent(GREET_MESSAGE);
                        amChatbotGreetMessages.setCreateTime(LocalDateTime.now());
                        amChatbotGreetMessagesService.save(amChatbotGreetMessages);
                        //更新task临时status的状态
                        amChatbotGreetTask.setStatus(1);
                        amChatbotGreetTask.setUpdateTime(LocalDateTime.now());
                        amChatbotGreetTaskService.updateById(amChatbotGreetTask);

                        LambdaQueryWrapper<AmChatbotGreetConditionNew> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(AmChatbotGreetConditionNew::getAccountId, amChatbotGreetTask.getAccountId());
                        queryWrapper.eq(AmChatbotGreetConditionNew::getPositionId, amChatbotGreetTask.getPositionId());
                        AmChatbotGreetConditionNew condition = amChatbotGreetConditionNewService.getOne(queryWrapper, false);

                        if (Objects.isNull(condition)) {
                            // 如果为空,则默认取第一个
                            condition = amChatbotGreetConditionNewService.getById(1);
                        }
                        AmGreetConditionVo amGreetConditionVo = AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(condition);

                        AmPosition amPosition = amPositionService.getById(amChatbotGreetTask.getPositionId());
                        if (Objects.isNull(amPosition) || amPosition.getIsDeleted() == 1 || amPosition.getIsOpen() ==0) {
                            log.error("职位已删除: amPosition={}", amPosition);
                            return;
                        }
                        // 创建 JSON 对象并根据逻辑填充数据
                        JSONObject conditions = new JSONObject();
                        conditions.put("学历要求", amGreetConditionVo.getDegree() != null ? amGreetConditionVo.getDegree() : Collections.singletonList(-1));
                        conditions.put("薪资待遇", amGreetConditionVo.getSalary() != null ?amGreetConditionVo.getSalary()  : "不限");
                        conditions.put("经验要求", amGreetConditionVo.getWorkYears() != null ? amGreetConditionVo.getWorkYears() :  Collections.singletonList("不限"));
                        conditions.put("求职意向", amGreetConditionVo.getIntention() != null ?amGreetConditionVo.getIntention() : Collections.singletonList(-1));
                        conditions.put("年龄", amGreetConditionVo.getAge() != null ? amGreetConditionVo.getAge() : -1);
                        conditions.put("性别", amGreetConditionVo.getGender() != null ? amGreetConditionVo.getGender() : "不限");


                        // 创建任务
                        AmClientTasks amClientTasks = new AmClientTasks();
                        amClientTasks.setId(UUID.randomUUID().toString());
                        amClientTasks.setBossId(zpLocalAccouts.getId());
                        amClientTasks.setTaskType(ClientTaskTypeEnums.GREET.getType());
                        amClientTasks.setOrderNumber(ClientTaskTypeEnums.GREET.getOrder());
                        amClientTasks.setStatus(0);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("greetId", amChatbotGreetTask.getId());
                        jsonObject.put("job_name", amPosition.getName());
                        jsonObject.put("job_id", amPosition.getEncryptId());

                        jsonObject.put("conditions", conditions);
                        jsonObject.put("times", amChatbotGreetTask.getTaskNum());
                        JSONObject messageObject = new JSONObject();
//                        messageObject.put("content", GREET_MESSAGE);

                        AmChatbotPositionOption positionOption = amChatbotPositionOptionService.lambdaQuery()
                                .eq(AmChatbotPositionOption::getAccountId, zpLocalAccouts.getId())
                                .eq(AmChatbotPositionOption::getPositionId, chatbotGreetTask.getPositionId())
                                .one();

                        if (positionOption != null) {
                            Long amMaskId = positionOption.getAmMaskId();
                            AmNewMask amNewMask = amNewMaskService.getById(amMaskId);
                            if (Objects.nonNull(amNewMask) && StringUtils.isNotBlank(amNewMask.getGreetMessage())){
                                messageObject.put("content", amNewMask.getGreetMessage());
                            }
                        }else {
                            log.info("打招呼任务追加消息失败,未找到对应的职位:{}", chatbotGreetTask.getPositionId());
                        }
                        jsonObject.put("message", messageObject);
                        amClientTasks.setData(jsonObject.toJSONString());
                        amClientTasks.setCreateTime(LocalDateTime.now());
                        amClientTasks.setUpdateTime(LocalDateTime.now());
                        amClientTasksService.save(amClientTasks);
                        log.info("打招呼任务处理结果  amClientTask={}",JSONObject.toJSONString(amClientTasks));
                    } catch (Exception e) {
                        log.error("打招呼任务处理失败,未找到打招呼的任务任务:{}", greetTask);
                    }
                }

            }finally {
                lock.unlock();
            }
        } else {
            log.info("打招呼定时任务未执行，因锁未获取成功");
        }

    }


    private void buildReChatTask(AmResume amResume, AmChatbotOptionsItems amChatbotOptionsItems, AmChatbotGreetResult amChatbotGreetResult, AmZpLocalAccouts amZpLocalAccouts,String chatId) {

        AmClientTasks amClientTasks = new AmClientTasks();
        JSONObject jsonObject = new JSONObject();
        JSONObject searchObject = new JSONObject();
        searchObject.put("encrypt_friend_id", amResume.getEncryptGeekId());
        searchObject.put("name", amResume.getName());
        jsonObject.put("user_id", amChatbotGreetResult.getUserId());
        jsonObject.put("messages", Collections.singletonList(amChatbotOptionsItems.getContent()));
        jsonObject.put("search_data", searchObject);
        if (StringUtils.isNotBlank(chatId)){
            jsonObject.put("rechat_last_message_id", chatId);
        }

        jsonObject.put("rechat",true);
        amClientTasks.setTaskType(ClientTaskTypeEnums.SEND_RECHAT_MESSAGE.getType());
        //复聊任务优先级最低
        amClientTasks.setOrderNumber(ClientTaskTypeEnums.SEND_RECHAT_MESSAGE.getOrder());
        amClientTasks.setSubType("rechat");
        amClientTasks.setBossId(amZpLocalAccouts.getId());
        amClientTasks.setData(jsonObject.toJSONString());
        amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
        amClientTasks.setCreateTime(LocalDateTime.now());
        boolean result = amClientTasksService.save(amClientTasks);

        // 执行任务
        AmChatbotGreetMessages amChatbotGreetMessages = new AmChatbotGreetMessages();
        amChatbotGreetMessages.setTaskId(amChatbotOptionsItems.getId());
        amChatbotGreetMessages.setTaskType(MessageTypeEnums.rechat.getCode());
        amChatbotGreetMessages.setAccountId(amZpLocalAccouts.getId());
        amChatbotGreetMessages.setIsSystemSend(1);
        amChatbotGreetMessages.setContent(amChatbotOptionsItems.getContent());
        amChatbotGreetMessages.setCreateTime(LocalDateTime.now());
        amChatbotGreetMessages.setFromUid(Integer.parseInt(amChatbotGreetResult.getUserId()));
        amChatbotGreetMessagesService.save(amChatbotGreetMessages);
        //更新task临时status的状态


        log.info("生成复聊任务处理结果 amClientTask={} result={}", JSONObject.toJSONString(amClientTasks), result);
        if (result) {
            // 生成聊天记录
            AmChatMessage amChatMessage = new AmChatMessage();
            amChatMessage.setConversationId(amChatbotGreetResult.getAccountId() + "_" + amResume.getUid());
            amChatMessage.setUserId(Long.parseLong(amZpLocalAccouts.getExtBossId()));
            amChatMessage.setRole(AIRoleEnum.ASSISTANT.getRoleName());
            amChatMessage.setType(-1);
            amChatMessage.setChatId(UUID.randomUUID().toString());
            amChatMessage.setContent(amChatbotOptionsItems.getContent());
            amChatMessage.setCreateTime(LocalDateTime.now());
            boolean save = amChatMessageService.save(amChatMessage);
            log.info("生成聊天记录结果 amChatMessage={} result={}", JSONObject.toJSONString(amChatMessage), save);
        }
    }




    /**
     * 处理定时删除过期的复聊任务
     */
    @Transactional
    @Scheduled(cron = "0 0/5 * * * ?")
    public void delete_reChat_timer() {
        Lock lock = DistributedLockUtils.getLock("delete_reChat_timer", 30);
        if (lock.tryLock()) {
            try {
                // 查出所有包含rechat的复聊服务,且状态为未开始或者开始的,判断他们的创建时间是否超出12小时,如果超出,则修改状态为失败,并且原因为超时
                LambdaQueryWrapper<AmClientTasks> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.like(AmClientTasks::getData, "rechat")
                        .and(wrapper -> wrapper
                                .or(innerWrapper -> innerWrapper
                                        .eq(AmClientTasks::getStatus, AmClientTaskStatusEnums.NOT_START.getStatus())
                                        .or()
                                        .eq(AmClientTasks::getStatus, AmClientTaskStatusEnums.START.getStatus())
                                )
                                .le(AmClientTasks::getCreateTime, LocalDateTime.now().minusHours(12))
                        );
                List<AmClientTasks> amClientTasks = amClientTasksService.list(queryWrapper);

                for (AmClientTasks amClientTask : amClientTasks) {
                    amClientTask.setStatus(AmClientTaskStatusEnums.FAILURE.getStatus());
                    amClientTask.setUpdateTime(LocalDateTime.now());
                    amClientTask.setReason("超时");
                    amClientTasksService.updateById(amClientTask);
                }
            }finally {
                lock.unlock();
            }
        } else {
            log.info("打招呼定时任务未执行，因锁未获取成功");
        }

    }

}
