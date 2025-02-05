package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 招聘聊天消息表
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_chat_message")
public class AmChatMessage implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 发送用户id (招聘账号或者用户)
     */
    private Long userId;

    /**
     * 对话的角色
     */
    private String role;

    /**
     * 模型
     */
    private String model;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * py客户端当前的消息id,用于过滤重复消息
     */
    private String chatId;

    /**
     * 消息类型 1 为真实的数据, -1 为虚拟的数据(ai生成的,发给客户端的时候,可能会发送失败,但是前端不会显示)
     */
    private Integer type;


}
