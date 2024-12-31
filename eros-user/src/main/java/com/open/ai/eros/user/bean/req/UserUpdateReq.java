package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户信息更新请求参数
 */
@Data
@ApiModel("用户信息更新的实体类")
public class UserUpdateReq {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("用户昵称")
    private String userName;

    @ApiModelProperty("用户头像URL")
    private String avatar;


    /**
     * 用户角色
     */
    @ApiModelProperty("用户角色")
    private String role;



}
