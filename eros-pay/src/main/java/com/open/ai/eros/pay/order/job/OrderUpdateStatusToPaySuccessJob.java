package com.open.ai.eros.pay.order.job;

import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import com.open.ai.eros.db.mysql.pay.service.impl.OrderServiceImpl;
import com.open.ai.eros.pay.order.manager.OrderManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @类名：OrderUpdateStatusJob
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/29 15:27
 */


/**
 * 查询外部订单信息来更新订单状态
 */
@Component
@Slf4j
@EnableScheduling
public class OrderUpdateStatusToPaySuccessJob {


    @Autowired
    private OrderManager orderManager;

    @Autowired
    private OrderServiceImpl orderService;


    /**
     * 扫描 支付成功，但是未收到回调的订单状态
     */
    @Async("OrderExecutor")
    @Scheduled(fixedDelay = 10000 )
    public void updateOrderStatusToSuccess(){

        long currentTimeMillis = System.currentTimeMillis();
        // 最近三十分钟的订单
        long timeOutTime = currentTimeMillis - 30 * 60 * 1000;
        Date startTime = new Date(timeOutTime);
        // 近两分钟
        Date endTime = new Date(currentTimeMillis - 60 * 2000);
        List<Order> awaitOrders = orderService.getBaseMapper().getAwaitOrder(startTime,endTime, OrderStatusEnum.WAIT_PAY.getStatus(), 100);
        if(CollectionUtils.isEmpty(awaitOrders)){
            return;
        }
        for (Order awaitOrder : awaitOrders) {
            orderManager.updateOrderStatusToPaySuccess(awaitOrder);
        }

    }







}
