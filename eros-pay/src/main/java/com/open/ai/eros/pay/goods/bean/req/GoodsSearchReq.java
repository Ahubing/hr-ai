package com.open.ai.eros.pay.goods.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @类名：GoodsSearchReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/24 21:54
 */
@ApiModel("搜索商品类")
@Data
public class GoodsSearchReq {


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


    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("商品id")
    private Long id;

    @ApiModelProperty("商品类型")
    private String type;

}
