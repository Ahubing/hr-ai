package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.ai.bean.vo.IcConfigVo;
import com.open.ai.eros.common.util.DateUtils;
import com.open.hr.ai.bean.req.CompanyInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @ApiModelProperty(value = "是否开启 差异化优势")
    private Boolean differentiatedAdvantagesSwitch;

    /**
     * 面试信息
     */
    @ApiModelProperty(value = "是否开启 面试信息")
    private Boolean openInterviewSwitch;

    /**
     * 面试信息开关后, 需要填写面试地点
     */
    @ApiModelProperty(value = "面试地点")
    private String interviewAddress;

    /**
     * 其他要求
     */
    @ApiModelProperty(value = "其他要求")
    private String otherArgue;

    /**
     * 其他招聘信息
     */
    @ApiModelProperty(value = "其他招聘信息")
    private String otherRecruitmentInfo;

    /**
     * 智能交互指令
     */
    @ApiModelProperty(value = "智能交互指令")
    private String style;


    /**
     * 沟通脚本
     */
    @ApiModelProperty(value = "沟通脚本")
    private String CommunicationScript;



    /**
     * 过滤词
     */
    @ApiModelProperty(value = "过滤用户的词")
    private String filterWords;

    /**
     * 示例对话
     */
    @ApiModelProperty(value = "示例对话")
    private String exampleDialogues;



    @ApiModelProperty("流程code")
    private Integer code;

    /**
     * 打开交换微信
     */
    @ApiModelProperty(value = "是否开启交换微信")
    private Boolean openExchangeWeChat;

    /**
     * 交换手机号
     */
    @ApiModelProperty(value = "是否开启交换手机号")
    private Boolean openExchangePhone;

    /**
     * 交换附件简历
     */
    @ApiModelProperty(value = "是否交换附件简历")
    private Boolean openExchangeAttachmentResume;

    /**
     * single-单面，group-群面
     */
    @ApiModelProperty(value = "single-单面，group-群面")
    private String interviewType;

    /**
     * 是否跳过节假日，1-是，2-否
     */
    @ApiModelProperty(value = "是否跳过节假日，1-是，2-否")
    private Integer skipHolidayStatus;

    @ApiModelProperty(value = "面试时段配置",required = true)
    private List<IcConfigVo> icConfigVos = new ArrayList<>();

    /**
     * 打招呼话术
     */
    @ApiModelProperty(value = "打招呼话术")
    private String greetMessage;

    /**
     * 关联的模型ID
     */
    private Long modelId;

/*    *//**
     * 关联的模型d名称
     *//*
    @TableField(exist = false) // **表示这个字段不存数据库**
    private String modelName;*/

}
