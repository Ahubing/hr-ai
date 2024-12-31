package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ApiModel("关注面具请求类")
@Data
public class FollowMaskOpReq {


    @ApiModelProperty("面具id")
    @NotNull(message = "面具id不能为空")
    private Long maskId;


    /**
     * 1 是 关注 2 取消关注
     */
    @ApiModelProperty("操作op 1 是 关注 2 取消关注  ")
    @Max(2)
    @Min(1)
    private int op;

}
