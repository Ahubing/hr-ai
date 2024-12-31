package com.open.ai.eros.db.mysql.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.user.entity.UserBalanceRecord;
import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户余额的记录表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-14
 */

@Mapper
public interface UserBalanceRecordMapper extends BaseMapper<UserBalanceRecord> {



    @Select({
            " <script> " +
            "select user_id , sum(balance) as 'income' , DATE(create_time) as 'statDay'   from user_balance_record  where " +
                    "   <![CDATA[  create_time >= #{startTime} ]]>   " +
                    "       and  <![CDATA[ create_time <= #{endTime} ]]> " +
                    " and type = #{type}  GROUP BY user_id, DATE(create_time)  limit #{pageIndex} , #{pageSize} " +
            " </script> "
    })
    List<UserIncomeStatVo> statUserIncome(
            @Param("type") String type,
            @Param("startTime") Date startTime, @Param("endTime") Date endTime,
            @Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize);


    @Select({
            " <script> " +
                    " select sum(balance) as 'income' , DATE(create_time) as 'statDay'  " +
                    " from user_balance_record " +
                    " where " +
                    "  <if test=\"userId != null \"> " +
                    "       user_id = #{userId} and  " +
                    "  </if>" +
                    "  type in ( " +
                    " <foreach item='type' index='index' collection='types' separator=','> " +
                    " #{type}" +
                    "   and dividend = 2  " +
                    " </foreach> )   " +
                    "   <![CDATA[ and create_time >= #{startTime} ]]>   " +
                    " and  <![CDATA[ and create_time <= #{endTime} ]]>" +
                    " GROUP BY DATE(create_time) limit 1 " +
                    " </script> "
    })
    UserIncomeStatVo statUserTodayIncome(
            @Param("userId") Long userId, @Param("types") List<String> types,
            @Param("startTime") Date startTime, @Param("endTime") Date endTime);


}
