package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Date 2025/1/4 21:17
 */
@Data
public class SearchExchangeListReq {

    /**
     * 选填，关键词、姓名手机号或者微信号
     */
    @ApiModelProperty(value = "选填，兑换码",required = false,notes = "选填，兑换码")
    private String code;

    @ApiModelProperty(value = "页面，从1开始。默认1",required = false,notes = "选填，页面，从1开始。默认1")
    private Integer page = 1;

    @ApiModelProperty("选填，每页数量。默认10")
    private Integer size = 10;

    @ApiModelProperty("选填,状态,0未使用，1已使用")
    private Integer status;



}
