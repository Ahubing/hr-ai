package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 14:27
 */
@Data
public class AddAccountReq {


    /**
     * 招聘平台id
     */
    @NotNull(message = "招聘平台id不能为空")
    @ApiModelProperty("招聘平台id")
    private Integer platformId;

    /**
     * 账号
     */
    @NotNull(message = "账号不能为空")
    @ApiModelProperty("账号")
    private String account;


    /**
     * 账号所属城市
     */
    @NotNull(message = "账号所属城市不能为空")
    @ApiModelProperty("账号所属城市")
    private String city;

    /**
     * 手机
     */
    @NotNull(message = "手机不能为空")
    @ApiModelProperty("手机")
    private String mobile;

    /**
     * 用户id
     */
    private Integer adminId;


}
