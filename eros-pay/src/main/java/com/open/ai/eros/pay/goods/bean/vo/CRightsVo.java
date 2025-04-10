package com.open.ai.eros.pay.goods.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权益
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */

@ApiModel("c端权益信息类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CRightsVo implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * 权益名称
     */
    @ApiModelProperty("权益名称")
    private String name;

    /**
     * 权益的 次数 余额
     */
    @ApiModelProperty("权益具体值")
    private String rightsValue;


    /**
     * 更新的规则
     */
    @ApiModelProperty("规则")
    private CRightsRuleVo rule;

    /**
     * 可以使用的模型   单个
     */
    @ApiModelProperty("可使用模型")
    private String canUseModel;

    /**
     * 说明
     */
    @ApiModelProperty("说明")
    private String intro;


    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


    /**
     * 有效期 小时
     */
    @ApiModelProperty("有效期")
    private String effectiveTime;

    @ApiModelProperty("有效期描述")
    private String effectiveTimeDesc;

    /**
     * 类型  @see com.open.ai.eros.db.constants.RightsTypeEnum
     */
    @ApiModelProperty("权益类型")
    private String type;

    @ApiModelProperty("权益类型描述")
    private String typeDesc;


    /**
     * 是否可累加  1：可累加 2：不可累加
     */
    @ApiModelProperty("是否可累加")
    private Integer canAdd;


}
