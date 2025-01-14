package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 *  用户
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddExchangeCodeReq {



    /**
     * 数量
     */
    @NotNull(message = "数量")
    @ApiModelProperty(value = "数量", required = true)
    private Integer cnt;

    /**
     * 周期
     */
    @NotNull(message = "周期")
    @ApiModelProperty(value = "月份", required = true)
    private Integer months;

    /**
     * 失效时间
     */
    @NotNull(message = "失效时间")
    @ApiModelProperty(value = "失效时间", required = true)
    private LocalDateTime endDate;


}
