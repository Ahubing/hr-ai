package com.open.ai.eros.db.mysql.pay.service.impl;

import com.open.ai.eros.db.mysql.pay.entity.OrderStatDay;
import com.open.ai.eros.db.mysql.pay.mapper.OrderStatDayMapper;
import com.open.ai.eros.db.mysql.pay.service.OrderStatDayService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单的日统计表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@Service
public class OrderStatDayServiceImpl extends ServiceImpl<OrderStatDayMapper, OrderStatDay> implements OrderStatDayService {

    /**
     * 获取最新的订单统计
     *
     * @return
     */
    public OrderStatDay getLastMaskStatDay(){
        return this.baseMapper.getLastOrderStatDay();
    }


    /**
     * 获取历史的订单数据
     *
     * @return
     */
    public OrderStatDay getHistoryOrderStat(){
        return this.baseMapper.getHistoryOrderStat();
    }

    /**
     * 根据时间查询出订单日统计表
     * @param startTime
     * @return
     */
    public List<OrderStatDay> getOrderStatByTime(Date startTime,Date endTime) {
        return this.getBaseMapper().getOrderStatByTime(startTime,endTime);
    }
}
