package com.open.ai.eros.db.mysql.social.mapper;

import com.open.ai.eros.db.mysql.social.entity.PushMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@Mapper
public interface PushMessageMapper extends BaseMapper<PushMessage> {


    @Select("select * from push_message where user_id = #{userId} and `status` = 'unRead' and push_to = #{source} order by create_time desc limit 1  ")
    PushMessage getUserLastMessage(@Param("userId") Long userId,@Param("source") String source);

}
