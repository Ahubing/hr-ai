package com.open.hr.ai.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.AmLocalAccountStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
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
import java.util.*;
import java.util.concurrent.locks.Lock;

@Component
@Slf4j
@EnableScheduling
public class AmZpLocalAccountJob {


    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;

    @Resource
    private AmClientTasksServiceImpl amClientTasksService;



    @Resource
    private AmChatbotGreetConfigServiceImpl amChatbotGreetConfigService;

    private static final String GREET_MESSAGE = "你好";

    private  LocalDateTime emptyTimestamp = LocalDateTime.of(1970, 1, 1, 0, 0, 0); // 特定的空值标识

    // 改成LocalDateTime



    /**
     * 处理定时任务 检测用户的状态, 超过25秒即下线
     */
    // 20秒检测一次
    @Scheduled(cron = "0/20 * * * * ?")
    public void offerLineAccount() {
        Lock lock = DistributedLockUtils.getLock("offline_account", 10);
        if (lock.tryLock()) {
            try {
                List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.list();
                for (AmZpLocalAccouts localAccout : localAccouts) {
                    if (!AmLocalAccountStatusEnums.OFFLINE.getStatus().equals(localAccout.getState())) {
                        // 规定空闲事件超过25秒就认定下线
                        if (Objects.nonNull(localAccout.getUpdateTime()) && (System.currentTimeMillis() - DateUtils.convertLocalDateTimeToTimestamp(localAccout.getUpdateTime())) > 25 * 1000) {
                            log.info("账号:{} 下线", localAccout.getId());
                            localAccout.setState(AmLocalAccountStatusEnums.OFFLINE.getStatus());
                            localAccout.setExtra("");
                            localAccout.setBrowserId("");
                            amZpLocalAccoutsService.updateById(localAccout);
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }



    /**
     * 处理定时任务 每小时根据在线的账号,生成同步岗位的任务
     */
    // 20秒检测一次
//    @Scheduled(cron = "0 0 * * * ?")
    public void getAllPositionJob() {
        Lock lock = DistributedLockUtils.getLock("get_all_job", 10);
        if (lock.tryLock()) {
            try {
                LambdaQueryWrapper<AmZpLocalAccouts> queryWrapper = new QueryWrapper<AmZpLocalAccouts>().lambda();
                queryWrapper.ne(AmZpLocalAccouts::getState, AmLocalAccountStatusEnums.OFFLINE.getStatus());
                List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.list();
                for (AmZpLocalAccouts localAccout : localAccouts) {
                    // 查询这个用户是否存在get_all_job 任务
                    LambdaQueryWrapper<AmClientTasks> queryWrapper1 = new QueryWrapper<AmClientTasks>().lambda();
                    queryWrapper1.eq(AmClientTasks::getBossId, localAccout.getId());
                    queryWrapper1.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.GET_ALL_JOB.getType());
                    queryWrapper1.le(AmClientTasks::getStatus, AmClientTaskStatusEnums.START.getStatus());
                    int count = amClientTasksService.count(queryWrapper1);
                    if (count > 0) {
                        log.info("账号:{} 存在未完成的任务", localAccout.getId());
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("boss_id", localAccout.getId());
                    map.put("browser_id", localAccout.getBrowserId());
                    map.put("page", 1);
                    AmClientTasks amClientTasks = new AmClientTasks();
                    amClientTasks.setId(UUID.randomUUID().toString());
                    amClientTasks.setBossId(localAccout.getId());
                    amClientTasks.setTaskType(ClientTaskTypeEnums.GET_ALL_JOB.getType());
                    amClientTasks.setOrderNumber(ClientTaskTypeEnums.GET_ALL_JOB.getOrder());
                    amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
                    amClientTasks.setData(JSONObject.toJSONString(map));
                    amClientTasks.setCreateTime(LocalDateTime.now());
                    amClientTasks.setUpdateTime(LocalDateTime.now());
                    boolean result = amClientTasksService.save(amClientTasks);
                    log.info("syncPositions save amClientTasks result={}", result);
                }
            } finally {
                lock.unlock();
            }
        }
    }


    /**
     * 每10分钟检测一次, 生成打招呼任务
     */
//    @Scheduled(cron = "0 10 * * * ?")
//    @Scheduled(cron = "0/20 * * * * ?")
    public void dealGreetStatus() {
        Lock lock = DistributedLockUtils.getLock("dealGreetStatus", 20);
        log.info("dealGreetStatus start");
        if (lock.tryLock()) {
            try {
                LambdaQueryWrapper<AmChatbotGreetConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                // 判断lastCannotGreetTime不等于emptyTimestamp
                lambdaQueryWrapper.isNotNull(AmChatbotGreetConfig::getLastCannotGreetTime);
                lambdaQueryWrapper.ne(AmChatbotGreetConfig::getLastCannotGreetTime, emptyTimestamp);
                List<AmChatbotGreetConfig> amChatbotGreetConfigs = amChatbotGreetConfigService.list(lambdaQueryWrapper);
                //判断是否是昨天,如果是昨天则把时间置为空,如果不是昨天则不做处理
                for (AmChatbotGreetConfig amChatbotGreetConfig : amChatbotGreetConfigs) {
                    LocalDateTime lastCannotGreetTime = amChatbotGreetConfig.getLastCannotGreetTime();
                    LocalDate lastCannotGreetDate = lastCannotGreetTime.toLocalDate();
                    LocalDate now = LocalDate.now();
                    if (lastCannotGreetDate.isBefore(now)) {
                        //设置特定的表示空值的时间
                        amChatbotGreetConfig.setLastCannotGreetTime(emptyTimestamp);
                        amChatbotGreetConfigService.updateById(amChatbotGreetConfig);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

}
