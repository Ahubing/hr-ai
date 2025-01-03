package com.open.ai.eros.db.mysql.hr.entity;

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
 * 兑换码
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mini_uni_user_exchange_code")
public class MiniUniUserExchangeCode implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer adminId;

    /**
     * 兑换码
     */
    private String code;

    /**
     * 有效期
     */
    private LocalDateTime endDate;

    /**
     * 时长
     */
    private Integer days;

    /**
     * 状态，0未使用，1已使用
     */
    private Boolean status;

    /**
     * 使用者用户id
     */
    private Integer usedUid;

    /**
     * 使用日期
     */
    private LocalDateTime usedDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
