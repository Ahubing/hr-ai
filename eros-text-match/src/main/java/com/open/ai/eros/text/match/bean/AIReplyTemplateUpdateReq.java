package com.open.ai.eros.text.match.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @类名：AIReplyTemplateAddReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/20 1:13
 */
@ApiModel("自动回复模版修改类")
@Data
public class AIReplyTemplateUpdateReq {


    @ApiModelProperty("回复模版id")
    private Long id;

    @NotEmpty(message = "回复的内容不能为空")
    @ApiModelProperty("回复的内容")
    private String replyContent;

    @NotEmpty(message = "词列表不能为空")
    @ApiModelProperty("词列表")
    private List<String> wordContents;

    @ApiModelProperty("通道id")
    private List<Long> channelIds;

}
