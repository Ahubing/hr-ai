package com.open.ai.eros.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @类名：ChatConversationResultVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/26 12:37
 */

@ApiModel("会话列表结果类")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
public class ChatConversationResultVo {



    @ApiModelProperty("是否为最后一页")
    private boolean lastPage;

    @ApiModelProperty("会话列表")
    private List<ChatConversationVo> chatConversationVos;

}
