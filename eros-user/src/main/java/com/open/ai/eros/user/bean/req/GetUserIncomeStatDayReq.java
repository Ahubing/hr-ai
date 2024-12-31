package com.open.ai.eros.user.bean.req;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;


@ApiModel("用户收益统计记录的请求类")
@Data
public class GetUserIncomeStatDayReq {

    /**
     * 只有管理员才给查询
     */
    @ApiModelProperty("面具创建用户ID")
    private Long userId;


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


    @ApiModelProperty("收益类型")
    private List<String> types;


}
