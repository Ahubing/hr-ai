package com.open.ai.eros.db.mysql.user.mapper;

import com.open.ai.eros.db.mysql.user.entity.UserFollowMask;
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
 * @since 2024-08-16
 */
@Mapper
public interface UserFollowMaskMapper extends BaseMapper<UserFollowMask> {


    @Select(" select  * from user_follow_mask where user_id = #{userId} and  mask_id = #{maskId} limit 1 ")
    UserFollowMask getUserFollowMask(@Param("userId") Long userId, @Param("maskId") Long maskId);


}
