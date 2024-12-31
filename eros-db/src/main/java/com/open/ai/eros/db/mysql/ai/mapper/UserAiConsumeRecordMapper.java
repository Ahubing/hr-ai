package com.open.ai.eros.db.mysql.ai.mapper;

import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecordStatVo;
import com.open.ai.eros.db.mysql.ai.entity.UserAiMasksRecordStatVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户的ai消费记录 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-11
 */
@Mapper
public interface UserAiConsumeRecordMapper extends BaseMapper<UserAiConsumeRecord> {


    @Select({
            " <script> " +
            " select mask_id , sum(cost) as 'cost' , DATE(create_time) as 'statsDay' ,count(1) as 'recordCount'  from user_ai_consume_record  where " +
                    "   <![CDATA[  create_time >= #{startTime} ]]>   " +
                    "       and  <![CDATA[ create_time <= #{endTime} ]]> " +
                    " and  mask_id is not null and dividend = 2  GROUP BY mask_id,DATE(create_time)  limit #{pageIndex} , #{pageSize} " +
            " </script> "
    })
    List<UserAiConsumeRecordStatVo> statConsumeRecord(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize);


    @Select({
            " <script> " +
            " select mask_id , sum(cost) as 'cost' , DATE(create_time) as 'statsDay' ,count(1) as 'recordCount'  from user_ai_consume_record  where " +
                    "   <![CDATA[  create_time >= #{startTime} ]]>   " +
                    "       and   user_id = #{userId}  " +
                    " and  mask_id is not null and dividend = 2  GROUP BY mask_id,DATE(create_time) order by cost desc" +
            " </script> "
    })
    List<UserAiConsumeRecordStatVo> todayStatConsumeRecordByUserId(@Param("startTime") Date startTime, @Param("userId") Long userId);


    @Select({
            " <script> " +
                    " select  sum(cost) as 'cost' ,  DATE(create_time) as 'statsDay' , count(1) as 'recordCount' " +
                    " from user_ai_consume_record " +
                    " where  " +
                    "  <if test=\" maskIds != null and maskIds.size > 0  \"> " +
                    "  mask_id in ( " +
                    " <foreach item='maskId' index='index' collection='maskIds' separator=','> " +
                    " #{maskId}" +
                    " </foreach> ) and   " +
                    "  </if> " +
                    "   dividend = 2  " +
                    "   <![CDATA[ and create_time >= #{startTime} ]]>   " +
            "           <![CDATA[ and create_time <= #{endTime} ]]>" +
                    " GROUP BY DATE(create_time) " +
                    " </script> "
    })
    UserAiConsumeRecordStatVo statTodayConsumeRecord(@Param("maskIds") List<Long> maskIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Select({
            " <script> " +
                    " select  sum(cost) as 'cost', DATE(create_time) as 'statsDay', count(1) as 'recordCount' " +
                    " from user_ai_consume_record " +
                    " where  " +
                    "  <if test=\"maskIds != null and maskIds.size > 0\"> " +
                    "  mask_id in (" +
                    " <foreach item='maskId' index='index' collection='maskIds' separator=','> " +
                    " #{maskId}" +
                    " </foreach>) and " +
                    "  </if> " +
                    " dividend = 2  " +
                    " <![CDATA[ and create_time >= #{startTime} ]]> " +
                    " <![CDATA[ and create_time <= #{endTime} ]]>" +
                    " GROUP BY DATE(create_time) " +
                    " order by DATE(create_time) " + // 确保结果按日期排序
                    " </script> "
    })
    List<UserAiConsumeRecordStatVo> statWeekConsumeRecord(@Param("maskIds") List<Long> maskIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);



    @Select({
            " <script> " +
                    " select  sum(cost) as 'cost', count(distinct user_id ) as 'usePeopleCount' , count(1) as 'recordCount' " +
                    " from user_ai_consume_record " +
                    " where  " +
                    "  <if test=\" maskIds != null and maskIds.size > 0  \"> " +
                    "  mask_id in ( " +
                    " <foreach item='maskId' index='index' collection='maskIds' separator=','> " +
                    " #{maskId}" +
                    " </foreach> ) and   " +
                    "  </if> " +
                    "   dividend = 2  " +
                    " <if test=\"startTime != null\">" +
                    "    <![CDATA[ and create_time >= #{startTime} ]]>" +
                    "</if>" +
                    " </script> "
    })
    UserAiMasksRecordStatVo statMasksConsumeRecord(@Param("maskIds") List<Long> maskIds, @Param("startTime") Date startTime);




    @Select({
            " <script> " +
                    " select  count(*) " +
                    " from user_ai_consume_record " +
                    " where  1= 1 " +
                    " <if test=\"startTime != null\">" +
                    "    <![CDATA[ and create_time >= #{startTime} ]]>" +
                    "</if>" +
                    " </script> "
    })
    Long statMasksRecordToday(@Param("startTime") Date startTime);

}
