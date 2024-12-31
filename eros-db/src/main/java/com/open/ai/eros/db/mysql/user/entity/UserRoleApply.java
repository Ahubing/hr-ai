package com.open.ai.eros.db.mysql.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色申请表，存储用户的角色申请信息
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_role_apply")
public class UserRoleApply implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 申请记录ID
     */
      private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 申请的角色
     */
    private String requestedRole;

    private String extra;

    private String concat;

    /**
     * 申请状态
     */
    private String status;

    /**
     * 审批管理员ID
     */
    private Long reviewedBy;

    /**
     * 审批完成时间，不论结果是通过还是拒绝
     */
    private LocalDateTime reviewedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


}
