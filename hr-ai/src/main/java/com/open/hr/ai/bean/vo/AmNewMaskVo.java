package com.open.hr.ai.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import com.open.hr.ai.bean.req.CompanyInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 面具
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmNewMaskVo {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 面具名称
     */
    @ApiModelProperty("面具名称")
    private String name;

    /**
     * 面具类别 普通面具：mask   分析: share_mask
     */
    @ApiModelProperty("面具类别")
    private String type;

    /**
     * 模型来源  aws  az gpt  claude
     */
    @ApiModelProperty("模型模版")
    private List<String> templateModel;

    /**
     * 面具的简单说明
     */
    @ApiModelProperty("面具的简单说明")
    private String intro;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long adminId;

    /**
     * 记忆上下文条数
     */
    @ApiModelProperty("记忆上下文条数")
    private Integer contentsNumber;

    /**
     * 面具的标签   逗号分开  游戏,性感
     */
    @ApiModelProperty("面具的标签")
    private List<String> tags;

    /**
     * 状态  1 发布 2 待发布
     */
    @ApiModelProperty("状态 1 发布 -1 删除")
    private Integer status;


    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime updateTime;

    /**
     * 背景图
     */
    @ApiModelProperty("背景图")
    private String avatar;



    /**
     * 公司的基本信息
     */
    private CompanyInfo companyInfo;


    /**
     * 差异化优势开启后,需要填写
     *
     * DifferentiationAdvantage
     * 面试地点
     *
     */
    private Boolean differentiatedAdvantages;

    /**
     * 面试信息
     */
    private Boolean openInterview;

    /**
     * 面试信息开关后, 需要填写面试地点
     */
    private String interviewAddress;

    /**
     * 其他要求
     */
    private String otherArgue;

    /**
     * 其他招聘信息
     */
    private String otherRecruitmentInfo;

    /**
     * 智能交互指令
     */
    private String style;




}
