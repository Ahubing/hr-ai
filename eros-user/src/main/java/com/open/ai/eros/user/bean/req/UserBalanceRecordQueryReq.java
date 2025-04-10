package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用户余额记录---查询请求参数类
 */
@Data
public class UserBalanceRecordQueryReq {


    @ApiModelProperty("账号type 余额类型 1：不可提现 2：可提现")
    @NotNull(message = "账号类型不能为空")
    private Integer userBalanceType;

    @ApiModelProperty(value = "收益类型  全部（不传） 面具分红：mask_chat 邀请新人：invitation_user ")
    private String type;

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小为1")
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNum;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 50, message = "每页条数最大为50")
    @ApiModelProperty(value = "每页条数", required = true)
    private Integer pageSize;

}
