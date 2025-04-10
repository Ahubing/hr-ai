package com.open.ai.eros.db.mysql.text.entity;

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
 * @since 2024-10-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_reply_template")
public class AiReplyTemplate implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 回复的内容
     */
    private String replyContent;

    private LocalDateTime createTime;

    private Long userId;


}
