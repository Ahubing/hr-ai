package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class MiniUniUserExchangeCodeVo {

    private Integer id;

    private Long adminId;

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
    private Integer months;

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
