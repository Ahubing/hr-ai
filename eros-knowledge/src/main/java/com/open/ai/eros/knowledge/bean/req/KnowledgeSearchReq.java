package com.open.ai.eros.knowledge.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @类名：KnowledgeSearchReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/12 18:14
 */
@ApiModel("知识库搜索类")
@Data
public class KnowledgeSearchReq {


    @ApiModelProperty("页码")
    @Min(1)
    private Integer pageNum;

    @ApiModelProperty("页数")
    @Max(50)
    @Min(10)
    private Integer pageSize;


    @ApiModelProperty("关键字")
    private String keyword;

    @ApiModelProperty("创建人id")
    private Long userId;



}
