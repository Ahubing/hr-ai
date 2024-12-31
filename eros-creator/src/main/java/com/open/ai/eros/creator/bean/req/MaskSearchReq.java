package com.open.ai.eros.creator.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @类名：MaskSearchReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/14 21:39
 */
@ApiModel("面具搜索类")
@Data
public class MaskSearchReq {

    /**
     * 搜索的关键字
     */
    @ApiModelProperty("搜索关键字")
    private String keywords;


    @ApiModelProperty("页码")
    private Integer pageNum = 1;


    @ApiModelProperty("显示数")
    private Integer pageSize = 10;

    /**
     * 标识
     */
    @ApiModelProperty("标识")
    private String tab;

    /**
     * 面具的类型
     */
    @ApiModelProperty("面具的类型")
    private String type;

}
