package com.open.ai.eros.pay.order.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @类名：CreateOrderReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 20:03
 */
@ApiModel("创建订单")
@Data
public class CreateOrderReq {


    @ApiModelProperty("商品id")
    @NotNull(message = "商品id不能为空")
    private Long goodId;

    @ApiModelProperty("支付方式")
    @NotEmpty(message = "支付方式不能为空")
    private String payWay;

}
