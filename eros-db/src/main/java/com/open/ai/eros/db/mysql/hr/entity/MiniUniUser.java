package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mini_uni_user")
public class MiniUniUser implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属的账号
     */
    private Integer adminId;

    private String miniName;

    /**
     * 姓名
     */
    private String name;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 公司
     */
    private String company;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 微信用户唯一值
     */
    private String openid;

    /**
     * 所属的校园商圈
     */
    private String mid;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 原微信昵称
     */
    private String nickname;

    /**
     * 原微信头像
     */
    private String avatar;

    /**
     * 修改的昵称
     */
    private String fNickname;

    /**
     * 修改的头像
     */
    private String fAvatar;

    /**
     * 第二身份名称
     */
    private String secondName;

    /**
     * 性别，0女，1男
     */
    private Boolean sex;

    /**
     * 用户状态。0未认证,1审核中，2已认证成功，3认证失败
     */
    private Boolean status;

    /**
     * 通知状态。0结果未通知，1已通知，2出错
     */
    private Boolean notify;

    /**
     * 认证失败原因
     */
    private String reason;

    /**
     * 是否拉进黑名单
     */
    private Boolean isBlack;

    /**
     * 禁言结束时间
     */
    private LocalDateTime banEndTime;

    /**
     * 届数
     */
    private String session;

    /**
     * 校区
     */
    private String campus;

    /**
     * 专业介绍
     */
    private String major;

    /**
     * 用户等级，0普通，1其他
     */
    private Boolean level;

    /**
     * 更新时间
     */
    private Integer updateTime;

    /**
     * 创建时间
     */
    private Integer createTime;

    /**
     * 到期时间
     */
    private Integer expiredTime;

    /**
     * 特殊权限
     */
    private String specialPermission;

    /**
     * 第二身份名称
     */
    private String secondNameAvatar;


}
