package com.open.hr.ai.bean.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 简历
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmResumeVo  {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 当前登录的总账号的id
     */
    @ApiModelProperty(value = "当前登录的总账号的id")
    private Long adminId;

    /**
     * 账号id  local_accounts的id
     */
    @ApiModelProperty(value = "账号id  local_accounts的id")
    private String accountId;

    /**
     * 用户信息加密id
     */
    @ApiModelProperty(value = "用户信息加密id")
    private String encryptGeekId;

    /**
     * 脚本返回的用户id
     */
    @ApiModelProperty(value = "脚本返回的用户id")
    private String uid;

    /**
     * 类型。0初筛 1邀约跟进，2面试安排 3已发offer 4已入职  5全部
     */
    @ApiModelProperty(value = "类型。0初筛 1邀约跟进，2面试安排 3已发offer 4已入职  5全部")
    private Integer type;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 所在公司/曾就职的公司
     */
    @ApiModelProperty(value = "所在公司/曾就职的公司")
    private String company;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市")
    private String city;

    /**
     * 应聘的职位
     */
    @ApiModelProperty(value = "应聘职位")
    private String position;

    /**
     * 期望的职位
     */
    @ApiModelProperty(value = "期望的职位")
    private String expectPosition;

    /**
     * 0女，1男
     */
    @ApiModelProperty(value = "0女，1男")
    private Integer gender;

    /**
     * 薪资
     */
    @ApiModelProperty(value = "薪资")
    private String salary;

    /**
     * 平台，来源
     */
    @ApiModelProperty(value = "平台，来源")
    private String platform;


    /**
     * 教育学历
     */
    @ApiModelProperty(value = "教育学历")
    private JSONArray education;


    /**
     * 项目经验。json字符串保存
     */
    @ApiModelProperty(value = "项目经验。json字符串保存")
    private JSONArray projects;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /**
     * 经验。json字符串保存
     */
    @ApiModelProperty(value = "经验。json字符串保存")
    private JSONArray experiences;

    /**
     * 绑定的岗位id
     */
    @ApiModelProperty(value = "绑定的岗位id")
    private Integer postId;



    /**
     * 申请状态
     */
    @ApiModelProperty(value = "申请状态")
    private String applyStatus;

    /**
     * 招聘信息
     */
    @ApiModelProperty(value = "招聘信息")
    private JSONObject zpData;



    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 手机
     */
    @ApiModelProperty(value = "手机")
    private String phone;

    /**
     * 微信
     */
    @ApiModelProperty(value = "微信")
    private String wechat;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "工资的低位")
    private Integer lowSalary;

    @ApiModelProperty(value = "工资的高位")
    private Integer highSalary;

    /**
     * 工作年限
     */
    @ApiModelProperty(value = "工作年限")
    private Integer workYears;

    /**
     * 附件简历
     */
    @ApiModelProperty(value = "附件简历")
    private String attachmentResume;


    /**
     * 技能
     */
    @ApiModelProperty(value = "技能")
    private String skills;


    /**
     * 招聘者id
     */
    @ApiModelProperty(value = "招聘者id")
    private String recruiterId;

    @ApiModelProperty(value = "分数")
    private BigDecimal score;

    /**
     * 1、 系统自动获取
     * 2、用户自定义上传
     */
    private Integer resumeType;

    /**
     * 胜任力模型数据
     */
    private JSONObject competencyModel;


}
