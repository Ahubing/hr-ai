package com.open.ai.eros.pay.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @类名：GoodsSearchResult
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/24 21:13
 */

@ApiModel("商品的搜索结果类")
@Data
public class GoodsSearchResult {


    @ApiModelProperty("是否是最后一页")
    private boolean lastPage;


    @ApiModelProperty("商品列表")
    private List<CGoodsVo> goodsVos;



}
