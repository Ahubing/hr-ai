package com.open.ai.eros.ai.util;

import com.open.ai.eros.common.service.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 面具频道的聊天的sse
 *
 */
@Component
@Slf4j
public class MaskChannelSseEmitterUtils {


    public static final long timeOut = 600000L;

    @Autowired
    private RedisClient redisClient;

    /**
     * 使用map对象，便于根据userId来获取对应的SseEmitter，或者放redis里面
     */
    private  Map<String, Map<Long,SseEmitter>> sseEmitterMap = new ConcurrentHashMap<>();


    public  SseEmitter getConnect(Long maskId,Long userId) {
        Map<Long, SseEmitter> emitterMap = sseEmitterMap.getOrDefault(String.valueOf(maskId), new ConcurrentHashMap<>());

        SseEmitter sseEmitter = emitterMap.get(userId);
        if(sseEmitter!=null){
            return sseEmitter;
        }
        connect(maskId, userId);
        emitterMap = sseEmitterMap.getOrDefault(String.valueOf(maskId), new ConcurrentHashMap<>());
        sseEmitter = emitterMap.get(userId);
        return sseEmitter;
    }

    /**
     * 创建用户连接并返回 SseEmitter
     *
     */
    public void connect(Long maskId,Long userId) {
        String maskIdKey = String.valueOf(maskId);
        try {
            /**
             * 设置超时时间，0表示不过期。默认30秒
             */
            SseEmitter sseEmitter = new SseEmitter(timeOut);
            /**
             * 注册回调
             */
            sseEmitter.onCompletion(completionCallBack(maskId,userId));
            sseEmitter.onError(errorCallBack(maskId,userId));
            sseEmitter.onTimeout(timeoutCallBack(maskId,userId));


            Map<Long, SseEmitter> longSseEmitterMap = sseEmitterMap.get(maskIdKey);
            if(longSseEmitterMap==null){
                synchronized (MaskChannelSseEmitterUtils.class){
                    longSseEmitterMap = sseEmitterMap.get(maskIdKey);
                    if(longSseEmitterMap==null){
                        longSseEmitterMap = new ConcurrentHashMap<>();
                        sseEmitterMap.put(maskIdKey,longSseEmitterMap);
                    }
                }
            }
            longSseEmitterMap.put(userId,sseEmitter);
            /**
             * 数量+1
             */
            redisClient.hincr("mask:online",String.valueOf(maskId),1L);
        } catch (Exception e) {
            log.info("创建新的sse连接异常，当前用户：{}", maskIdKey);
        }
    }

    ///**
    // * 给指定用户发送消息
    // *
    // */
    //public  void sendMessage(Long userId,Long maskId, String message) {
    //    String key = String.format("%s:%s", maskId, userId);
    //    if (sseEmitterMap.containsKey(key)) {
    //        try {
    //            sseEmitterMap.get(key).send(message);
    //        } catch (IOException e) {
    //            log.error("用户[{}]推送异常:{}", key, e.getMessage());
    //            removeUser(maskId,userId);
    //        }
    //    }
    //}

    /**
     * 向同组人发布消息   （要求userId+groupId）
     *
     */
    //public  void groupSendMessage(Long maskId, String message) {
    //
    //    if (MapUtil.isNotEmpty(sseEmitterMap)) {
    //        sseEmitterMap.forEach((k, v) -> {
    //            try {
    //                if (k.startsWith(String.valueOf(maskId))) {
    //                    v.send(message, MediaType.APPLICATION_JSON);
    //                }
    //            } catch (IOException e) {
    //                log.error("用户[{}]推送异常:{}", k, e.getMessage());
    //                String[] split = k.split(":");
    //                removeUser(Long.parseLong(split[0]),Long.parseLong(split[1]));
    //            }
    //        });
    //    }
    //}


    /**
     * 给一个面具频道发送消息
     *
     * @param maskId
     * @param message
     * @param sendUserId
     */
    public  void batchSendMessage(Long maskId,String message,Long sendUserId) {
        String maskIdKey = String.valueOf(maskId);
        Map<Long, SseEmitter> emitterMap = sseEmitterMap.getOrDefault(maskIdKey, new ConcurrentHashMap<>());
        Set<Map.Entry<Long, SseEmitter>> entries = emitterMap.entrySet();
        for (Map.Entry<Long, SseEmitter> entry : entries) {
            Long userId = entry.getKey();
            SseEmitter sseEmitter = entry.getValue();
            try {
                // 不需要自己给自己发消息
                if(!Objects.equals(userId, sendUserId)){
                    sseEmitter.send(message);
                }
            } catch (IOException e) {
                log.error("用户[{}]推送异常:{}", userId, e.getMessage());
                removeUser(maskId,userId);
            }
        }


    }

    ///**
    // * 群发消息
    // *
    // * @date: 2022/7/12 14:51
    // * @auther: 公众号：程序员小富
    // */
    //public  void batchSendMessage(String message, Set<String> ids) {
    //    ids.forEach(userId -> sendMessage(userId, message));
    //}

    /**
     * 移除用户连接
     */
    public  void removeUser(Long maskId,Long userId) {
        String maskIdKey = String.valueOf(maskId);

        Map<Long, SseEmitter> longSseEmitterMap = sseEmitterMap.get(maskIdKey);
        if(longSseEmitterMap!=null){
            longSseEmitterMap.remove(userId);
        }
        // 数量-1
        redisClient.hincr("mask:online",String.valueOf(maskId),-1L);
        log.info("面具：maskId={} 移除用户：{}", maskId,userId);
    }


    /**
     * 获取当前连接数量
     */
    public  int getUserCount(Long maskId) {
        String hget = redisClient.hget("mask:online", String.valueOf(maskId));
        if(StringUtils.isEmpty(hget)){
            return 0;
        }
        return Integer.parseInt(hget);
    }

    private  Runnable completionCallBack(Long maskId,Long userId) {
        String key = String.format("%s:%s", maskId, userId);
        return () -> {
            log.info("结束连接：{}", key);
            removeUser(maskId,maskId);
        };
    }

    private  Runnable timeoutCallBack(Long maskId,Long userId) {
        String key = String.format("%s:%s", maskId, userId);
        return () -> {
            log.info("连接超时：{}", key);
            removeUser(maskId,maskId);
        };
    }

    private  Consumer<Throwable> errorCallBack(Long maskId,Long userId) {
        String key = String.format("%s:%s", maskId, userId);
        return throwable -> {
            log.info("连接异常：{}", key);
            removeUser(maskId,userId);
        };
    }


}
