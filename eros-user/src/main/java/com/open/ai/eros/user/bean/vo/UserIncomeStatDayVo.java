package com.open.ai.eros.user.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户收益日统计表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */

@ApiModel("日收益实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserIncomeStatDayVo implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * 收益类型
     */
    @ApiModelProperty("收益类型")
    private String type;

    /**
     * 统计天
     */
    @ApiModelProperty("统计天")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime statDay;

    /**
     * 收益
     */
    @ApiModelProperty("收益")
    private String income;


}
