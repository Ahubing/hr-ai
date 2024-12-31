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
 * @since 2024-08-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 对话的角色
     */
    private String role;

    /**
     * 面具id
     */
    private Long maskId;

    /**
     * 模型
     */
    private String model;


    /**
     * 用户对话的id
     */
    private Long parentId;


    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 1：可见 2：不可见
     */
    private Integer status;


    /**
     * 是否已读  1 已读 2 未读
     */
    private Integer readStatus;


    /**
     * 质量 默认 0  1：好 2：不好
     */
    private Integer quality;

}
