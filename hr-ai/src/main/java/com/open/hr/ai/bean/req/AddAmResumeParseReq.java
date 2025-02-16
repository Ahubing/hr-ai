package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 14:27
 */
@Data
public class AddAmResumeParseReq {


    /**
     * 简历url
     */
    @NotNull(message = "在线简历url word 和 pdf 不能为空")
    @ApiModelProperty(value = "简历url 文件格式word 和 pdf 不能为空", required = true)
    private String resumeUrl;

    /**
     * 职位平台
     */
    @ApiModelProperty(value = "简历来源", required = true)
    private String platForm;


}
