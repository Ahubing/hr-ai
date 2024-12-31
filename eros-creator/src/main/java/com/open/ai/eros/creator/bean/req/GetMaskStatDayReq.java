package com.open.ai.eros.creator.bean.req;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@ApiModel("面具统计记录的请求类")
@Data
public class GetMaskStatDayReq {

    /**
     * 只有管理员才给查询
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("面具id")
    private Long maskId;

    @ApiModelProperty("开始时间")
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    @ApiModelProperty("结束时间")
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    @Max(50)
    @Min(1)
    @ApiModelProperty("页数")
    private Integer page = 1;


    @Max(50)
    @Min(10)
    @ApiModelProperty("分页size")
    private Integer pageSize = 10;

}
