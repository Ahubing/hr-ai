package com.open.ai.eros.db.mysql.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_conversation")
public class ChatConversation implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * uuid
     */
      private String id;

    /**
     * 面具的id
     */
    private Long maskId;

    /**
     * 会话名
     */
    private String name;

    /**
     * 面具头像
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 分享的会话id
     */
    private Long shareMaskId;

    /**
     * 知识库id
     */
    private Long knowledgeId;

    /**
     * ai参数
     */
    private String aiParam;

    /**
     * 会话类型 1：面具 2 知识库 3 常规
     */
    private Integer type;


}
