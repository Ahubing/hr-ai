package com.open.ai.eros.creator.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-13
 */

@ApiModel("面具简单信息类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SimpleMaskVo implements Serializable {

    private static final long serialVersionUID=1L;


    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

    /**
     * 面具名称
     */
    @ApiModelProperty("面具名称")
    private String name;


}
