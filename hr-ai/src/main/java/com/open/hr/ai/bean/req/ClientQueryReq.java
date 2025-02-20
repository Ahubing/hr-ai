package com.open.hr.ai.bean.req;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author 
 * @Date 2025/1/7 20:54
 */
@Data
public class ClientQueryReq {

    /**
     * 与python定义的字段一致
     * qr_code
     */
    @ApiModelProperty(value = "user_id")
    @NotEmpty(message = "user_id不能为空")
    private String user_id;


}
