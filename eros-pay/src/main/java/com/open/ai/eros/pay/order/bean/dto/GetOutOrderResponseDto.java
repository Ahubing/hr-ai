package com.open.ai.eros.pay.order.bean.dto;

import lombok.Data;

/**
 * @类名：GetOutOrderResponseDto
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 22:22
 */
@Data
public class GetOutOrderResponseDto {

    private int code;

    /**
     * 支付状态
     * 	1为支付成功，0为未支付
     */
    private int status;


}
