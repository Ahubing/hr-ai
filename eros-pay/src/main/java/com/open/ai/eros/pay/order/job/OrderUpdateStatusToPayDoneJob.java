package com.open.ai.eros.pay.order.job;

import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import com.open.ai.eros.db.mysql.pay.service.impl.OrderServiceImpl;
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
 * 订单状态为支付成功的兜底
 *
 */
@Component
@Slf4j
@EnableScheduling
public class OrderUpdateStatusToPayDoneJob {


    @Autowired
    private OrderServiceImpl orderService;


    @Autowired
    private RedisClient redisClient;


    /**
     * 扫描 支付成功，但是未成功 发放商品+ 订单状态
     */
    @Async("OrderExecutor")
    @Scheduled(fixedDelay = 20000 )
    public void updateOrderStatusToSuccess(){

        log.info("开始扫描支付成功订单");
        long currentTimeMillis = System.currentTimeMillis();
        // 最近三十分钟的订单 到 当前60秒之前的已支付完成的订单
        long timeOutTime = currentTimeMillis - 30 * 60 * 1000;
        Date startTime = new Date(timeOutTime);

        Date endTime = new Date(currentTimeMillis - 60 * 1000);

        List<Order> awaitOrders = orderService.getBaseMapper().getPaySuccessOrder(startTime,endTime, OrderStatusEnum.PAY_SUCCESS.getStatus(), 100);
        if(CollectionUtils.isEmpty(awaitOrders)){
            return;
        }
        for (Order awaitOrder : awaitOrders) {
            log.info("扫描支付成功订单 id={}",awaitOrder.getId());
            redisClient.sadd(CommonConstant.ORDER_GOODS_PROVIDE_SET,String.valueOf(awaitOrder.getId()));
        }

    }







}
