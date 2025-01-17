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
import java.util.*;
import java.util.concurrent.locks.Lock;

@Component
@Slf4j
@EnableScheduling
public class AmZpLocalAccountJob {


    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;
    private static final String GREET_MESSAGE = "你好";


    /**
     * 处理定时任务
     * 下面跟着php逻辑实现, 后续改造
     */

    // 20秒检测一次
    @Scheduled(cron = "0/20 * * * * ?")
    public void offerLineAccount() {
        Lock lock = DistributedLockUtils.getLock("offline_account", 10);
        if (lock.tryLock()) {
            try {
                List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.list();
                for (AmZpLocalAccouts localAccout : localAccouts) {
                    if ("active".equals(localAccout.getState())) {
                        // 规定超过25秒就认定下线
                        if (Objects.nonNull(localAccout.getUpdateTime()) && System.currentTimeMillis() - DateUtils.convertLocalDateTimeToTimestamp(localAccout.getUpdateTime()) > 25 * 1000) {
                            localAccout.setState("inactive");
                            amZpLocalAccoutsService.updateById(localAccout);
                            log.info("账号:{} 下线", localAccout.getId());
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }


}
