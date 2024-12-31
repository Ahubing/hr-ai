package com.open.ai.eros.db.mysql.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户余额表，存储用户的可提取和不可提取余额
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_balance")
public class UserBalance implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 余额记录ID
     */
      private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 余额类型，可提取或不可提取
     */
    private String type;

    /**
     * 余额，最多10位数字，其中2位是小数
     */
    private Long balance;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
