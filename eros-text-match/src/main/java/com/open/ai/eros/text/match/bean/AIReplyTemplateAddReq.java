package com.open.ai.eros.text.match.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @类名：AIReplyTemplateAddReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/20 1:13
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("自动回复模版新增类")
@Data
public class AIReplyTemplateAddReq {


    @NotEmpty(message = "回复的内容不能为空")
    @ApiModelProperty("回复的内容")
    private String replyContent;

    @NotNull(message = "词列表不能为空")
    @ApiModelProperty("词列表")
    private List<String> wordContents;

    @ApiModelProperty("通道id")
    private List<Long> channelIds;

}
