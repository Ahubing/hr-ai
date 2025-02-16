package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户自定义上传简历
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("upload_am_resume")
public class UploadAmResume implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 当前登录的总账号的id
     */
    private Integer adminId;


    /**
     * 类型。0初筛 1邀约跟进，2面试安排 3已发offer 4已入职  5全部
     */
    private Integer type;

    /**
     * 姓名
     */
    private String name;

    /**
     * 所在公司/曾就职的公司
     */
    private String company;

    /**
     * 城市
     */
    private String city;

    /**
     * 应聘的职位
     */
    private String position;

    /**
     * 0女，1男
     */
    private String gender;

    /**
     * 薪资
     */
    private String salary;

    /**
     * 平台，来源
     */
    private String platform;

    /**
     * 教育学历
     */
    private String education;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 经验。json字符串保存
     */
    private String experiences;

    /**
     * 绑定的岗位id
     */
    private Integer postId;


    /**
     * 申请状态
     */
    @TableField("applyStatus")
    private String applyStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 微信号
     */
    private String wechat;

    private String email;

    /**
     * 工作年限
     */
    private Integer workYears;

    /**
     * 在线简历
     */
    private String originalUrl;

    /**
     * 项目经验
     */
    private String projects;

    /**
     * 技能
     */
    private String skills;


}
