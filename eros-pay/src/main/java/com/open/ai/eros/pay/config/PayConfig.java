package com.open.ai.eros.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;



/**
 * @类名：PayConfig
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 20:35
 */
@Data
@ConfigurationProperties("pay")
@Configuration
public class PayConfig {

    /**
     * 支付的域名
     */
    private String cdn;

    /**
     * API接口支付
     */
    private String payUrl;


    /**
     * 支付方式 逗号切割
     */
    private String way;


    /**
     * 支付方式回调
     */
    private String notifyUrl;

    /**
     * 签名字符串
     */
    private String sign;

    /**
     * 签名的方式
     */
    private String 	signType;

    /**
     * 商户ID
     */
    private Integer pid;

    /**
     * 单个订单查询
     */
    private String orderQueryUrl;


}
