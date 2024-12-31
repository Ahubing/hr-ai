package com.open.ai.eros.knowledge.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 文档切片表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@ApiModel("新增切片实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DocsSliceAddReq implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 知识库ID
     */
    @ApiModelProperty("知识库ID")
    @NotNull(message = "知识库id不能为空")
    private Long knowledgeId;

    /**
     * 文档名称
     */
    @ApiModelProperty("名称")
    @NotEmpty(message = "名称不能为空")
    private String name;

    /**
     * 切片内容
     */
    @ApiModelProperty("切片内容")
    @NotEmpty(message = "名切片内容不能为空")
    private String content;


    /**
     * 跟随文档的type
     */
    @ApiModelProperty("切片类型-和文档类型一致")
    @NotEmpty(message = "切片类型不能为空")
    private String type;

}
