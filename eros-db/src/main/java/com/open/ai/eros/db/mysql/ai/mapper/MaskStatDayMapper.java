package com.open.ai.eros.db.mysql.ai.mapper;

import com.open.ai.eros.db.mysql.ai.entity.MaskStatDay;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.ai.entity.MaskStatList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 面具消耗的日统计表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-12
 */
@Mapper
public interface MaskStatDayMapper extends BaseMapper<MaskStatDay> {

    @Select(" select * from mask_stat_day order by stats_day desc limit 1 ")
    MaskStatDay getLastMaskStatDay();


    @Select({
            " <script> " +
                    " select SUM(cost) as 'cost', SUM(record_count) as 'recordCount', DATE(create_time) as 'statsDay' , mask_id as 'maskId' " +
                    " from mask_stat_day " +
                    " where  " +
                    "  <if test=\" userId != null \"> " +
                    "  user_id  = #{userId}" +
                    "  </if> " +
                    " <if test=\"startTime != null\">" +
                    "    <![CDATA[ and create_time >= #{startTime} ]]>" +
                    "</if>" +
                    " GROUP BY mask_id " +
                    " ORDER BY create_time" +
                    " </script> "
    })
    List<MaskStatList> statMasksConsumeRecord(@Param("userId")Long userId, @Param("startTime") Date startTime);



    @Select(" select sum(record_count) from mask_stat_day  ")
    Long getLastMaskStatRecordCount();

}
