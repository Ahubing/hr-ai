package com.open.ai.eros.permission.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色--查询请求参数类
 */
@Data
@ApiModel(description = "角色权限查询请求")
public class RolePermissionQueryReq {

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "角色")
    private String role;

}
