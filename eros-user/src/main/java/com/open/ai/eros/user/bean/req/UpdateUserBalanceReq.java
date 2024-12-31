package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @类名：UpdateUserBalanceReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/18 17:32
 */

@ApiModel("修改")
@Data
public class UpdateUserBalanceReq {


    /**
     * 可提现金额
     */
    @ApiModelProperty("可提现金额")
    @NotNull(message = "可提现金额不能为空")
    @Min(0)
    private Long withDrawable;

    /**
     * 不可提现金额
     */
    @ApiModelProperty("不可提现金额")
    @NotNull(message = "不可提现金额不能为空")
    @Min(0)
    private Long noWithDrawable;

    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;


}
