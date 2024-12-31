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
@ApiModel("修改切片实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DocsSliceUpdateReq implements Serializable {

    private static final long serialVersionUID=1L;


    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

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

}
