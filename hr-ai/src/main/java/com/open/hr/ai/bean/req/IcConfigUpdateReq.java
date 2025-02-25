package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Time;

@Data
public class IcConfigUpdateReq {

    @ApiModelProperty(value = "id",required = true)
    private String id;

    /**
     * 1-周一，2-周二...7周日
     */
    @ApiModelProperty("1-周一，2-周二...7周日，以此类推")
    private String dayOfWeek;

    /**
     * 上午起始时间
     */
    @ApiModelProperty("上午起始时间")
    private Time morningStartTime;

    /**
     * 上午截止时间
     */
    @ApiModelProperty("上午截止时间")
    private Time morningEndTime;

    /**
     * 下午起始时间
     */
    @ApiModelProperty("下午起始时间")
    private Time afternoonStartTime;

    /**
     * 下午截止时间
     */
    @ApiModelProperty("下午截止时间")
    private Time afternoonEndTime;
}
