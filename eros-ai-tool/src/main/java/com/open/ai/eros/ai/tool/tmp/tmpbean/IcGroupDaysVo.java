package com.open.ai.eros.ai.tool.tmp.tmpbean;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

//群面日历
@Data
@AllArgsConstructor
public class IcGroupDaysVo {

    @ApiModelProperty("日期")
    private LocalDate localDate;

    @ApiModelProperty("上午群面信息")
    private Map<String,Integer> morningCount;

    @ApiModelProperty("下午群面信息")
    private Map<String,Integer> afternoonCount;

}
