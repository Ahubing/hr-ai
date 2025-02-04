package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Date 2025/1/4 21:17
 */
@Data
public class SearchAmResumeReq {

    /**
     * 选填，关键词、姓名手机号或者微信号
     */
    @ApiModelProperty("职位id")
    private Integer position_id;

    @ApiModelProperty("学历 1：大专 2：本科 3：硕士 4：博士")
    private Integer education ;

    @ApiModelProperty("工作年限 1：应届 2：1-3年 3：3-5年 4：5-10年 5：10年以上")
    private Integer experience;

    @ApiModelProperty("技能要求")
    private String tec;


}
