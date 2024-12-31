package com.open.ai.eros.db.mysql.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 分享面具
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("share_mask")
public class ShareMask implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 面具id
     */
    private Long maskId;

    /**
     * 聊天结束id
     */
    private Long endChatId;

    /**
     * 会话id
     */
    private String conversionId;

    /**
     * 聊天起始id
     */
    private Long startChatId;

    /**
     * 分享标题
     */
    private String title;

    /**
     * 分享用户id
     */
    private Long userId;

    /**
     * 使用次数
     */
    private Integer useNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
