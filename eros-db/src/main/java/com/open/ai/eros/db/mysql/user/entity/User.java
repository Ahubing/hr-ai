package com.open.ai.eros.db.mysql.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表，存储用户的基本信息，包括通过谷歌账号登录的用户
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 用户ID
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户密码，谷歌登录用户可为空
     */
    private String password;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 谷歌账号ID，普通注册用户可为空
     */
    private String googleId;

    /**
     * 被邀请代码
     */
    private String invitedCode;

    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * 用户状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
