package com.open.ai.eros.pay.order.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取支付的url的参数，以下参数都是必填
 *
 */

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class GetPayUrlDto {


    /**
     * 商户ID
     */
    private Integer pid;


    /**
     * 支付方式
     */
    private String type;

    /**
     * 流水号
     */
    private String out_trade_no;

    /**
     * 支付状态通知url
     */
    private String notify_url;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 单位元 1.00 最大两位
     */
    private String money;

    /**
     * 发起支付的ip
     */
    private String clientip;

    /**
     * 签名字符串
     */
    private String sign;

    /**
     * 签名的方式
     */
    private String 	sign_type;

}
