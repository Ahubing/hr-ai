package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author 
 * @Date 2025/1/7 20:54
 */
@Data
public class ClientQrCodeReq {

    /**
     * 与python定义的字段一致
     * qr_code
     */
    @NotEmpty(message = "qr_code不能为空")
    @ApiModelProperty(value = "qr_code")
    private String qr_code;

    /**
     * 与python定义的字段一致
     * expires
     */
    @NotNull(message = "expires不能为空")
    @ApiModelProperty(value = "expires")
    private Long expires;
}
