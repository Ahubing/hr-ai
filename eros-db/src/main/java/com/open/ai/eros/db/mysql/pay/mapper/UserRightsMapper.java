package com.open.ai.eros.db.mysql.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.pay.entity.UserRights;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Mapper
public interface UserRightsMapper extends BaseMapper<UserRights> {


    @Select("select * from user_rights  where  user_id = #{userId} and status = #{status} order by status asc  ")
    List<UserRights> getUserRights(@Param("userId") Long userId,@Param("status") Integer status);


    @Select("select * from user_rights  where  user_id = #{userId} and type =#{type} order by effective_end_time desc limit 1  ")
    UserRights getLastUserRightsByType(@Param("userId") Long userId, @Param("type") String type);


    @Select({
            "<script> " +
                "select * from user_rights  where status = 1 and  " +
                    "   <![CDATA[  update_time >= #{startTime} ]]>   " +
                    "       and  <![CDATA[ update_time <= #{endTime} ]]> " +
                    "limit #{pageIndex} , #{pageSize}  " +
            "</script> "
    })
    List<UserRights> getActiveUserRights(@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("pageIndex") Integer pageIndex,@Param("pageSize") Integer pageSize);

//  <![CDATA[ and create_time <= #{endTime} ]]>
    @Select({
              "<script> " +
                    "select * from  user_rights where  effective_end_time &lt; #{time}  and status = 1  limit 10 " +
               "</script> "
            })
    List<UserRights> getInactive(@Param("time") Date time);



}
