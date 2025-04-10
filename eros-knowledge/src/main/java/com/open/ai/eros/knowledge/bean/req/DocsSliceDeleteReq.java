package com.open.ai.eros.knowledge.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 文档切片表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@ApiModel("删除文档切片类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DocsSliceDeleteReq implements Serializable {



    /**
     * 向量库的ID
     */
    @ApiModelProperty("ID")
    private Long id;


    /**
     * 文档ID
     */
    @ApiModelProperty("文档ID")
    private Long docsId;



}
