package com.open.ai.eros.pay.order.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @类名：GetPayUrlVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 21:52
 */

@ApiModel("获取支付的实体类")
@Data
public class GetPayUrlVo {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("订单号")
    private Long id;


    @ApiModelProperty("支付url")
    private String payUrl;

}
