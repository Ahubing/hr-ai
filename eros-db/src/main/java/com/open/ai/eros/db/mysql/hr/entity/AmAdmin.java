package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.open.ai.eros.db.mysql.user.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_admin")
public class AmAdmin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 公司名
     */
    private String company;

    private String mobile;

    private String email;

    /**
     * 加密盐
     */
    private String salt;

    /**
     * 状态。0未启用，1禁用，2启用
     */
    private Integer status;

    /**
     * 特殊权限
     */
    private String specialPermission;

    /**
     * 最近3个登陆ip，json保存
     */
    private String lastLoginClientIp;

    /**
     * 最近3次登陆时间，json
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     * @return
     */
    private Long creatorId;

    /**
     * 角色
     * @return
     */
    private String role;

    private String slackOff;


    /**
     * 创建时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    private LocalDateTime updateTime;


    public User convertToUser() {
        User user = new User();
        user.setId(id);
        user.setUserName(username);
        user.setPassword(password);
        user.setEmail(email);
        // todo 特殊处理 esc
        user.setStatus(status == 2 ? "ACTIVE" : "DISABLED");
        user.setRole("all".equals(specialPermission) ? "system" : "user");//有特殊权限默认系统管理员。否则，普通用户
        return user;
    }
}
