//package com.open.ai.eros.user.job;
//
//
//import com.open.ai.eros.common.util.DateUtils;
//import com.open.ai.eros.common.util.DistributedLockUtils;
//import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatDay;
//import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatVo;
//import com.open.ai.eros.db.mysql.user.service.impl.UserBalanceRecordServiceImpl;
//import com.open.ai.eros.db.mysql.user.service.impl.UserIncomeStatDayServiceImpl;
//import com.open.ai.eros.user.constants.UserBalanceRecordEnum;
//import com.open.ai.eros.user.convert.UserIncomeStatConvert;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.locks.Lock;
//import java.util.stream.Collectors;
//
//
///**
// * 用户收益统计
// */
//@Component
//@Slf4j
//@EnableScheduling
//public class UserIncomeStatJob {
//
//
//    @Autowired
//    private UserIncomeStatDayServiceImpl userIncomeStatDayService;
//
//
//    /**
//     * 按天统计用户的收益
//     * 1. 邀请新用户收益
//     * 2. 面具收益
//     */
//    @Transactional
//    @Scheduled(cron = "0 0 1 * * ?")
//    //@Scheduled(fixedDelay = 1000)
//    public void statUserIncome() {
//
//        Lock lock = DistributedLockUtils.getLock("statUserIncome", 300);
//        if (lock.tryLock()) {
//            try {
//                //获取前一天的开始时间和结束时间
//                Date beforeOneDayDate = DateUtils.plusDays(new Date(), -1);
//                Date endTime = DateUtils.endOfDay(beforeOneDayDate);
//                Date startTime = DateUtils.startOfDay(beforeOneDayDate);
//                UserIncomeStatDay lastMaskStatDay = userIncomeStatDayService.getLastStatDay();
//                if (lastMaskStatDay != null && Date.from(lastMaskStatDay.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()).after(startTime)) {
//                    // 已经操作了
//                    return;
//                }
//
//                List<UserBalanceRecordEnum> userBalanceRecordEnums = Arrays.asList(UserBalanceRecordEnum.INVITATION_NEW_USER_BALANCE, UserBalanceRecordEnum.MASK_CHAT_BALANCE);
//
//                for (UserBalanceRecordEnum userBalanceRecordEnum : userBalanceRecordEnums) {
//                    Integer page = 1;
//                    Integer pageSize = 30;
//                    while (true) {
//                        List<UserIncomeStatVo> userIncomeStatVos = userBalanceRecordService.statUserIncome(userBalanceRecordEnum.getType(), startTime, endTime, page++, pageSize);
//                        if (CollectionUtils.isEmpty(userIncomeStatVos)) {
//                            break;
//                        }
//
//                        List<UserIncomeStatDay> incomeStatDays = userIncomeStatVos.stream().map(e -> {
//
//                            UserIncomeStatDay userIncomeStatDay = UserIncomeStatConvert.I.convertUserIncomeStat(e);
//                            userIncomeStatDay.setCreateTime(LocalDateTime.now());
//                            userIncomeStatDay.setType(userBalanceRecordEnum.getType());
//                            return userIncomeStatDay;
//                        }).collect(Collectors.toList());
//                        boolean saveBatch = userIncomeStatDayService.saveBatch(incomeStatDays);
//                        log.info("statUserIncome saveBatch={},size={}", saveBatch, incomeStatDays.size());
//                        if (userIncomeStatVos.size() < pageSize) {
//                            break;
//                        }
//                    }
//                }
//
//            } finally {
//                lock.unlock();
//            }
//        }
//
//    }
//
//    @Autowired
//    private UserBalanceRecordServiceImpl userBalanceRecordService;
//
//
//
//}
