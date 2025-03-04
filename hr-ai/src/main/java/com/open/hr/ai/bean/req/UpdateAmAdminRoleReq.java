package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
public class UpdateAmAdminRoleReq {

    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空")
    private Long id;


    @ApiModelProperty("权限")
    @NotEmpty(message = "权限不能为空")
    private String role;


}
