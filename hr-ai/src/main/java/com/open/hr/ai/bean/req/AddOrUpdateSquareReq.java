package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *  角色广场
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddOrUpdateSquareReq {


    private Integer id;



    /**
     * 名称
     */
    @NotEmpty(message = "名称不能为空")
    @ApiModelProperty(value = "名称", required = true, notes = "名称不能为空")
    private String name;

    /**
     * 模型
     */
    @ApiModelProperty(value = "描述", required = true, notes = "描述")
    private String description;

    /**
     * 行业
     */
    @ApiModelProperty(value = "行业", required = false, notes = "行业")
    private String profession;

    /**
     * AI引导prompt
     */
    @ApiModelProperty(value = "关键词", required = false, notes = "关键词，系统关键词在1训练广场列表有返回；英文逗号隔开上传，如：化学,后端,java")
    private String keywords;


}
