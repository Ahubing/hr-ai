package com.open.ai.eros.ai.bean.vo;

import com.open.ai.eros.ai.lang.chain.bean.TokenUsageVo;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.user.bean.vo.UserCacheBalanceVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @类名：AITextChatReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/8 21:25
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AITextChatVo {

    /**
     * ai回复的消息id
     */
    private Long aiChatMessageId;

    private String template;

    private String model;

    private Boolean stream = true;

    /**
     * 知识库id
     */
    private Long knowledgeId;

    /**
     * 上下文的条数
     */
    private Integer contentNumber;

    /**
     * 会话类型
     */
    private Integer conversationType;

    /**
     * 工具
     */
    private List<String> tool;

    /**
     * 面具id
     */
    private Long maskId;

    /**
     * 调试面具的最新消息
     */
    private BMaskVo bMaskVo;


    /**
     * 用户对话的id
     */
    private Long chatId;

    /**
     * 会话id
     */
    private String conversationId;

    private LinkedList<ChatMessage> messages;

    /**
     * 检索最小的得分
     */
    private Double minScore;


    /**
     * 最后一次推送的内容
     */
    private List<DocsSource> docsSources;

    /**
     * 当前访问用户的钱包
     */
    private UserCacheBalanceVo userCacheBalanceVo;

    private List<dev.langchain4j.data.message.ChatMessage> toolExecutionResultMessages;

    private Long shareMaskId;

    /**
     * 所有的ai操作计费
     */
    private List<TokenUsageVo> tokenUsages;

    public List<TokenUsageVo> getTokenUsages(){
        if(this.tokenUsages==null){
            this.tokenUsages = new ArrayList<>();
        }
        return this.tokenUsages;
    }


    /**
     * 用户问题前缀
     */
    private String userPrefix;

    /**
     * 用户问题的后缀
     */
    private String userSuffix;


}
