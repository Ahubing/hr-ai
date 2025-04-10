package com.open.ai.eros.pay.order.job;

import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.pay.order.manager.OrderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @类名：OrderTimeOutJob
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/29 15:03
 */

@Component
@Slf4j
@EnableScheduling
public class OrderTimeOutJob {


    @Autowired
    private OrderManager orderManager;


    /**
     * 更新超时订单数
     */
    @Scheduled(fixedDelay = 1000 * 10 )
    public void updateTimeOutOrder(){
        long currentTimeMillis = System.currentTimeMillis();
        // 三十分钟之前的订单
        long timeOutTime = currentTimeMillis - 30 * 60 * 1000;
        Date date = new Date(timeOutTime);
        Date startTime = DateUtils.startOfDay(date);
        boolean timeOutOrder = orderManager.haveTimeOutOrder(startTime,date);
        if(!timeOutOrder){
            return;
        }
        orderManager.updateTimeOutOrder(startTime,date);
    }



}
