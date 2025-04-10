package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Date 2025/1/4 21:17
 */
@Data
public class SearchUserReq {

    /**
     * 选填，关键词、姓名手机号或者微信号
     */
    @ApiModelProperty("选填，关键词、姓名手机号或者微信号")
    private String keyword;

    @ApiModelProperty("选填，页面，从1开始。默认1")
    private Integer page = 1;

    @ApiModelProperty("选填，每页数量。默认10")
    private Integer size = 10;



}
