package com.open.ai.eros.pay.order.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @类名：OrderStatusVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/4 12:55
 */
@ApiModel("订单状态")
@Data
public class OrderStatusVo {

    @ApiModelProperty("状态标识")
    private Integer status;

    @ApiModelProperty("状态描述")
    private String desc;

}
