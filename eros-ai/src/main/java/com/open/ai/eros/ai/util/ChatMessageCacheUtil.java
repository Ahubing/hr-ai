package com.open.ai.eros.ai.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.constants.ModelPriceEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatMessageCacheUtil {


    public static Cache<String, LinkedList<ChatMessage>> cache ;

    static {
        // 初始化 共用的 缓存
        cache  = CacheBuilder.newBuilder().initialCapacity(100000).expireAfterAccess(720, TimeUnit.MINUTES).build();
    }


    /**
     * 获取用户在缓存中的message
     *
     * @param user
     * @param contentNumber
     * @return
     */
    public static LinkedList<ChatMessage> getUserChatMessages(String user, Integer contentNumber){
        LinkedList<ChatMessage> lastSixElements = new LinkedList<>();
        if(contentNumber<=1){
            return lastSixElements;
        }
        LinkedList<ChatMessage> contextInfo = new LinkedList<>();
        // 添加元素到contextInfo中

        try {
            contextInfo = ChatMessageCacheUtil.cache.get(user, LinkedList::new);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // 获取后6个元素
        int userContextInfoSize = contextInfo.size();
        int startIndex = userContextInfoSize - contentNumber;
        if (startIndex >= 0) {
            ListIterator<ChatMessage> iterator = contextInfo.listIterator(startIndex);
            while (iterator.hasNext()) {
                lastSixElements.addFirst(iterator.next());
            }
        } else {
            // 如果contextInfo的大小小于6，则获取完整的contextInfo列表
            lastSixElements.addAll(contextInfo);
        }
        return lastSixElements;
    }


    /**
     * 获取不会超过 模型最大的token的上下文参数
     *
     * @param chatMessages
     * @param model
     */
    public static void getOkUserChatMessages(LinkedList<ChatMessage> chatMessages, String model){
        ModelPriceEnum modelPrice = ModelPriceEnum.getModelPrice(model);
        Integer maxInTokenNumber = modelPrice.getMaxInTokenNumber();
        for (;;){
            int tokenMessages = TokenUtil.countTokenMessages(chatMessages, model);
            log.info("getOkUserChatMessages tokenMessages={}",tokenMessages);
            if(maxInTokenNumber>tokenMessages){
                break;
            }
            if(chatMessages.size()==1){
                ChatMessage chatBaseMessage = chatMessages.get(0);
                String string = chatBaseMessage.getContent().toString();
                String substring = string.substring(string.length() / 2);
                chatBaseMessage.setContent(substring);
            }
            chatMessages.removeFirst();
        }
    }


    public static void saveChatMessage(String user, ChatMessage chatMessage){
        LinkedList<ChatMessage> chatMessages = new LinkedList<>();
        try {
            chatMessages = cache.get(user, LinkedList::new);
        } catch (ExecutionException e) {
            log.error("exception error e",e);
        }
        chatMessages.add(chatMessage);
    }




}
