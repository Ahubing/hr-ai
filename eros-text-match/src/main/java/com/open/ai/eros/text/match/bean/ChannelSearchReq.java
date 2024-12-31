package com.open.ai.eros.text.match.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @类名：ChannelSearchReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/20 0:46
 */

@ApiModel("通道搜索类")
@Data
public class ChannelSearchReq {

    @ApiModelProperty("页码")
    @Max(1000)
    @Min(1)
    private Integer page;


    @ApiModelProperty("页数")
    @Max(1000)
    @Min(10)
    private Integer pageSize;


    private String keyWord;


}
