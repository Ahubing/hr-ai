package com.open.ai.eros.db.mysql.user.entity;

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
 * 登录日志表，存储用户的登录日志信息
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("login_log")
public class LoginLog implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 日志记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录IP地址
     */
    private String loginIp;

}
