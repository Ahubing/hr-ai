package com.open.ai.eros.db.mysql.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_order")
public class Order implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 流水号
     */
    private String code;

    /**
     * 汇率
     */
    private BigDecimal rate;

    /**
     * 金额
     */
    private BigDecimal price;

    /**
     * 商品快照id
     */
    private Long goodSnapshotId;


    /**
     * 商品id
     */
    private Long goodsId;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 状态 1：待支付 2 支付成功 3：取消、超时
     */
    private Integer status;

    /**
     * 支付的url
     */
    private String payUrl;

    /**
     * 结算单位
     */
    private String unit;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
