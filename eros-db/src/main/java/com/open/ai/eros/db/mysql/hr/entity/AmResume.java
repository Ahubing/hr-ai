package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.open.ai.eros.common.constants.AmAdminStatusEnums;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
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
     * 应聘职位
     */
    private String  position;

    /**
     * 期望职位
     */
    private String expectPosition;

    /**
     * 0女，1男
     */
    private Integer gender;


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
     * 工作经验。json字符串保存
     */
    private String experiences;


    /**
     * 项目经验。json字符串保存
     */
    private String projects;

    /**
     * 绑定的职位id
     */
    private Integer postId;



    /**
     * 申请状态
     */
    @TableField("applyStatus")
    private String applyStatus;



    /**
     * 招聘信息
     */
    @TableField("zpData")
    private String zpData;


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

    private Integer lowSalary;

    private Integer highSalary;

    /**
     * 工作年限
     */
    private Integer workYears;

    private String attachmentResume;

    private String skills;

    /**
     * 1、 系统自动获取
     * 2、用户自定义上传
     */
    private Integer resumeType;

    /**
     * 胜任力模型数据
     */
    private String competencyModel;

    /**
     *  0离职/离校-正在找工作，1在职/在校-考虑机会，2在职/在校-寻找新工作, -1未知
     */
    private Integer intention;


    /**
     *  0初中及以下，1中专/技校，2高中，3大专，4本科，5硕士，6博士, -1未知
     */
    private Integer degree;

    /**
     *  是否是学生 1是学生 -1 不是
     */
    private Integer isStudent;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 岗位id
     */
    private Integer positionId;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 部门id
     */
    private Integer deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * @param newType 新状态
     * @param isAlUpdate 是否是ai更新
     */
    public void updateType(ReviewStatusEnums newType,Boolean isAlUpdate){
        if(newType == null || newType.getStatus().equals(this.type)){
            return;
        }

        if (!isAlUpdate || (newType.equals(ReviewStatusEnums.ABANDON) || newType.getStatus() > this.type)){
            this.type = newType.getStatus();
        }

    }

}
