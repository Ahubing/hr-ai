package com.open.ai.eros.pay.goods.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
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

@ApiModel("权益")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RightsVo implements Serializable {

    private static final long serialVersionUID=1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 权益名称
     */
    private String name;

    /**
     * 权益的 次数 余额
     */
    private String rightsValue;


    /**
     * 更新的规则
     */
    private RightsRuleVo rule;



    /**
     * 可以使用的模型   单个
     */
    private String canUseModel;

    /**
     * 说明
     */
    private String intro;


    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


    /**
     * 有效期 小时
     */
    private String effectiveTime;

    private String effectiveTimeDesc;

    /**
     * 类型  @see com.open.ai.eros.db.constants.RightsTypeEnum
     */
    private String type;

    private String typeDesc;

    /**
     * 1:上架 2：下架
     */
    private Integer status;



    /**
     * 是否可累加  1：可累加 2：不可累加
     */
    private Integer canAdd;


}
