package com.open.ai.eros.db.mysql.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_follow_mask")
public class UserFollowMask implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 面具id
     */
    private Long maskId;

    private LocalDateTime createTime;


}
