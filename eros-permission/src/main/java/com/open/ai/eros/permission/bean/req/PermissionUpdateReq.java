package com.open.ai.eros.permission.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("更新权限的请求实体类")
public class PermissionUpdateReq {

    @ApiModelProperty("权限ID")
    @NotNull(message = "权限ID不能为空")
    private Long id;

    @ApiModelProperty("权限英文名")
    private String permission;

    @ApiModelProperty("权限名称")
    private String permissionName;

    @ApiModelProperty("权限类型：1-操作权限，2-页面模块权限")
    private Integer permissionType;
}
