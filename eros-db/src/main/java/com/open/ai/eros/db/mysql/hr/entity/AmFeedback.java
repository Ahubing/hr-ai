package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 意见反馈表
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_feedback")
public class AmFeedback implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 反馈自增feedback_id
     */
      @TableId(value = "feedback_id", type = IdType.AUTO)
    private Integer feedbackId;

    /**
     * 反馈人的member_id
     */
    private Integer memberId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 处理状态 1未处理，2已解决，3，驳回
     */
    private Boolean status;

    /**
     * 是否已读 0 否 1 是
     */
    private Boolean isRead;

    /**
     * 驳回原因
     */
    private String remark;

    /**
     * 创建时间
     */
    private Integer createTime;

    /**
     * 处理时间
     */
    private Integer handelTime;


}
