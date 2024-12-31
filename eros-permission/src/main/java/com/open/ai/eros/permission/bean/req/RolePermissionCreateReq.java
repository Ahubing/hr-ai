package com.open.ai.eros.permission.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 角色---创建请求参数类
 */
@Data
@ApiModel(description = "创建角色权限请求")
public class RolePermissionCreateReq {

    @NotBlank(message = "角色不能为空")
    @ApiModelProperty(value = "角色", required = true)
    private String role;

    @NotNull(message = "权限ID不能为空")
    @ApiModelProperty(value = "权限ID", required = true)
    private Long permissionId;

}
