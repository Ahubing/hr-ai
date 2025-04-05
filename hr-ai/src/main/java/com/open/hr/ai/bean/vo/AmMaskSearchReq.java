package com.open.hr.ai.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @类名：MaskSearchReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/14 21:39
 */

@Data
public class AmMaskSearchReq {

    /**
     * 搜索的关键字
     */
    @ApiModelProperty("搜索关键字")
    private String keywords;


    @ApiModelProperty("页码")
    private Integer page = 1;


    @ApiModelProperty("显示数")
    private Integer pageSize = 10;


    /**
     * 面具的状态
     */
    @ApiModelProperty("面具的状态")
    private Integer status;

    /**
     * 关联的模型ID
     */
    private Long modelId;

}
