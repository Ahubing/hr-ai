package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author liuzilin
 * @Date 2025/2/9 15:10
 */
@Data
public class CompanyInfo {

    @ApiModelProperty(value = "公司名称")
    @NotEmpty(message = "公司名称不能为空")
    private String company;

    @ApiModelProperty(value = "行业领域")
    @NotEmpty(message = "行业领域不能为空")
    private String area;

    @NotEmpty(message = "成立时间")
    private String establishedTime;

    @NotEmpty(message = "公司规模")
    private String scale;

    @ApiModelProperty(value = "总部地点")
    private String headquartersLocation;

    @ApiModelProperty(value = "官方网站")
    private String officialWebsite;

//
//    private String positionName;
//
//    private String city;

    @ApiModelProperty(value = "工作地点")
    @NotEmpty(message = "工作地点不能为空")
    private String workLocation;

    @ApiModelProperty(value = "工作时间")
    @NotEmpty(message = "工作时间不能为空")
    private String workTime;

    @ApiModelProperty(value = "最短工作时间要求")
    @NotEmpty(message = "最短工作时间要求不能为空")
    private String workMiniTime;


    @ApiModelProperty(value = "紧急程度")
    @NotEmpty(message = "紧急程度不能为空")
    private String emergencyDegree;

}
