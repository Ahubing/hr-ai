package com.open.ai.eros.pay.order.bean.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @类名：OrderSearchReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/26 12:21
 */

@Data
public class OrderSearchReq {


    @NotNull(message = "页数不为空")
    private Integer pageNum;

    @NotNull(message = "分页数不为空")
    private Integer pageSize;

    private Integer status;

    private Long  id;
    private Long  code;

    private Long userId;

    private Long startTime;

    private Long endTime;

}
