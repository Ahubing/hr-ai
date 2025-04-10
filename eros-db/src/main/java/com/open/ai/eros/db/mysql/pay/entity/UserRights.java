package com.open.ai.eros.db.mysql.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_rights")
public class UserRights implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    private Long userId;



    /**
     * 可以使用的模型
     */
    private String canUseModel;


    /**
     * 权益类型
     */
    private String type;


    /**
     * 权益的快照版本id
     */
    private Long rightsSnapshotId;


    /**
     * 已使用的权益量级
     *
     */
    private Long usedRightsValue;


    /**
     * 可使用的总量
     *
     */
    private Long totalRightsValue;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


    /**
     * 有效开始时间
     */
    private LocalDateTime effectiveStartTime;


    /**
     * 状态 1 生效中  2 已失效 3：已使用
     */
    private Integer status;


    /**
     * 有效结束时间
     */
    private LocalDateTime effectiveEndTime;


}
