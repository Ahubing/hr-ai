package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Date 2025/2/9 15:10
 */
@Data
public class DifferentiationAdvantage {

    @ApiModelProperty(value = "薪酬福利")
    private String salaryAndWelfare;
    @ApiModelProperty(value = "职业发展")
    private String careerDevelopment;
    @ApiModelProperty(value = "工作环境")
    private String workEnvironment;
    @ApiModelProperty(value = "公司文化")
    private String welfare;

}
