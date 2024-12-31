package com.open.ai.eros.pay.order.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @类名：OrderQuerySearchResult
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/26 12:19
 */


@ApiModel("b端查询订单的实体")
@Data
public class BOrderQuerySearchResult {



    @ApiModelProperty("订单列表")
    private List<OrderVo> orderVos;


}
