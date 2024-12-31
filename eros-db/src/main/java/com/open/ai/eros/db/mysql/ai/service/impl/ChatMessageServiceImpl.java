package com.open.ai.eros.db.mysql.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.ai.entity.ChatMessage;
import com.open.ai.eros.db.mysql.ai.entity.GetChatMessageVo;
import com.open.ai.eros.db.mysql.ai.entity.GetNewChatMessageVo;
import com.open.ai.eros.db.mysql.ai.mapper.ChatMessageMapper;
import com.open.ai.eros.db.mysql.ai.service.IChatMessageService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-11
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {



    public  int updateMessageStatus(String conversionId, Long userId,Date startTime,Date endTime){
        return this.getBaseMapper().updateUnReadChatMessage(conversionId,userId,startTime,endTime);
    }


    public  int updateMessageStatus(String conversionId, Long userId,Long chatId, Integer status){
        return this.getBaseMapper().updateMessageStatus(conversionId,userId,chatId,status);
    }


    public int getUnReadChatMessage(String conversionId, Long userId, Date startTime,Date endTime){
        return this.getBaseMapper().getUnReadChatMessage(conversionId,userId,startTime,endTime);
    }



    public ChatMessage getNewMessage(String conversionId,Long userId){
        return this.getBaseMapper().getNewMessage(conversionId,userId);
    }



    /**
     * 根据parentId获取消息信息
     *
     * @param parentId
     * @return
     */
    public List<ChatMessage> getByParentId(Long parentId){
        return this.getBaseMapper().getByParentId(parentId);
    }


    /**
     *
     * 获取AI的聊天记录
     *
     * @return
     */
    public List<ChatMessage> getChatMessage(GetChatMessageVo getChatMessageVo){
        return this.baseMapper.getChatMessage(getChatMessageVo);
    }

    //
    ///**
    // * 获取chatId最新的聊天消息
    // *
    // * @param conversionId
    // * @param userId
    // * @param chatId
    // * @param pageSize
    // * @return
    // */
    //public List<ChatMessage> getNewChatMessage(String conversionId, Long userId, Long chatId, Integer pageSize){
    //    return this.baseMapper.getNewChatMessage(conversionId,userId, Arrays.asList(AIRoleEnum.USER.getRoleName(),AIRoleEnum.ASSISTANT.getRoleName()),chatId,pageSize);
    //}


    public List<ChatMessage> getNewChatMessage(GetNewChatMessageVo newChatMessageVo){
        return this.baseMapper.getNewChatMessage(newChatMessageVo);
    }


    public int backMessage(String conversationId,Long id){
        return this.getBaseMapper().backMessage(conversationId,id);
    }


    /**
     * 删除用户的单个对话消息
     *
     * @param id
     * @return
     */
    public int deleteById(Long id){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(id);
        chatMessage.setStatus(2);
        return this.baseMapper.updateById(chatMessage);
    }


    /**
     * 修改用户的单个对话消息
     *
     * @param id
     * @return
     */
    public int updateById(Long id,Long userId,String content){
        return this.baseMapper.updateMessage(id,userId,content);
    }

}
