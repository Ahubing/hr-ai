package com.open.ai.eros.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ApiModel("可用时间查询")
@Data
@AllArgsConstructor
public class IcSpareTimeReq {

    @NotNull(message = "面具id不能为空")
    @ApiModelProperty(value = "面具id",required = true)
    private Long maskId;

    @NotNull(message = "起始时间不能为空")
    @ApiModelProperty(value = "起始时间",required = true)
    private LocalDateTime startTime;

    @NotNull(message = "截止时间不能为空")
    @ApiModelProperty(value = "截止时间",required = true)
    private LocalDateTime endTime;
}
