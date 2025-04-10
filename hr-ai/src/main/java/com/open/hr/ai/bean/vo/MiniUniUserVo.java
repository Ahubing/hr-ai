package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class MiniUniUserVo {


    /**
     * 主键
     */
    private Integer id;

    /**
     * 所属的账号
     */
    private Integer adminId;


    /**
     * 姓名
     */
    private String name;

    /**
     * 用户名
     */
    private String username;


    /**
     * 电话
     */
    private String mobile;

    /**
     * 微信
     */
    private String wechat;


    /**
     * 用户状态。0未认证,1审核中，2已认证成功，3认证失败
     */
    private Integer status;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 到期时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime expiredTime;


}
