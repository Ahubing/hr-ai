package com.open.ai.eros.db.mysql.permission.entity;

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
 * 角色权限表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("role_permission")
public class RolePermission implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 角色ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色
     */
    private String role;

    /**
     * 权限ID
     */
    private Long permissionId;

    /**
     * 状态 0 有效 0 无效
     */
    private Integer status;

    /**
     * 权限创建时间
     */
    private LocalDateTime createTime;

    /**
     * 权限修改时间
     */
    private LocalDateTime modifyTime;


}
