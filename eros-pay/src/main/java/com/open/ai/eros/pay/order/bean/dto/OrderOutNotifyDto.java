package com.open.ai.eros.pay.order.bean.dto;

import lombok.Data;

/**
 * @类名：OrderOutNotifyDto
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/4 22:43
 */
@Data
public class OrderOutNotifyDto {

    /**
     * 商户系统内部的订单号 (订单id)
     */
    private String out_trade_no;

    /**
     * 	聚合支付平台订单号 (流水)
     */
    private String trade_no;

    /**
     * 	只有TRADE_SUCCESS是成功
     */
    private String 	trade_status;
}
