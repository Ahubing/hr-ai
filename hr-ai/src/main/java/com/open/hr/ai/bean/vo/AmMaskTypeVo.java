package com.open.hr.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @类名：MaskTypeVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/14 21:45
 */

@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("面具分类类")
@Data
public class AmMaskTypeVo {

    @ApiModelProperty("面具分类type")
    private String type;

    @ApiModelProperty("描述")
    private String desc;
}
