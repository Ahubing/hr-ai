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
 * 简历
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_resume")
public class AmResume implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 当前登录的总账号的id
     */
    private Long adminId;

    /**
     * 账号id  local_accounts的id
     */
    private String accountId;

    /**
     * 用户信息加密id
     */
    @TableField("encryptGeekId")
    private String encryptGeekId;

    /**
     * 脚本返回的用户id
     */
    private String uid;

    /**
     * 类型。0初筛 1邀约跟进，2面试安排 3已发offer 4已入职  5全部
     */
    private Integer type;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 所在公司/曾就职的公司
     */
    private String company;

    /**
     * 城市
     */
    private String city;

    /**
     * 找的职位
     */
    private String position;

    /**
     * 0女，1男
     */
    private Integer gender;

    /**
     * 薪资
     */
    private String salary;

    /**
     * 平台，来源
     */
    private String platform;

    /**
     * 工作年限
     */
    @TableField("workYear")
    private String workYear;

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
     * 招聘的职位目录
     */
    @TableField("positionCategory")
    private String positionCategory;

    /**
     * 工资/薪资
     */
    @TableField("jobSalary")
    private String jobSalary;

    /**
     * 沟通说明
     */
    @TableField("bottomText")
    private String bottomText;

    /**
     * 申请状态
     */
    @TableField("applyStatus")
    private String applyStatus;

    /**
     * 是否有点击拿简历;0否，1是
     */
    private Boolean isClick;

    /**
     * 招聘信息
     */
    @TableField("zpData")
    private String zpData;

    /**
     * 求职期望等，由脚本返回的字段直接保存
     */
    private String content1;

    /**
     * 毕业于 xxx专业
     */
    private String content2;

    /**
     * 更多内容说明
     */
    private String content3;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 手机
     */
    private String phone;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 邮箱
     */
    private String email;


}
