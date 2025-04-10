package com.open.ai.eros.permission.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("创建权限的请求实体类")
public class PermissionCreateReq {

    @ApiModelProperty("权限英文名")
    @NotBlank(message = "权限英文名不能为空")
    private String permission;

    @ApiModelProperty("权限名称")
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    @ApiModelProperty("权限类型：1-操作权限，2-页面模块权限")
    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;
}
