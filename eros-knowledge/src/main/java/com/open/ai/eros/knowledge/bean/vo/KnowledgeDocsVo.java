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
@ApiModel("知识库文档实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KnowledgeDocsVo implements Serializable {

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
     * 知识库id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("知识库id")
    private Long knowledgeId;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private String type;

    /**
     * url
     */
    @ApiModelProperty("url")
    private String url;


    /**
     * 切片数量
     */
    @ApiModelProperty("切片数量")
    private Integer sliceNum;

    /**
     *  切片状态
     */
    @ApiModelProperty("切片状态")
    private Integer sliceStatus;

    /**
     * 文档内容
     */
    @ApiModelProperty("文档内容")
    private String content;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


    /**
     * 切割方式
     */
    @ApiModelProperty("切割方式")
    private String splitterType;

}
