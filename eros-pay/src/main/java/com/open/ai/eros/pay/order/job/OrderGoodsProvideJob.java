package com.open.ai.eros.pay.order.job;

/**
 * @类名：OrderGoodsProvideJob
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/4 19:20
 */

import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.pay.order.manager.OrderManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 订单 支持成功的 商品发放
 */
@Component
@Slf4j
@EnableScheduling
public class OrderGoodsProvideJob {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private OrderManager orderManager;


    @Scheduled(fixedDelay = 2000  )
    public void orderGoodsProvide(){

        Set<String> orderIds = redisClient.spop(CommonConstant.ORDER_GOODS_PROVIDE_SET, 10);
        if(CollectionUtils.isEmpty(orderIds)){
            return;
        }
        for (String orderId : orderIds) {
            try {
                orderManager.updateOrderStatusToDoneAndProvideGoodsToUser(Long.parseLong(orderId));
            }catch (Exception e){
                log.error("orderGoodsProvide error orderId={}",orderId,e);
            }
        }

    }







}
