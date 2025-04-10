package com.open.ai.eros.social.bean.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@ApiModel("最新消息的实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LastMessagePushMessageVo {


    @ApiModelProperty("Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 文本内容
     */
    @ApiModelProperty("消息内容")
    private String content;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;


}
