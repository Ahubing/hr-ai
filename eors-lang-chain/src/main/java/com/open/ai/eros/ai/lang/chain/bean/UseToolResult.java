package com.open.ai.eros.ai.lang.chain.bean;

import dev.langchain4j.data.message.ChatMessage;
import lombok.Data;

import java.util.List;

/**
 * @类名：UseToolResult
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/25 0:06
 */
@Data
public class UseToolResult {


    private List<ChatMessage> chatMessages;

    private TokenUsageVo tokenUsage;


}
