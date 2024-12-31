package com.open.ai.eros.db.mysql.ai.mapper;

import com.open.ai.eros.db.mysql.ai.entity.ChatConversation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-04
 */

@Mapper
public interface ChatConversationMapper extends BaseMapper<ChatConversation> {


    @Select(" select * from  chat_conversation where  user_id = #{userId} order by create_time desc limit 100 ")
    List<ChatConversation> getList(@Param("userId")Long userId);



    @Delete(" delete from chat_conversation where id = #{chatConversationId} and  user_id = #{userId} limit 1  ")
    int deleteChatConversation(@Param("userId")Long userId,@Param("chatConversationId") String chatConversationId);


    @Update(" update  chat_conversation set name = #{name} where id = #{chatConversationId} and  user_id = #{userId} limit 1  ")
    int updateChatConversation(@Param("userId")Long userId,@Param("chatConversationId") String chatConversationId,@Param("name") String name);



    @Select("select * from  chat_conversation where  user_id = #{userId} and mask_id = #{maskId} order by create_time desc limit 1 ")
    ChatConversation getChatConversationByUserAndMaskId(@Param("userId") Long userId,@Param("maskId") Long maskId);




}
