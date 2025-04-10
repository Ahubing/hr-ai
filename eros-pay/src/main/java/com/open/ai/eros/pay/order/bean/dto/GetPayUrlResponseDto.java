package com.open.ai.eros.pay.order.bean.dto;

import lombok.Data;

/**
 * @类名：GetPayUrlResponse
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 21:19
 */
@Data
public class GetPayUrlResponseDto {

    /**
     * 状态码 1为成功，其它值为失败
     */
    private int code;

    /**
     * msg
     */
    private String msg;

    /**
     * 订单号
     */
    private String trade_no;

    //   --------以下三种支付方式只会返回一个
    /**
     * 支付跳转url
     */
    private String payurl;

    /**
     * 二维码链接
     */
    private String qrcode;

    /**
     * 小程序跳转url
     */
    private String urlscheme;

}
