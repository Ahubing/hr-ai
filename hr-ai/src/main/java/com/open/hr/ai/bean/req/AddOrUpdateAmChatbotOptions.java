package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddOrUpdateAmChatbotOptions{


    @ApiModelProperty("id")
    private Integer id;

    /**
     * 方案名
     */
    @NotNull(message = "方案名不能为空")
    @ApiModelProperty("方案名")
    private String name;

    /**
     * 关联的职位ids,用英文逗号分割
     */
    @ApiModelProperty("关联的职位ids,用英文逗号分割")
    private String positionIds;

    /**
     * 方案类型。0为boss从未回复，1为boss询问信息后
     */
    @ApiModelProperty("方案类型。0为boss从未回复，1为boss询问信息后")
    private Integer type;

    /**
     * 男士称呼别名
     */
    @ApiModelProperty("男士称呼别名")
    private String manAlias;

    /**
     * 女士称呼别名
     */
    @ApiModelProperty("女士称呼别名")
    private String womanAlias;

    /**
     * 复聊持续天数，单位：天
     */
    @ApiModelProperty("复聊持续天数，单位：天")
    private Integer rechatDuration;

    /**
     * 创建人的id
     */
    @ApiModelProperty("创建人的id")
    private Integer adminId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
