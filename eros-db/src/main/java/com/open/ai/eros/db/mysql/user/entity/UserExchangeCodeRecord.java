package com.open.ai.eros.db.mysql.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户兑换码记录表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_exchange_code_record")
public class UserExchangeCodeRecord implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 兑换码id
     */
    private Long exchangeCodeId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
