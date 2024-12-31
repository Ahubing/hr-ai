package com.open.ai.eros.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @Author liuzilin
 * @Date 2024/9/20 22:32
 */
@ApiModel("消息记录列表")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmbeddingSearchResultVo {
    private String message;
}
