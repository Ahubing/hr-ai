package com.open.ai.eros.creator.bean.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 面具消耗的日统计表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-12
 */
@ApiModel("面具日统计信息")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MaskStatDayVo implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * 统计的天 格式 yyyyMMdd
     */
    @ApiModelProperty("统计天")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD)
    private LocalDateTime statsDay;

    /**
     * 面具作者ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 面具id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long maskId;


    /**
     * 面具名称
     */
    @ApiModelProperty("面具名称")
    private String maskName;

    /**
     * 消费ai额度 单位美元
     */
    @ApiModelProperty("ai额度")
    private String cost;

    /**
     * 记录条数
     */
    @ApiModelProperty("条数")
    private Long recordCount;

    /**
     * 积分
     */
    @ApiModelProperty("积分")
    private String costPoints;


}
