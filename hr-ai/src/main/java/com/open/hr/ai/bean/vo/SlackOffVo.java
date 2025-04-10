package com.open.hr.ai.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Date 2025/3/6 21:56
 */
@Data
public class SlackOffVo {
    @ApiModelProperty("工作强度")
    private Integer slackOff;

    @ApiModelProperty("工作开关")
    private Integer slackSwitch;

    @ApiModelProperty("工作时间")
    private List<WorkTimeVo> workTime;
}
