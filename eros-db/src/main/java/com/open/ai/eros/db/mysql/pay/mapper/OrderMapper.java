package com.open.ai.eros.db.mysql.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-25
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {


    @Select("select  * from t_order where user_id = #{userId} and  id = #{id} limit 1 ")
    Order getOrderById(@Param("userId") Long userId, @Param("id") Long id);


    @Select("select  count(1) from t_order where  create_time >= #{startTime} and  create_time <= #{endTime}  and  status = #{status} limit 1  ")
    int haveTimeOutOrder(@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("status") Integer status);


    @Update(" update t_order  set status = #{updateStatus} , update_time =#{updateTime} where create_time >= #{startTime} and  create_time <= #{endTime}  and  status = #{status} limit 100  ")
    int updateTimeOutOrder(@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("status") Integer status,@Param("updateStatus") Integer updateStatus,@Param("updateTime") Date updateTime);



    @Select("select  *  from t_order where   create_time >= #{startTime} and  create_time <= #{endTime}   and  status = #{status} limit  #{pageSize}  ")
    List<Order> getAwaitOrder(@Param("startTime") Date startTime,@Param("endTime") Date endTime, @Param("status") Integer status, @Param("pageSize") Integer pageSize);


    @Select("select  *  from t_order where  create_time >= #{startTime} and  create_time <= #{endTime}   and  status = #{status} limit  #{pageSize}  ")
    List<Order> getPaySuccessOrder(@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("status") Integer status, @Param("pageSize") Integer pageSize);


    /**
     * 更新订单状态从 待支付变成 支付成功
     * @param id
     * @param status
     * @return
     */
    @Update("update t_order set status = #{status}   where id = #{id} and status = 1 limit 1 ")
    int updateOrderStatusToPaySuccess(@Param("id") Long id,@Param("status") Integer status);



    @Update("update t_order set status = #{status} where id = #{id} and status = 1 limit 1 ")
    int updateOrderStatusToPayFail(@Param("id") Long id,@Param("status") Integer status);




    /**
     * 更新订单状态从  支付成功 变成 已完成
     * @param id
     * @param status
     * @return
     */
    @Update("update t_order set status = #{status} where id = #{id} and status = 2 limit 1 ")
    int updateOrderStatusToDone(@Param("id") Long id,@Param("status") Integer status);


    @Select(" select * from t_order where code = #{code}  and user_id = #{userId} limit 1 ")
    Order getOrderByCode(@Param("userId") Long userId,@Param("code") String code);


    @Select(" select * from t_order where goods_id = #{goodId}  and user_id = #{userId} and status = 1  limit 1 ")
    Order getOrderByPayStatus(@Param("userId")Long userId,@Param("goodId")Long goodId);



    /**
     * 查询今天全部已完成的订单统计
     * @param startTime
     * @return
     */
    @Select(
            "<script> "+
                    "SELECT * FROM t_order  where status = 5" +
                    "         <![CDATA[ and create_time >= #{startTime} ]]> "+
                    "</script> "
    )
    List<Order> getTodayOrders(@Param("startTime") Date startTime);


}
