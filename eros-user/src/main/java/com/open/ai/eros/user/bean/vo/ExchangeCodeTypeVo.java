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

import java.time.LocalDateTime;

/**
 * <p>
 * 兑换码类型给b端
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-27
 */
@ApiModel("兑换码类型")
@Data
public class ExchangeCodeTypeVo {


    /**
     * 兑换码类型 type: balance   rights
     */
    @ApiModelProperty("兑换码类型")
    private String type;


    @ApiModelProperty("兑换码类型描述")
    private String desc;


}
