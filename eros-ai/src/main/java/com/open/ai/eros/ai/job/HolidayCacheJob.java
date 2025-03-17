package com.open.ai.eros.ai.job;

import cn.hutool.http.HttpUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.common.util.TimeUtil;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Component
@Slf4j
@EnableScheduling
public class HolidayCacheJob {

    private static final String HOLIDAY_LOCK = "holidayLock";

    @Resource
    private JedisClientImpl jedisClient;

    @EventListener(ApplicationReadyEvent.class)
    public void initHoliday() {

        Lock lock = DistributedLockUtils.getLock(HOLIDAY_LOCK, 20, TimeUnit.SECONDS);
        try {
            if (lock.tryLock()) {
                log.info("HolidayCacheJob init start---");
                flushHolidayCache();
                log.info("HolidayCacheJob init end---");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("HolidayCacheJob init error---",e);
        }finally {
            lock.unlock();
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void flushHoliday(){
        Lock lock = DistributedLockUtils.getLock(HOLIDAY_LOCK, 20, TimeUnit.SECONDS);
        try {
            if (lock.tryLock()) {
                log.info("HolidayCacheJob flush start---");
                flushHolidayCache();
                log.info("HolidayCacheJob flush end---");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("HolidayCacheJob flush error---",e);
        }finally {
            lock.unlock();
        }
    }

    // 初始化未来一个月holiday缓存
    private void flushHolidayCache() {
        String jsonString = HttpUtil.get(CommonConstant.HOLIDAY_API_URL);
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject holidays = json.getAsJsonObject("holidays");
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusMonths(1);
        for (LocalDate date = now; date.isBefore(endDate); date = date.plusDays(1)) {
            String dateStr = CommonConstant.HOLIDAY_PREFIX + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            if(holidays.keySet().contains(dateStr)){
                jedisClient.set(dateStr, "1", CommonConstant.HOLIDAY_EXPIRE_TIME);
            }else {
                jedisClient.set(dateStr, "0", CommonConstant.HOLIDAY_EXPIRE_TIME);
            }
        }
    }
}
