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
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@ApiModel("修改知识库文档实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KnowledgeDocsUpdateReq implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
      private Long id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @NotEmpty(message = "名称不能为空")
    private String name;

    /**
     * 知识库id
     */
    @ApiModelProperty("知识库id")
    @NotNull(message = "知识库id不能为空")
    private Long knowledgeId;

    /**
     * 文档内容
     */
    @ApiModelProperty("文档内容")
    private String content;


    @ApiModelProperty("切割规则")
    private String sliceRule;

    /**
     * 切割方式
     */
    @ApiModelProperty("切割方式")
    private String splitterType;

}
