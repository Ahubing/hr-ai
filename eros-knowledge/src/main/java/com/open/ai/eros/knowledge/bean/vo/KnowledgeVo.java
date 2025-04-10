package com.open.ai.eros.knowledge.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@ApiModel("知识库实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KnowledgeVo implements Serializable {

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
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


    @ApiModelProperty("向量模型")
    private String templateModel;


    @ApiModelProperty("向量数据库")
    private String vectorDatabase;

}
