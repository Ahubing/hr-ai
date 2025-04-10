package com.open.ai.eros.permission.bean.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PermissionVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 权限ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 权限英文
     */
    private String permission;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限类型 ，1 操作权限 2 页面模块权限
     */
    private Integer permissionType;

    /**
     * 权限创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 权限修改时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime modifyTime;

}
