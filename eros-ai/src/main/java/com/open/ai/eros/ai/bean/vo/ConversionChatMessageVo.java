package com.open.ai.eros.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @类名：ConversionChatMessageVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 19:01
 */
@ApiModel("消息记录列表")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ConversionChatMessageVo {


    @ApiModelProperty("是否为最后一页")
    private boolean lastPage;

    @ApiModelProperty("对话消息列表")
    private List<ChatMessageVo> chatMessageVos;


}
