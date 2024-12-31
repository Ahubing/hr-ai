package com.open.ai.eros.db.mysql.social.entity;

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
 * @since 2024-08-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("push_message")
public class PushMessage implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 标题
     */
    private String title;

    /**
     * 状态  是否已读   read  unRead
     */
    private String status;

    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createAccount;

    /**
     * 推送去处
     */
    private String pushTo;


}
