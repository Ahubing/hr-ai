package com.open.ai.eros.ai.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @类名：MaskSseConversationVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/22 21:47
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MaskSseConversationVo {

    private SseEmitter sseEmitter;

    private volatile long connectTime;

    public MaskSseConversationVo(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
        this.connectTime = System.currentTimeMillis();
    }
}
