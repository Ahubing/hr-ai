package com.open.ai.eros.db.mysql.creator.mapper;

import com.open.ai.eros.db.mysql.creator.entity.Mask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-13
 */
@Mapper
public interface MaskMapper extends BaseMapper<Mask> {


    @Select("select id , name, avatar from mask where  status = 1 ")
    List<Mask> getSimpleMask();

    @Update("update mask set text_match_channel = null where text_match_channel = #{channelId} and user_id = #{userId}  ")
    int updateMaskChannelNull(@Param("channelId") Long channelId,@Param("userId") Long userId);


    @Select("select id from  mask where user_id = #{userId} ")
    List<Long> getMaskId(@Param("userId") Long userId);

    @Update("update mask set collect_num = collect_num + 1 where id =#{maskId} limit 1 ")
    int updateMaskCollectNum(@Param("maskId") Long maskId);



    @Update(" update mask set heat = heat + 1 where id =#{maskId}  limit 1 ")
    int updateMaskHeat(@Param("maskId") Long maskId);


    @Select("select count(*) from  mask  ")
    Long getMaskSum();

}
