package com.open.ai.eros.social.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel("修改消息的实体类")
@Data
public class PushMessageUpdateReq {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 文本内容
     */
    @ApiModelProperty("内容")
    @NotEmpty(message = "内容不能为空")
    private String content;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    @NotEmpty(message = "标题不能为空")
    private String title;


    /**
     * 发送信息的id
     */
    @ApiModelProperty("指定用户id")
    @NotNull(message = "指定用户id不能为空")
    private Long targetUserId;

    @ApiModelProperty("推送去处")
    @NotEmpty(message = "推送去处")
    private String pushTo;

}
