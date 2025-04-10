package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
public class SearchAmAdminReq {


    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = false, notes = "账号")
    private String username;


    @ApiModelProperty(value = "邮箱", required = false, notes = "邮箱")
    private String email;


    /**
     * 状态。0未启用，1禁用，2启用
     */
    @ApiModelProperty(value = "状态。0未启用，1禁用，2启用", required = false, notes = "状态。0未启用，1禁用，2启用")
    private Integer status;


    @ApiModelProperty(value = "电话号码", required = false, notes = "电话号码")
    private String mobile;

    @ApiModelProperty(value = "页面，从1开始。默认1", required = false, notes = "选填，页面，从1开始。默认1")
    private Integer page = 1;

    @ApiModelProperty("选填，每页数量。默认10")
    private Integer size = 10;


}
