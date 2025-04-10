package com.open.ai.eros.db.mysql.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 角色权限表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-08
 */
@ApiModel("用户角色权限表")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RolePermissionVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 权限类型 ，1 操作权限 2 页面模块权限
     */
    private Integer permissionType;

    /**
     * 权限英文
     */
    private String permission;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 角色
     */
    private String role;

    /**
     * 权限ID
     */
    private Long permissionId;

}
