package com.open.ai.eros.db.mysql.pay.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户权益
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CacheUserRightsVo{

    private Long id;


    /**
     * 类型  @see com.open.ai.eros.db.constants.RightsTypeEnum
     */
    private String type;


    /**
     * 已使用的权益量级
     *
     */
    private Long usedRightsValue;



    /**
     * 可以使用的模型
     */
    private String canUseModel;

    /**
     * 可使用的总量
     *
     */
    private Long totalRightsValue;


    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 有效开始时间
     */
    private Long effectiveStartTime;


    /**
     * 有效结束时间
     */
    private Long effectiveEndTime;


}
