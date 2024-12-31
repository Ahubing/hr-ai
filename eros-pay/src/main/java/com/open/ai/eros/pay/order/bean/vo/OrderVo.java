package com.open.ai.eros.pay.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.pay.goods.bean.vo.CGoodsVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * @since 2024-08-25
 */

@ApiModel("订单表")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderVo implements Serializable {

    private static final long serialVersionUID=1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("订单id")
    private Long id;

    /**
     * 流水号
     */
    @ApiModelProperty("流水号")
    private String code;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private String price;

    /**
     * 商品id
     */
    @ApiModelProperty("商品信息")
    private CGoodsVo cGoodsVo;


    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("状态描述")
    private String statusDesc;

    /**
     * 结算单位
     */
    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


    @ApiModelProperty("支付url")
    private String payUrl;



}
