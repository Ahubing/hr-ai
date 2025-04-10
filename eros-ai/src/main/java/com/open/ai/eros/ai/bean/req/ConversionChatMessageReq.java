package com.open.ai.eros.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @类名：ConversionChatMessageReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 14:29
 */
@ApiModel("获取会话消息的实体类")
@Data
public class ConversionChatMessageReq {

    @ApiModelProperty("会话Id")
    @NotEmpty(message = "会话id不能为空")
    private String conversionId;

    @ApiModelProperty("分享Id")
    private Long shareMaskId;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("页数")
    private Integer pageSize;
}
