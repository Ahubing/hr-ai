package com.open.ai.eros.db.mysql.pay.mapper;

import com.open.ai.eros.db.mysql.pay.entity.OrderStatDay;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单的日统计表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
public interface OrderStatDayMapper extends BaseMapper<OrderStatDay> {


    @Select(" select * from order_stat_day order by stats_day desc limit 1 ")
    OrderStatDay getLastOrderStatDay();

    @Select(" select sum(cost) as cost , sum(record_count)  as recordCount from order_stat_day  ")
    OrderStatDay getHistoryOrderStat();


    @Select({
            " <script> " +
                    " select *  " +
                    " from order_stat_day " +
                    " where  1 = 1 " +
                    " <if test=\"startTime != null\">" +
                    "    <![CDATA[ and create_time >= #{startTime} ]]>" +
                    "</if>" +
                    " <if test=\"endTime != null\">" +
                    "    <![CDATA[ and create_time <= #{endTime} ]]>" +
                    "</if>" +
                    " </script> "
    })
    List<OrderStatDay> getOrderStatByTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);



}
