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
public class MaskStatCountVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 总面具数
     */
    @ApiModelProperty("总面具数")
    private Long maskSum;

    /**
     * 历史使用条数
     */
    @ApiModelProperty("历史使用条数")
    private Long historyRecordCount;

    /**
     * 今天使用条数
     */
    @ApiModelProperty("今天使用条数")
    private Long todayRecordCount;

    /**
     * 获取全部用户数
     */
    @ApiModelProperty("获取全部用户数")
    private Long allUserSum;

    /**
     * 获取全部创作者数
     */
    @ApiModelProperty("获取全部创作者数")
    private Long allCreatorSum;

    /**
     * 获取普通用户数
     */
    @ApiModelProperty("获取全部创作者数")
    private Long allCommonSum;


    /**
     * 获取今日注册数
     */
    @ApiModelProperty("获取今日注册数")
    private Long todayRegister;


}
