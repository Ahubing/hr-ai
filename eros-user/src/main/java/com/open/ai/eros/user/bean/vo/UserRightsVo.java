package com.open.ai.eros.user.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 用户权益
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@ApiModel("用户权益类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserRightsVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("权益名称")
    private String name;

    /**
     * 可以使用的模型
     */
    @ApiModelProperty("可以使用的模型")
    private String canUseModel;


    /**
     * 已使用的权益量级
     *
     */
    @ApiModelProperty("已使用的权益量级")
    private String usedRightsValue;


    /**
     * 可使用的总量
     *
     */
    @ApiModelProperty("可使用的总量")
    private String totalRightsValue;



    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 有效开始时间
     */
    @ApiModelProperty("有效开始时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime effectiveStartTime;


    /**
     * 过期时间
     */
    @ApiModelProperty("过期时间")
    private String expireTime;


    /**
     * 有效结束时间
     */
    @ApiModelProperty("有效结束时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime effectiveEndTime;


    /**
     * 类型  @see com.open.ai.eros.db.constants.RightsTypeEnum
     */
    @ApiModelProperty("权益类型")
    private String type;

    @ApiModelProperty("权益类型描述")
    private String typeDesc;

    /**
     * 状态 1 生效中  2 已失效 3：已使用
     */
    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("状态描述")
    private String statusDesc;

}
