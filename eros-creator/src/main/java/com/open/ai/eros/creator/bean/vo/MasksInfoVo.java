package com.open.ai.eros.creator.bean.vo;

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
public class MasksInfoVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 面具个数
     */
    @ApiModelProperty("面具个数")
    private Integer maskCount;

    /**
     * 名下面具总使用记录条数
     */
    @ApiModelProperty("使用记录条数")
    private Long recordCount;


    /**
     * 名下面具总使用人数
     */
    @ApiModelProperty("使用面具人数")
    private Long usePeopleCount;

    /**
     * 面具总收入
     */
    @ApiModelProperty("面具总收入")
    private String costPoints;


}
