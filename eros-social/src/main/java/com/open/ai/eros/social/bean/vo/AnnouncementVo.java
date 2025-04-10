package com.open.ai.eros.social.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@ApiModel("公告的实体类")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AnnouncementVo {

    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 公告的标题，不能为空
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 公告的内容，不能为空
     */
    @ApiModelProperty("内容")
    private String content;

    /**
     * 发布公告的作者，不能为空
     */
    @ApiModelProperty("发布公告的作者")
    private String createUser;

    /**
     * 公告的创建时间，默认为当前时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 公告的修改时间
     */
    @ApiModelProperty("公告的修改时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime updateTime;

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("悬浮时间")
     private Integer duration;
}
