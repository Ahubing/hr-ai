package com.open.ai.eros.permission.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 角色---更新请求参数类
 */
@Data
@ApiModel(description = "更新角色权限请求")
public class RolePermissionUpdateReq {

    @NotNull(message = "ID不能为空")
    @ApiModelProperty(value = "角色权限ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "角色")
    private String role;

    @ApiModelProperty(value = "权限ID")
    private Long permissionId;

}
