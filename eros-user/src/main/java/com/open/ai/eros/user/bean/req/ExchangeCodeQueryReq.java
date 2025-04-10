package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @类名：ExchangeCodeQueryReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/28 23:21
 */
@ApiModel("兑换码搜索类")
@Data
public class ExchangeCodeQueryReq {

    private Long userId;

    @ApiModelProperty("页数")
    @Max(1000)
    @Min(1)
    @NotNull(message = "页数")
    private Integer pageNum;


    @ApiModelProperty("页码")
    @Max(50)
    @Min(10)
    @NotNull(message = "pageSize")
    private Integer pageSize;


    @ApiModelProperty("兑换码类型")
    private String type;

    @ApiModelProperty("兑换码")
    private String code;

    @ApiModelProperty("状态")
    private Integer status;


}
