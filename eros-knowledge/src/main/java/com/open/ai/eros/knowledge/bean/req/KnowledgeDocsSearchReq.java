package com.open.ai.eros.knowledge.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @类名：KnowledgeDocsSearchReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/12 23:56
 */
@ApiModel("文档搜索类")
@Data
public class KnowledgeDocsSearchReq {


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


    @ApiModelProperty("知识库id")
    private Long knowledgeId;


    @ApiModelProperty("文档id")
    private Long id;


}
