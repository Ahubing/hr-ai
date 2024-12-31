package com.open.ai.eros.db.mysql.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import com.open.ai.eros.db.mysql.pay.mapper.OrderMapper;
import com.open.ai.eros.db.mysql.pay.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-25
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {


    @Autowired
    private RedisClient redisClient;


    /**
     * 获取用户待支付的订单
     *
     * @param userId
     * @param goodId
     * @return
     */
    public Order getOrderByPayStatus(Long userId,Long goodId){
        return this.getBaseMapper().getOrderByPayStatus(userId,goodId);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatusToPaySuccess(Long id){
        int result = this.getBaseMapper().updateOrderStatusToPaySuccess(id,OrderStatusEnum.PAY_SUCCESS.getStatus());
        log.info("updateOrderStatus id={},result={}",id,result);
        if(result>0){
            redisClient.sadd(CommonConstant.ORDER_GOODS_PROVIDE_SET,String.valueOf(id));
            return true;
        }
        return false;
    }


    public void updateOrderStatusToPayFail(Long id){
        int result = this.getBaseMapper().updateOrderStatusToPayFail(id,OrderStatusEnum.PAY_FAIL.getStatus());
        log.info("updateOrderStatusToPayFail id={},result={}",id,result);
    }


    /**
     * 根据流水号查询 订单信息
     *
     * @param code
     * @return
     */
    public Order getOrderByCode(Long userId,String code){
        return this.getBaseMapper().getOrderByCode(userId,code);
    }



    public Order getOrderById(Long userId, Long id){
        return this.getBaseMapper().getOrderById(userId,id);
    }

    /**
     * 获取今日订单
     * @return
     */
    public List<Order> getTodayOrder(){
        Date startTime = DateUtils.startOfDay(new Date());
        return this.getBaseMapper().getTodayOrders(startTime);
    }


}
