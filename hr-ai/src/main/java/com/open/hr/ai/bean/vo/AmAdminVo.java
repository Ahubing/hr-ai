package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
public class AmAdminVo {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = false, notes = "账号")
    private String username;


    @ApiModelProperty(value = "邮箱", required = false, notes = "邮箱")
    private String email;


    /**
     * 状态。0未启用，1禁用，2启用
     */
    @ApiModelProperty(value = "状态。0未启用，1禁用，2启用", required = false, notes = "状态。0未启用，1禁用，2启用")
    private Integer status;


    /**
     * 最近3次登陆时间，json
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    @ApiModelProperty(value = "上次登录时间", required = false, notes = "上次登录时间")
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    @ApiModelProperty(value = "创建时间", required = false, notes = "创建时间")
    private LocalDateTime createTime;

    /**
     * 创建人
     *
     * @return
     */
    @ApiModelProperty(value = "创建人", required = false, notes = "创建人")
    private Long creatorId;


    @ApiModelProperty("电话号码")
    private String mobile;

    /**
     * 角色
     *
     * @return
     */
    @ApiModelProperty(value = "角色", required = false, notes = "角色")
    private String role;

}
