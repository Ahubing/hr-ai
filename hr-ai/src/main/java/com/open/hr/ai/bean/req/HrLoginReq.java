package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求参数
 */
@ApiModel("用户登录的实体类")
@Data
public class HrLoginReq {


    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;


    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String password;


}
