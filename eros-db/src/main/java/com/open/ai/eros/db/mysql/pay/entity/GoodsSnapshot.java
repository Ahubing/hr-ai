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
 * 商品快照表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("goods_snapshot")
public class GoodsSnapshot implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 商品类型：权益商品 和 普通商品
     */
    private String type;

    private Long goodsId;

    /**
     * 商品
     */
    private String name;

    /**
     * 普通商品时 出售的额度金额  权益商品时：权益id组成
     */
    private String goodValue;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 价格的货币
     */
    private String unit;

    /**
     * 已出售商品数
     */
    private Long seedNum;

    /**
     * 商品数
     */
    private Long total;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 简介
     */
    private String intro;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
