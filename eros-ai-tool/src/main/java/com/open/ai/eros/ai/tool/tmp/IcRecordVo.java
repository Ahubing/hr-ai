package com.open.ai.eros.ai.tool.tmp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IcRecordVo {

    private String id;

    /**
     * 受聘者uid
     */
    @ApiModelProperty("受聘者uid")
    private String employeeUid;

    /**
     * 管理员/招聘方id
     */
    @ApiModelProperty("管理员/招聘方id")
    private Long adminId;

    /**
     * 面试开始时间
     */
    @ApiModelProperty("面试开始时间")
    private LocalDateTime startTime;

    /**
     * 面具id
     */
    @ApiModelProperty("面具id")
    private Long maskId;

    /**
     * 面试类型single-单面，group-群面
     */
    @ApiModelProperty("面试类型single-单面，group-群面")
    private String interviewType;

    /**
     * 取消状态1-未取消，2-已取消
     */
    @ApiModelProperty("取消状态1-未取消，2-已取消")
    private Integer cancelStatus;

    /**
     * 谁取消了，1-招聘方，2-受聘方
     */
    @ApiModelProperty("谁取消了，1-招聘方，2-受聘方")
    private Integer cancelWho;

    /**
     * 取消时间
     */
    @ApiModelProperty("取消时间")
    private LocalDateTime cancelTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private LocalDateTime modifyTime;
}
