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
 * 权益类型
 *
 * @类名：RightsRuleVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/20 23:36
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("权益类型")
@Data
public class BRightsTypeVo {

    /**
     * 权益类型
     */
    @ApiModelProperty("权益类型")
    private String type;

    /**
     *
     */
    @ApiModelProperty("权益类型描述")
    private String typeDesc;


}
