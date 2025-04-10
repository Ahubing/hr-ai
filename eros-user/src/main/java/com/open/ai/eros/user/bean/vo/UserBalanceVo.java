package com.open.ai.eros.user.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @类名：UserBalanceVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/13 23:37
 */
@ApiModel("用户的余额信息")
@Data
public class UserBalanceVo {

    /**
     * 可提现金额
     */
    @ApiModelProperty("可提现金额")
    private String withDrawable;

    /**
     * 不可提现金额
     */
    @ApiModelProperty("不可提现金额")
    private String noWithDrawable;


}
