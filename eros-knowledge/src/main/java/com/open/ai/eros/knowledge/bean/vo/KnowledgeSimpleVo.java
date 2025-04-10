package com.open.ai.eros.knowledge.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("知识库实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KnowledgeSimpleVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;


}
