package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "方案名不能为空")
    @ApiModelProperty(value = " 方案名", required = true, notes = "方案名不能为空")
    private String name;

    /**
     * 关联的职位ids,用英文逗号分割
     */
    @ApiModelProperty(value = " 关联的职位ids", required = false, notes = "关联的职位ids,是一个id集合, 用英文逗号分割")
    private String positionIds;

    /**
     * 方案类型。0为boss从未回复，1为boss询问信息后
     */
    @ApiModelProperty(value = " 方案类型", required = false, notes = "0为boss从未回复，1为boss询问信息后")
    private Integer type;

    /**
     * 男士称呼别名
     */
    @ApiModelProperty(value = " 男士称呼别名", required = false, notes = "男士称呼别名")
    private String manAlias;

    /**
     * 女士称呼别名
     */
    @ApiModelProperty(value = " 女士称呼别名", required = false, notes = "女士称呼别名")
    private String womanAlias;

    /**
     * 复聊持续天数，单位：天
     */
    @ApiModelProperty(value = " 复聊持续天数", required = false, notes = "复聊持续天数，单位：天")
    private Integer rechatDuration;


}
