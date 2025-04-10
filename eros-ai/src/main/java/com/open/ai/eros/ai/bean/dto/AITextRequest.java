package com.open.ai.eros.ai.bean.dto;

import com.open.ai.eros.common.vo.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * @类名：AITextReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/12/9 0:12
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AITextRequest {


    /**
     * 知识库标识
     */
    private Long knowledgeId;


    /**
     * 用户消息
     */
    private LinkedList<ChatMessage> messages;

    /**
     * FunctionCall的结果消息
     */
    private List<dev.langchain4j.data.message.ChatMessage> toolExecutionResultMessages;


    /**
     * 用户id
     */
    private Long userId;


    /**
     * 模板
     */
    private String template;

    /**
     * 模型
     */
    private String model;


    /**
     * 上下文的条数
     */
    private Integer contentNumber;


    /**
     * 工具
     */
    private List<String> tool;


    /**
     * 面具id
     */
    private Long maskId;


}
