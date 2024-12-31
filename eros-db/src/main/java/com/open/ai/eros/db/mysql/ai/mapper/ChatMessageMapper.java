package com.open.ai.eros.db.mysql.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.ai.entity.ChatMessage;
import com.open.ai.eros.db.mysql.ai.entity.GetChatMessageVo;
import com.open.ai.eros.db.mysql.ai.entity.GetNewChatMessageVo;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-11
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {



    @Update(" update  chat_message set status = 2  where  conversation_id = #{conversionId} and  id > #{id}  and  status = 1 ")
    int backMessage(@Param("conversionId") String conversionId, @Param("id") Long id);



    @Update(" update  chat_message set read_status = 1  where  conversation_id = #{conversionId} and  user_id = #{userId}  and  read_status = 2 and  create_time >= #{startTime} and  create_time <= #{endTime}   ")
    int updateUnReadChatMessage(@Param("conversionId") String conversionId, @Param("userId") Long userId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    @Select(" select count(1) from chat_message where  conversation_id = #{conversionId} and  user_id = #{userId}  and  read_status = 2 and  create_time >= #{startTime} and  create_time <= #{endTime}   ")
    int getUnReadChatMessage(@Param("conversionId") String conversionId, @Param("userId") Long userId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    @Select(" select * from chat_message where  conversation_id = #{conversionId} and  user_id = #{userId}  and status = 1 order by id desc  limit 1  ")
    ChatMessage getNewMessage(@Param("conversionId") String conversionId, @Param("userId") Long userId);


    @Select({
            "<script> "+
            " select * from chat_message where  " +
                    " ( conversation_id = #{getChatMessageVo.conversionId}  " +
                    "  <if test=\"getChatMessageVo.shareConversionId != null  \"> "+
                    "    or ( conversation_id = #{getChatMessageVo.shareConversionId}  " +
                    " and <![CDATA[ id >=  #{getChatMessageVo.startChatId} ]]>   " +
                    " and  <![CDATA[ #{getChatMessageVo.endChatId} <= id ]]>   " +
                    " )  "+
                    "  </if> " +
                    " )  and  user_id = #{getChatMessageVo.userId}  and status = 1 order by create_time desc  limit #{getChatMessageVo.pageIndex} , #{getChatMessageVo.pageSize}  " +
            "</script> "
    })
    List<ChatMessage> getChatMessage(@Param("getChatMessageVo") GetChatMessageVo getChatMessageVo);



    @Select({
            "<script> "+
                    " select * from chat_message where  " +
                    " ( conversation_id = #{getNewChatMessageVo.conversionId}  " +
                    "  <if test=\"getNewChatMessageVo.shareConversionId != null  \"> "+
                    "    or ( conversation_id = #{getNewChatMessageVo.shareConversionId}  " +
                    " and <![CDATA[ id >=  #{getNewChatMessageVo.startChatId} ]]>   " +
                    " and  <![CDATA[ #{getNewChatMessageVo.endChatId} <= id ]]>   " +
                    " )  "+
                    "  </if> " +
                    " )  and  user_id = #{getNewChatMessageVo.userId}  and  <![CDATA[ id <=  #{getNewChatMessageVo.chatId} ]]>  and status = 1 order by id desc  limit  #{getNewChatMessageVo.pageSize}  " +
                    " </script> "
    })
    List<ChatMessage> getNewChatMessage(@Param("getNewChatMessageVo") GetNewChatMessageVo getNewChatMessageVo);

    //
    //
    //
    //@Select(" select * from chat_message where  conversation_id = #{conversionId} and  user_id = #{userId} and id <= #{chatId} and status = 1 order by id desc  limit #{pageSize}  ")
    //List<ChatMessage> getNewChatMessage(@Param("conversionId") String conversionId,
    //                                    @Param("userId") Long userId ,
    //                                    @Param("roles") List<String> roles,
    //                                    @Param("chatId") Long chatId,
    //                                    @Param("pageSize") Integer pageSize);
    //


    @Delete(" delete from chat_message where id = #{id} and  user_id = #{userId} limit 1   ")
    int deleteMessage(@Param("id") Long id,@Param("userId") Long userId );

    @Update("update chat_message set content = #{content} where id = #{id} and user_id = #{userId} limit 1   ")
    int updateMessage(@Param("id") Long id,@Param("userId") Long userId ,@Param("content") String content);


    @Select(" select * from chat_message where  parent_id = #{parentId}  and status = 1  ")
    List<ChatMessage> getByParentId(@Param("parentId") Long parentId);


    @Update({
            "<script> ",
            " update chat_message set status  = #{status} where  conversation_id = #{conversionId} and  user_id = #{userId} " +
             "  <if test=\"chatId != null and chatId > 0 \"> ",
            "    and id > #{id}  ",
            "  </if>" +
            "</script> "
    })
    int updateMessageStatus(@Param("conversionId") String conversionId, @Param("userId") Long userId,@Param("chatId") Long chatId,@Param("status") Integer status);


}
