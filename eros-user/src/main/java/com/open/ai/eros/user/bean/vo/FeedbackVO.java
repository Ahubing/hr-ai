package com.open.ai.eros.user.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel("反馈信息类")
@Data
public class FeedbackVO {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("反馈ID")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("反馈内容")
    private String content;

    @ApiModelProperty("反馈类型")
    private String type;

    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;


    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    @ApiModelProperty("修改时间")
    private LocalDateTime modifyTime;

    @ApiModelProperty("反馈状态")
    private String status;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("联系方式")
    private String contact;

    @ApiModelProperty("附件")
    private List<String> fileList;

    @ApiModelProperty("额外说明")
    private String extra;
}
