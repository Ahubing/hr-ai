package com.open.ai.eros.text.match.bean;

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
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-10-17
 */
@ApiModel("自动回复模版类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AiReplyTemplateVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("ID")
    @JsonSerialize(using = ToStringSerializer.class)
      private Long id;

    /**
     * 回复的内容
     */
    @ApiModelProperty("自动回复内容")
    private String replyContent;


    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


    @ApiModelProperty("匹配标签")
    private Set<String> wordContents;


    @ApiModelProperty("通道id")
    private Set<Long> channelIds;

}
