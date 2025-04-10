package com.open.ai.eros.knowledge.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@ApiModel("新增知识库类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KnowledgeAddReq implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @NotEmpty(message = "知识库名称不能为空")
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


    @ApiModelProperty("向量模型")
    private String templateModel;


    @ApiModelProperty("向量数据库")
    private String vectorDatabase;




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
