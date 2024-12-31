package com.open.ai.eros.creator.job;

import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.creator.convert.MaskStatConvert;
import com.open.ai.eros.db.mysql.ai.entity.MaskStatDay;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecordStatVo;
import com.open.ai.eros.db.mysql.ai.service.impl.MaskStatDayServiceImpl;
import com.open.ai.eros.db.mysql.ai.service.impl.UserAiConsumeRecordServiceImpl;
import com.open.ai.eros.db.mysql.creator.entity.Mask;
import com.open.ai.eros.db.mysql.creator.service.impl.MaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Component
@Slf4j
@EnableScheduling
public class MaskConsumeStatJob {


    @Autowired
    private UserAiConsumeRecordServiceImpl userAiConsumeRecordService;

    @Autowired
    private MaskStatDayServiceImpl maskStatDayService;

    @Autowired
    private MaskServiceImpl maskService;


    /**
     * 一天只需要成功执行前一天的数据
     */
    //@Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 2)
    public void statUserAiConsumeRecord() {

        Lock lock = DistributedLockUtils.getLock("statUserAiConsumeRecord", 30);
        if (lock.tryLock()) {
            try {
                //获取前一天的开始时间和结束时间
                Date beforeOneDayDate = DateUtils.plusDays(new Date(), -1);
                Date endTime = DateUtils.endOfDay(beforeOneDayDate);
                Date startTime = DateUtils.startOfDay(beforeOneDayDate);
                MaskStatDay lastMaskStatDay = maskStatDayService.getLastMaskStatDay();
                if (lastMaskStatDay != null && Date.from(lastMaskStatDay.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()).after(startTime)) {
                    // 已经操作了
                    return;
                }
                Integer page = 1;
                Integer pageSize = 30;
                while (true) {
                    List<UserAiConsumeRecordStatVo> userAiConsumeRecordStatVos = userAiConsumeRecordService.statConsumeRecord(startTime, endTime, page++, pageSize);
                    if (CollectionUtils.isEmpty(userAiConsumeRecordStatVos)) {
                        break;
                    }
                    List<MaskStatDay> maskStatDays = userAiConsumeRecordStatVos.stream().map(e -> {
                        MaskStatDay maskStatDay = MaskStatConvert.I.convertMaskStatDay(e);

                        Mask mask = maskService.getById(e.getMaskId());
                        if (mask != null) {
                            maskStatDay.setUserId(mask.getUserId());
                        }
                        maskStatDay.setCreateTime(LocalDateTime.now());
                        return maskStatDay;
                    }).collect(Collectors.toList());
                    boolean batchSaveResult = maskStatDayService.batchSave(maskStatDays);
                    log.info("statUserAiConsumeRecord batchSaveResult={}", batchSaveResult);
                    if (userAiConsumeRecordStatVos.size() < pageSize) {
                        break;
                    }
                }
            } finally {
                lock.unlock();
            }
        }

    }

}
