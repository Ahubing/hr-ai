package com.open.hr.ai.bean.req;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

/**
 * @Author 
 * @Date 2025/1/7 20:54
 */
@Data
public class ClientFinishTaskReq {

    /**
     * 与python定义的字段一致
     * qr_code
     */
    @ApiModelProperty(value = "boss_id")
    private String boss_id;

    /**
     * 与python定义的字段一致
     * expires
     */
    @ApiModelProperty(value = "task_type")
    private String task_type;


    /**
     * 与python定义的字段一致
     * task_id
     */
    @ApiModelProperty(value = "task_id")
    private String task_id;


    /**
     * 与python定义的字段一致
     * success
     */
    @ApiModelProperty(value = "success")
    private Boolean success;

    /**
     * 与python定义的字段一致
     * reason
     */
    @ApiModelProperty(value = "reason")
    private String reason;


    @ApiModelProperty(value = "data")
    private JSONObject data;


}
