package com.open.ai.eros.pay.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *
 *
 *
 * 权益规则
 *
 * @类名：RightsRuleVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/20 23:36
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("权益规则")
@Data
public class BRightsRuleVo {

    /**
     * 权益类型
     */
    @ApiModelProperty("权益规则")
    private String rule;

    /**
     *
     */
    @ApiModelProperty("权益规则描述")
    private String ruleDesc;


}
