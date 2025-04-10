package com.open.ai.eros.creator.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @类名：ShareMaskVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/9 21:15
 */

@ApiModel("分享面具类")
@Data
public class ShareMaskReq {


    @ApiModelProperty("面具id")
    @NotEmpty(message = "id不能为空")
    private Long maskId;


    @ApiModelProperty("标题")
    @NotEmpty(message = "标题不能为空")
    private String title;


    @ApiModelProperty("开始聊天id")
    private Long startChatId;


    @ApiModelProperty("开始聊天id")
    private Long endChatId;


}
