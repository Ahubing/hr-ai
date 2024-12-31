package com.open.ai.eros.db.mysql.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 兑换码表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("exchange_code")
public class ExchangeCode implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 兑换码类型 type: balance   rights
     */
    private String type;

    /**
     * 价值 额度 或者 权益
     */
    private String bizValue;

    /**
     * 兑换码名称
     */
    private String name;

    /**
     * 已兑换数
     */
    private Long usedNum;

    /**
     * 兑换码
     */
    private String code;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 发行的数量
     */
    private Long total;

    private LocalDateTime createTime;

    /**
     * 状态 1：有效 2：下架
     */
    private Integer status;


}
