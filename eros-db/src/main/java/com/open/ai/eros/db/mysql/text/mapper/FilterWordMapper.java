package com.open.ai.eros.db.mysql.text.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.text.entity.FilterWordInfo;
import com.open.ai.eros.db.mysql.text.entity.FilterWordInfoVo;
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
 * @author lixin
 * @since 2023-05-17
 */
@Mapper
public interface FilterWordMapper extends BaseMapper<FilterWordInfo> {


    @Select("select id , word_content , type , risk_type , risk_level , channel_str  , language, reply_id  from filter_word_info  where reply_id = #{replyId} and create_user_id = #{userId}  and  status = 1 and type = 5 ")
    List<FilterWordInfo> getByReplyId(@Param("replyId") Long replyId,@Param("userId") Long userId);

    @Update("delete from filter_word_info where reply_id = #{replyId} and create_user_id = #{userId} ")
    int deleteReplyId(@Param("replyId") Long replyId,@Param("userId") Long userId);


    @Select("select id , word_content , type , risk_type , risk_level , language  from filter_word_info  where status = 1 and type = 4 ")
    List<FilterWordInfoVo> getAllWhite();

    @Select("SELECT max(create_time) from filter_word_info ")
    Date getMaxCreateTime();

    @Select("SELECT max(update_time) from filter_word_info ")
    Date getMaxUpdateTime();

    @Select("select count(1) from filter_word_info where status =1 ")
    int getFilterWordCount();

    @Select("select count(1) from filter_word_channel_info where status =1  ")
    int getChannelCount();

    @Select("select id , word_content , type , risk_type , risk_level , channel_str  , language, reply_id  from filter_word_info   where status = 1 order by create_time desc limit #{offsetIndex},#{pageSize} ")
    List<FilterWordInfoVo> getFilterWord(@Param("offsetIndex") Integer offsetIndex, @Param("pageSize") Integer pageSize);

    /**
     * 获取修改的敏感词信息
     * @param offsetIndex
     * @param pageSize
     * @return
     */
    @Select("select id , word_content ,type , risk_type , risk_level , status , channel_str , update_time , language from filter_word_info where " +
            "  update_time>=#{startTime} and  update_time <= #{endTime} order by update_time desc " +
            " limit #{offsetIndex},#{pageSize} " )
    List<FilterWordInfoVo> getUpdateFilterWords(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("offsetIndex") Integer offsetIndex, @Param("pageSize") Integer pageSize);


    /**
     *   @Select("select id , word_content ,type , risk_type , risk_level  from filter_word_info  where status = 1 " +
     *             " and ( create_time>=#{startTime} and  create_time <= #{endTime} ) " +
     *             " limit #{offsetIndex},#{pageSize} " )
     */
    /**
     * 获取新增的敏感词信息
     * @param offsetIndex
     * @param pageSize
     * @return
     */
    @Select("select id , word_content ,type , risk_type , risk_level , status , channel_str , create_time , language  from filter_word_info  where status = 1 " +
            " and ( create_time>=#{startTime} and  create_time <= #{endTime} ) " +
            " order by create_time desc limit #{offsetIndex},#{pageSize}  " )
    List<FilterWordInfoVo> getAddFilterWords(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("offsetIndex") Integer offsetIndex, @Param("pageSize") Integer pageSize);

}
