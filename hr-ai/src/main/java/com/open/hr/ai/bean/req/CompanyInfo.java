package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Date 2025/2/9 15:10
 */
@Data
public class CompanyInfo {

    @ApiModelProperty(value = "公司名称", required = true)
    @NotEmpty(message = "公司名称不能为空")
    private String company;

    @ApiModelProperty(value = "行业领域", required = true)
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

    @ApiModelProperty(value = "工作地点", required = true)
    @NotEmpty(message = "工作地点不能为空")
    private String workLocation;

    @ApiModelProperty(value = "工作时间", required = true)
    @NotEmpty(message = "工作时间不能为空")
    private String workTime;

    @ApiModelProperty(value = "最短工作时间要求", required = true)
    @NotEmpty(message = "最短工作时间要求不能为空")
    private String workMiniTime;


    @ApiModelProperty(value = "紧急程度", required = true)
    @NotEmpty(message = "紧急程度不能为空")
    private String emergencyDegree;



    @ApiModelProperty(value = "职位类型", required = true)
    @NotEmpty(message = "职位类型不能为空")
    private String jobTypeName;



    @ApiModelProperty(value = "薪资范围", required = true)
    @NotEmpty(message = "薪资范围不能为空")
    private String salaryDesc;


    /**
     * 其他要求
     */
    @ApiModelProperty(value = "其他要求")
    private String otherArgue;

    /**
     * 招聘人数
     */
    @ApiModelProperty(value = "招聘人数")
    private Integer recruitingNumbers;


    @ApiModelProperty(value = "工作经验", required = true)
    @NotEmpty(message = "工作经验不能为空")
    private String experienceName;

    @ApiModelProperty(value = "职位名称", required = true)
    @NotEmpty(message = "职位名称不能为空")
    private String jobName;

    @ApiModelProperty(value = "工作城市", required = true)
    @NotEmpty(message = "工作城市不能为空")
    private String locationName;


    @ApiModelProperty(value = "专业技能", required = true)
    @NotEmpty(message = "专业技能不能为空")
    private String skillRequire;

    @ApiModelProperty(value = "degreeName", required = true)
    @NotEmpty(message = "degreeName不能为空")
    private String degreeName;

}
