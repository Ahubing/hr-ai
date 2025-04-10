package com.open.ai.eros.pay.goods.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-24
 */
@ApiModel("商品类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GoodsVo implements Serializable {

    private static final long serialVersionUID=1L;


    @JsonSerialize(using = ToStringSerializer.class)
      private Long id;

    /**
     * 商品类型：权益商品 和 普通商品
     */
    @ApiModelProperty("商品类型")
    private String type;

    @ApiModelProperty("商品描述")
    private String typeDesc;

    /**
     * 商品
     */
    @ApiModelProperty("商品名称")
    private String name;


    /**
     * 权益可视化内容
     */
    @ApiModelProperty("权益可视化内容")
    private List<CRightsVo> rightsVos;

    /**
     * 权益实际内容
     */
    @ApiModelProperty("权益实际内容")
    private String goodValue;

    /**
     * 价格
     */
    @ApiModelProperty("价格")
    private Double price;

    @ApiModelProperty("价格可视化")
    private String priceStr;

    /**
     * 价格的货币
     */
    @ApiModelProperty("货币")
    private String unit;

    /**
     * 已出售商品数
     */
    @ApiModelProperty("已出售商品数")
    private Long seedNum;

    /**
     * 商品数
     */
    @ApiModelProperty("商品数")
    private Long total;


    /**
     * 简介
     */
    @ApiModelProperty("简介")
    private String intro;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


    /**
     * 状态 1：出售中  2：下架
     */
    @ApiModelProperty("状态")
    private Integer status;

}
