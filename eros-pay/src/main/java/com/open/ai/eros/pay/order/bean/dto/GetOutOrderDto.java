package com.open.ai.eros.pay.order.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @类名：GetOutOrderDto
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 22:19
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetOutOrderDto {


    //----------提示：系统订单号 和 商户订单号 二选一传入即可，如果都传入以系统订单号为准！
    /**
     * 内部的订单id
     */
    private String trade_no	;

    /**
     * 聚合平台的订单号
     */
    private String out_trade_no;


}
