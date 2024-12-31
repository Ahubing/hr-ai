package com.open.ai.eros.db.mysql.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.ai.entity.ChatConversation;
import com.open.ai.eros.db.mysql.ai.mapper.ChatConversationMapper;
import com.open.ai.eros.db.mysql.ai.service.IChatConversationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-04
 */
@Service
public class ChatConversationServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversation> implements IChatConversationService {



    public int deleteChatConversation(Long userId,String chatConversationId){
        return this.baseMapper.deleteChatConversation(userId, chatConversationId);
    }


    public int updateChatConversation(Long userId,String chatConversationId,String name){




        return this.baseMapper.updateChatConversation(userId, chatConversationId,name);
    }


    public ChatConversation getChatConversationByUserAndMaskId(Long userId,Long maskId){
        return this.getBaseMapper().getChatConversationByUserAndMaskId(userId, maskId);
    }


}
