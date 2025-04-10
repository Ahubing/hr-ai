package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *  用户更改自己的密码
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
public class UpdatePasswordReq {

    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空")
    private Long id;


    @ApiModelProperty("旧密码")
    @NotEmpty(message = "旧密码不能为空")
    private String oldPassword;


    @ApiModelProperty("新密码")
    @NotEmpty(message = "新密码不能为空")
    private String newPassword;



}
