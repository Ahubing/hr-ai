package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @类名：AddUserRightsReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/24 12:22
 */

@ApiModel("给用户添加权益请求类")
@Data
public class AddUserRightsReq {

    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @ApiModelProperty("权益id")
    @NotNull(message = "权益id不能为空")
    private Long rightsId;


}
