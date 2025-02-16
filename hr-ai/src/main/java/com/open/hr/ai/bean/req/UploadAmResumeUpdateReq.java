package com.open.hr.ai.bean.req;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户自定义上传简历
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-16
 */
@Data
public class UploadAmResumeUpdateReq {


    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    private Integer id;

    /**
     * 当前登录的总账号的id
     */
    private Long adminId;


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
    @ApiModelProperty(value = "应聘的职位")
    private String position;


    /**
     * 期望职位
     */
    @ApiModelProperty(value = "期望职位")
    private String expectPosition;


    /**
     * 0女，1男
     */
    @ApiModelProperty(value = "女，男")
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
    @ApiModelProperty("申请状态")
    private String applyStatus;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 微信号
     */
    @ApiModelProperty(value = "微信号")
    private String wechat;

    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 工作年限
     */
    @ApiModelProperty(value = "工作年限")
    private Integer workYears;

    /**
     * 在线简历
     */
    @ApiModelProperty(value = "在线简历")
    private String originalUrl;

    /**
     * 项目经验
     */
    @ApiModelProperty(value = "项目经验")
    private JSONArray projects;

    /**
     * 技能
     */
    @ApiModelProperty(value = "技能")
    private String skills;

    /**
     * 低薪资
     */
    @ApiModelProperty(value = "低薪资位")
    private Integer lowSalary;

    /**
     * 高薪资
     */
    @ApiModelProperty(value = "高薪资位")
    private Integer highSalary;

    /**
     * 在线简历url 或 附件简历
     */
    @ApiModelProperty(value = "在线简历 或 附件简历")
    private String attachmentResume;


}
