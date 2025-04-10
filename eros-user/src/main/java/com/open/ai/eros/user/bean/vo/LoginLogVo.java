package com.open.ai.eros.user.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志---返回类
 *
 * @author Eros-AI
 * @since 2024-08-13
 */
@Data
public class LoginLogVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志记录ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime loginTime;

    /**
     * 登录IP地址
     */
    private String loginIp;

}
