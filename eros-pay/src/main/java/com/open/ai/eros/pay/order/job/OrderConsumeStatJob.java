package com.open.ai.eros.pay.order.job;

import com.open.ai.eros.common.constants.BalanceUnitEnum;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.creator.convert.MaskStatConvert;
import com.open.ai.eros.db.mysql.ai.entity.MaskStatDay;
import com.open.ai.eros.db.mysql.ai.service.impl.MaskStatDayServiceImpl;
import com.open.ai.eros.db.mysql.ai.service.impl.UserAiConsumeRecordServiceImpl;
import com.open.ai.eros.db.mysql.creator.entity.Mask;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import com.open.ai.eros.db.mysql.pay.entity.OrderStatDay;
import com.open.ai.eros.db.mysql.pay.service.impl.OrderStatDayServiceImpl;
import com.open.ai.eros.pay.order.manager.OrderManager;
import com.open.ai.eros.pay.order.manager.OrderStatDayManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Component
@Slf4j
@EnableScheduling
public class OrderConsumeStatJob {



    @Autowired
    private OrderManager orderManager;

    @Autowired
    private OrderStatDayServiceImpl orderStatDayService;


    /**
     * 一天只需要成功执行前一天的数据
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 2)
    public void statUserAiConsumeRecord() {
        Lock lock = DistributedLockUtils.getLock("orderConsumeRecord", 30);
        if (lock.tryLock()) {
            try {
                //获取前一天的开始时间和结束时间
                Date beforeOneDayDate = DateUtils.plusDays(new Date(), -1);
                Date endTime = DateUtils.endOfDay(beforeOneDayDate);
                Date startTime = DateUtils.startOfDay(beforeOneDayDate);
                Integer page = 1;
                Integer pageSize = 30;
                OrderStatDay orderStatDay = new OrderStatDay();
                BigDecimal income =new BigDecimal(0);
                Long orderCount = 0L;
                OrderStatDay lastMaskStatDay = orderStatDayService.getLastMaskStatDay();
                if (lastMaskStatDay != null && Date.from(lastMaskStatDay.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()).after(endTime)) {
                    // 已经操作了
                    return;
                }
                while (true) {
                    List<Order> orders = orderManager.orderRecord(page++, pageSize, 2, startTime, endTime);
                    if (CollectionUtils.isEmpty(orders)) {
                        break;
                    }
                    for (Order order : orders) {
                        // 确保货币单位为DOLLAR
                        if (order.getUnit().equals(BalanceUnitEnum.DOLLAR.getUnit())) {
                            // 将订单价格与汇率相乘，并将结果累加到income上
                            // 注意：需要使用order.getPrice().multiply(order.getRate())
                            income = income.add(order.getPrice().multiply(order.getRate()));
                        }else {
                            income = income.add(order.getPrice());
                        }
                    }
                    orderCount++;
                    if (orders.size() < pageSize) {
                        break;
                    }
                }
                orderStatDay.setStatsDay(LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay());
                orderStatDay.setCost(income);
                orderStatDay.setRecordCount(orderCount);
                orderStatDay.setCreateTime(LocalDateTime.now());
                boolean save = orderStatDayService.save(orderStatDay);
                log.info("OrderConsumeStatJob count order info success time={},result={}",LocalDateTime.now(),save);
            } finally {
                lock.unlock();
            }
        }

    }

}
