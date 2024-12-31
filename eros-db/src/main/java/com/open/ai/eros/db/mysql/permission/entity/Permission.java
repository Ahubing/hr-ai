package com.open.ai.eros.db.mysql.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("permission")
public class Permission implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 权限ID
     */
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
    private LocalDateTime createTime;

    /**
     * 权限修改时间
     */
    private LocalDateTime modifyTime;

}
