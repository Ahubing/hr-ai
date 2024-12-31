package com.open.ai.eros.pay.goods.bean.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GoodsUpdateReq implements Serializable {

    private static final long serialVersionUID=1L;


    @NotNull(message = "商品ID不能为空")
    private Long id;

    /**
     * 商品类型：权益商品 和 普通商品
     */
    @NotEmpty(message = "商品类型不能为空")
    private String type;

    /**
     * 商品
     */
    @NotEmpty(message = "商品名称不能为空")
    private String name;

    /**
     * 普通商品时 出售的额度金额  权益商品时：权益id组成
     */
    @NotNull(message = "商品实质内容不能为空")
    private String goodValue;

    /**
     * 价格
     */
    @NotEmpty(message = "价格不能为空")
    private String price;

    /**
     * 价格的货币
     */
    @NotEmpty(message = "商品类型不能为空")
    private String unit;

    /**
     * 已出售商品数
     */
    @NotNull(message = "商品类型不能为空")
    private Long seedNum;

    /**
     * 商品数
     */
    @Max(99999999)
    @Min(1)
    @NotNull(message = "商品数不能为空")
    private Long total;


    /**
     * 简介
     */
    private String intro;


    @NotNull(message = "状态")
    private Integer status;


}
