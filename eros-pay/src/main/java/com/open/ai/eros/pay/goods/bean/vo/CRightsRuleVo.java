package com.open.ai.eros.pay.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

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
@ApiModel("权益规则类")
@Data
public class CRightsRuleVo {

    /**
     * 更新规则
     */
    @ApiModelProperty("更新规则")
    private List<String> rule;

    /**
     * 每次更新的值
     */
    @ApiModelProperty("每次更新的值")
    private String everyUpdateNumber;


}
