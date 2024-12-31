package com.open.ai.eros.knowledge.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * <p>
 * 文档切片表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@ApiModel("文档切片搜索类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DocsSliceSearchReq implements Serializable {


    @ApiModelProperty("页码")
    @Min(1)
    private Integer pageNum;


    @ApiModelProperty("页数")
    @Max(50)
    @Min(10)
    private Integer pageSize;


    /**
     * 文档ID
     */
    @ApiModelProperty("文档ID")
    private Long docsId;

    /**
     * 知识库ID
     */
    @ApiModelProperty("知识库ID")
    private Long knowledgeId;

    /**
     * 文档名称
     */
    @ApiModelProperty("文档名称")
    private String keyword;


    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;

    /**
     * 状态 1: 未向量  2：向量化
     */
    @ApiModelProperty("状态")
    private Integer status;

}
