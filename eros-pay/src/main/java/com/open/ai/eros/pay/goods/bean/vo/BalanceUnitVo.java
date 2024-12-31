package com.open.ai.eros.pay.goods.bean.vo;

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
@ApiModel("金额单位")
@Data
public class BalanceUnitVo {

    @ApiModelProperty("金额单位")
    private String unit;

    @ApiModelProperty("金额单位描述")
    private String desc;
}
