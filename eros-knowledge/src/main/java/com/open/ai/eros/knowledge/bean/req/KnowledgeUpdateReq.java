package com.open.ai.eros.knowledge.bean.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@ApiModel("修改知识库类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KnowledgeUpdateReq implements Serializable {

    private static final long serialVersionUID=1L;


    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 封面
     */
    @ApiModelProperty("封面")
    private String cover;

    /**
     * 简介说明
     */
    @ApiModelProperty("简介说明")
    private String intro;


    /**
     * 检索最小的得分
     */
    @ApiModelProperty("检索最小的得分")
    private Double minScore;

    /**
     * 拦截的条数
     */
    @ApiModelProperty("拦截的条数")
    private Integer number;

    /**
     * 严格模式 1:严格 2:不严格
     */
    @ApiModelProperty("严格模式 1:严格 2:不严格")
    private Integer strict;

}
