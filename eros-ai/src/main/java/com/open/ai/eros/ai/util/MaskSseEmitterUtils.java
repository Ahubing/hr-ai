package com.open.ai.eros.ai.util;

import com.open.ai.eros.ai.bean.vo.MaskSseConversationVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


/**
 * 面具会话聊天的sse聊天
 */
@Component
@Slf4j
public class MaskSseEmitterUtils {


    public static final long timeOut = 600000L;

    /**
     * 使用map对象，便于根据userId来获取对应的SseEmitter，或者放redis里面
     */
    private Map<String, MaskSseConversationVo> sseEmitterMap = new ConcurrentHashMap<>();

    public MaskSseConversationVo getSseEmitter(String conversationId) {
        return sseEmitterMap.get(conversationId);
    }


    /**
     * 创建用户连接并返回 SseEmitter
     */
    public void connect(String conversationId) {
        try {
            /**
             * 设置超时时间，0表示不过期。默认30秒
             */
            SseEmitter sseEmitter = new SseEmitter(timeOut);
            /**
             * 注册回调
             */
            sseEmitter.onCompletion(completionCallBack(conversationId));
            sseEmitter.onError(errorCallBack(conversationId));
            sseEmitter.onTimeout(timeoutCallBack(conversationId));
            MaskSseConversationVo conversationVo = MaskSseConversationVo.builder().sseEmitter(sseEmitter).connectTime(System.currentTimeMillis()).build();
            sseEmitterMap.put(conversationId, conversationVo);
            sseEmitter.send(SendMessageUtil.EROS_CONNECT_SUCCESS);
        } catch (Exception e) {
            log.info("创建新的sse连接异常，当前用户：{}", conversationId);
        }
    }


    /**
     * 移除用户连接
     */
    public void removeUser(String conversationId) {
        sseEmitterMap.remove(conversationId);
        log.info("移除用户：{}", conversationId);
    }


    private Runnable completionCallBack(String conversationId) {
        return () -> {
            log.info("结束连接：{}", conversationId);
            removeUser(conversationId);
        };
    }

    private Runnable timeoutCallBack(String conversationId) {
        return () -> {
            log.info("连接超时：{}", conversationId);
            removeUser(conversationId);
        };
    }

    private Consumer<Throwable> errorCallBack(String conversationId) {
        return throwable -> {
            log.info("连接异常：{}", conversationId);

            removeUser(conversationId);
        };
    }


}
