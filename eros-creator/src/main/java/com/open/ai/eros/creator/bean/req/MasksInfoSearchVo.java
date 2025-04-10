package com.open.ai.eros.creator.bean.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 用户名下面具数据
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-12
 */
@ApiModel("用户名下面具数据")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MasksInfoSearchVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 面具作者ID
     */
    @ApiModelProperty("面具作者ID")
    private Long userId;

    /**
     * 查询时间窗口 -1 查询全部, 0 当天 , 1 本周, 2 这个月
     */
    private Integer timeWindow = -1;


}
